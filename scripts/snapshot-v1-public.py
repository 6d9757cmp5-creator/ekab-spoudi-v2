#!/usr/bin/env python3
"""Snapshot non-sensitive public V1 educational source into V2/v1-archive.

This is a read-only copy of the public old repository, not a live dependency or
an executable module in V2. Existing V2 UI/progress/IPA are never overwritten.
Do not publish the archive through GitHub Pages.
"""
import datetime
import hashlib
import json
from pathlib import Path
import re
import shutil
import subprocess
import sys

if len(sys.argv) != 3:
    raise SystemExit("Usage: snapshot-v1-public.py SOURCE_REPO DEST_ARCHIVE")
src = Path(sys.argv[1]).resolve()
dest = Path(sys.argv[2]).resolve()
if not (src/".git").is_dir():
    raise SystemExit("Missing source Git checkout")
if dest == src or src in dest.parents:
    raise SystemExit("Destination cannot be nested in source")
commit=subprocess.check_output(["git","-C",str(src),"rev-parse","HEAD"],text=True).strip()
tracked=subprocess.check_output(["git","-C",str(src),"ls-files","-z"],stdout=subprocess.PIPE).split(b"\0")
files=[p.decode("utf-8") for p in tracked if p]
# Files can be included in a future revision only after security/license review.
excluded_dirs=(".github/","functions/","workers/","vendor/")
excluded_exact={
 "AGENTS.md","original.pdf","eoppep-source.pdf","ekab-spoudi-safe-updates-v22.zip",
 "docs/APPWRITE.md","docs/AI-GRADING.md",
 "appwrite-client.js","account-ui.js","auth-callback.js",
 "oauth-callback.html","oauth-return.js","cloud-sync.js",
 "sync-codec.js","sync-local.js","sync-model.js",
 "ai-grading-client.js","ai-grading-config.js",
 "questions.json",
}
excluded_prefixes=("selida-","eoppep-page-")
excluded_ext={".env",".key",".pem",".p12",".p8",".mobileprovision",".keystore",".jks"}
secret_patterns=[
 re.compile(rb"-----BEGIN (?:RSA |EC |OPENSSH |DSA )?PRIVATE KEY-----"),
 re.compile(rb"\bgh[pousr]_[A-Za-z0-9_]{30,}"),
 re.compile(rb"\bgithub_pat_[A-Za-z0-9_]{25,}"),
 re.compile(rb"\bsk-(?:proj-)?[A-Za-z0-9_-]{32,}"),
 re.compile(rb"\b(?:AKIA|ASIA)[A-Z0-9]{16}\b"),
]
if dest.exists():
    shutil.rmtree(dest)
dest.mkdir(parents=True)
copied=[]
skipped=[]
def exclude(path):
    name=Path(path).name
    if path in excluded_exact: return "credentials, authentication, cloud sync, or unneeded placeholder"
    if any(path.startswith(d) for d in excluded_dirs): return "backend/workflow/vendor isolated from V2"
    if any(name.startswith(p) for p in excluded_prefixes): return "image scan requires source-rights review"
    if name.startswith(".env") or Path(name).suffix.lower() in excluded_ext: return "configuration or secret material"
    if name.lower().endswith((".zip",".ipa",".apk",".aab",".dmg")): return "old prebuilt distribution"
    if len(path)>220 or any(x in path for x in ("..","\\","//")): return "invalid path"
    return None
for p in files:
    reason=exclude(p)
    path=src/p
    if reason is None and (not path.is_file() or path.is_symlink()):
        reason="not a regular file"
    if reason is None and path.stat().st_size>10*1024*1024:
        reason="oversized or unverified publication rights"
    if reason is None:
        data=path.read_bytes()
        if any(rx.search(data) for rx in secret_patterns):
            reason="possible embedded credential detected; withheld"
    if reason is None:
        target=dest/p
        target.parent.mkdir(parents=True,exist_ok=True)
        target.write_bytes(data)
        copied.append({"path":p,"size":len(data),"sha256":hashlib.sha256(data).hexdigest()})
    else:
        skipped.append({"path":p,"reason":reason})
manifest={
 "source":"public GitHub 6d9757cmp5-creator/ekab-spoudi",
 "source_commit":commit,
 "generated_utc":datetime.datetime.now(datetime.timezone.utc).isoformat(),
 "note":"Archived for V2 migration; not loaded automatically, never overwrites V2 UI.",
 "total_source_files":len(files),
 "copied_files":len(copied),
 "copied_bytes":sum(f["size"] for f in copied),
 "excluded_files":len(skipped),
 "files":copied,
 "excluded":skipped,
 "not_available_from_public_git":[
  "personal study progress and notes saved on devices",
  "private account records, cloud data and passwords",
  "API keys and signing certificates",
  "external live services, third-party media rights and deployed backends",
 ],
}
(dest/"MIGRATION_MANIFEST.json").write_text(json.dumps(manifest,ensure_ascii=False,indent=2),encoding="utf-8")
(dest/"README.md").write_text(
 "# ΕΚΑΒ 1 → ΕΚΑΒ Σπουδή V2: αρχειοθήκη μεταφοράς\n\n"
 "Το περιεχόμενο αυτού του φακέλου προέρχεται από το δημόσιο repository του ΕΚΑΒ 1, "
 "ως ξεχωριστό στιγμιότυπο για τη V2. **Δεν εκτελείται** αυτόματα, "
 "**δεν αλλάζει** το interface και δεν μοιράζεται λογαριασμούς ή πρόοδο.\n\n"
 "Οι ήδη ενεργοποιημένες εκπαιδευτικές εγγραφές συγχρονίζονται χωριστά μέσω "
 "του scripts/import-v1-data.py στην παραγωγή της V2. Οι υπόλοιπες "
 "σελίδες, αρχεία, σενάρια και εκπαιδευτικοί μηχανισμοί παραμένουν εδώ "
 "μέχρι να δοκιμαστούν και να ενσωματωθούν.\n\n"
 "Πλήρη στοιχεία και αιτίες εξαιρέσεων: MIGRATION_MANIFEST.json.\n"
 "Δεδομένα προόδου χρηστών, credentials, backend και αδειοδοτημένο ή "
 "αβέβαιων δικαιωμάτων PDF/σκαναρισμένο περιεχόμενο δεν μεταφέρονται.\n",
 encoding="utf-8")
print(json.dumps({k:manifest[k] for k in ("source_commit","total_source_files","copied_files","copied_bytes","excluded_files")},ensure_ascii=False))
if len(copied)<260:
    raise RuntimeError("Unexpectedly few safe source files. Aborting.")
