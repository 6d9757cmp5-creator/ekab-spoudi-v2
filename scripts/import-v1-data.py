#!/usr/bin/env python3
"""Import only public structured educational data from EKAB 1 into EKAB V2 at build time.
Original V2 UI/JS/CSS and local progress remain unchanged. Source repo is READ-ONLY.
"""
import datetime
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys

if len(sys.argv) != 3:
    raise SystemExit("Usage: import-v1-data.py <v1-checkout> <v2-site-output>")
source = Path(sys.argv[1]).resolve()
site = Path(sys.argv[2]).resolve()
if not source.is_dir() or not site.is_dir():
    raise SystemExit("Missing checkout or site directory")
raw_dir = site / "v1-content" / "raw"
raw_dir.mkdir(parents=True, exist_ok=True)
allowed = [
    "official_questions.json", "eoppep-sos.json", "medical-terms.json",
    "saek-exams-bank.json", "saek-search-A.json", "saek-search-B.json",
    "saek-search-C.json", "saek-search-D.json", "supplementary_exercises.json",
    "old_type_written.json", "extra-exercises-search.json",
    "app-pages-search-1.json", "app-pages-search-2.json",
    "app-pages-search-3.json", "version.json",
    "mentor/library/source-index.json",
]
report = {"source_repository":"6d9757cmp5-creator/ekab-spoudi",
          "source_commit":subprocess.check_output(["git","-C",str(source),"rev-parse","HEAD"],text=True).strip(),
          "transferred":[], "not_transferred":[], "counts":{}, "warnings":[],
          "generated_utc":datetime.datetime.now(datetime.timezone.utc).isoformat(),
          "independent_v2":True}
def load_json(file):
    return json.loads((source/file).read_text(encoding="utf-8-sig"))
def tracked_copy(path):
    src = source / path
    if not src.is_file():
        report["not_transferred"].append({"path":path,"reason":"not found in V1 main"})
        return None
    try:
        value=load_json(path)
    except (json.JSONDecodeError, UnicodeError) as ex:
        report["not_transferred"].append({"path":path,"reason":"invalid structured JSON: "+str(ex)[:100]})
        return None
    dst=raw_dir/path
    dst.parent.mkdir(parents=True,exist_ok=True)
    shutil.copyfile(src,dst)
    report["transferred"].append({"path":path,"bytes":src.stat().st_size,
                                   "sha256":hashlib.sha256(src.read_bytes()).hexdigest()})
    return value
saved={name:tracked_copy(name) for name in allowed}

# Full SAEK chapter text is in searchable chapter records, not just a headline.
chapters={}
for semester in "ABCD":
    greek=dict(A="A",B="B",C="C",D="D")[semester]
    records=(saved.get("saek-search-"+greek+".json") or {}).get("items",[])
    for item in records:
        try:
            course_id=int(str(item.get("courseId","")).strip())
            chapter_no=int(item.get("chapter",0))
        except (ValueError,TypeError):
            continue
        if course_id<1 or course_id>20 or chapter_no<1:
            continue
        key=f"saek-{course_id:02d}"
        entry={"title":str(item.get("chapterTitle","")).strip(),
               "content":str(item.get("searchText") or item.get("preview") or "").strip(),
               "_chapter":chapter_no}
        if entry["title"] and entry["content"]:
            chapters.setdefault(key,{})[chapter_no]=entry
chapters={k:[dict(title=d["title"],content=d["content"]) for _,d in sorted(v.items())]
          for k,v in chapters.items()}
report["counts"]["saek_chapters"]=sum(map(len,chapters.values()))
report["counts"]["saek_courses_with_chapters"]=len(chapters)

# The 314 official question IDs must remain stable, preserving V2 answer history.
original=saved.get("official_questions.json")
def find_questions(obj):
    if isinstance(obj,list):
        return obj if len(obj)>100 and all(isinstance(x,dict) for x in obj[:3]) else []
    if isinstance(obj,dict):
        for key in ("questions","items","allQuestions","theory","entries"):
            if key in obj:
                got=find_questions(obj[key])
                if got: return got
        for value in obj.values():
            got=find_questions(value)
            if got:return got
    return []
q_rows=[]
for row in find_questions(original):
    id_=str(row.get("id") or row.get("questionId") or "").strip()
    answer=row.get("answer") or row.get("educationalAnswer") or row.get("solution")
    question=row.get("question") or row.get("text") or row.get("prompt")
    if id_.startswith("EOP-") and isinstance(answer,str) and answer.strip() and isinstance(question,str) and question.strip():
        q_rows.append({"id":id_,"answer":answer.strip(),"question":question.strip()})
report["counts"]["matched_question_answers"]=len(q_rows)
if not q_rows: report["warnings"].append("Official V1 questions could not be matched by stable ID; preserved raw JSON, did not overwrite V2 answers.")

# V1 SOS answers are supplemental, not a license to replace audited questions.
sos=saved.get("eoppep-sos.json") or {}
report["counts"]["sos_questions"]=len(sos.get("items",[])) if isinstance(sos,dict) else 0
terms=saved.get("medical-terms.json") or {}
report["counts"]["medical_terms"]=len(terms.get("items",[])) if isinstance(terms,dict) else 0
checks=saved.get("saek-exams-bank.json") or {}
report["counts"]["saek_study_checks"]=len(checks.get("items",[])) if isinstance(checks,dict) else 0

# Parse the V1 educational pharmacology catalog only if it is regular JSON inside JS.
medications=[]
pharma_path=source/"farmaka-catalog.js"
if pharma_path.is_file():
    raw=pharma_path.read_text(encoding="utf-8")
    m=re.search(r"window\.EKAB_PHARMA\s*=\s*(\{[\s\S]*\})\s*;\s*$",raw)
    if m:
        try:
            parsed=json.loads(m.group(1))
            medications=parsed.get("medicines",[])
            if not isinstance(medications,list): medications=[]
            dst=raw_dir/"farmaka-catalog.json"
            dst.write_text(json.dumps(parsed,ensure_ascii=False,indent=2),encoding="utf-8")
            report["transferred"].append({"path":"farmaka-catalog.js (structured medicines only)","bytes":len(raw)})
        except json.JSONDecodeError:
            report["warnings"].append("V1 pharmacology catalog is not pure JSON; V2 pharmacology left unchanged.")
report["counts"]["medicines_catalog"]=len(medications)

# These modules cannot be honestly marked functional by copying their data.
report["not_transferred"] += [
    {"category":"user_data","reason":"V1 private personal progress/notes/account/cloud records are not in public GitHub; no access or permission to move them"},
    {"category":"backend","reason":"V1 Appwrite/AI/cloud functions, credentials and user accounts must not be copied or connected to V2"},
    {"category":"features","reason":"Native-only functions, voice, full 3D anatomy, live AI and live emergency dispatch require distinct implementation/testing"},
    {"category":"media","reason":"External photos/videos/PDFs and licensed educational assets require item-level rights and clinical verification before republication"},
    {"category":"frontend","reason":"V1 UI, CSS and JavaScript screens intentionally not copied: preserve V2 interface"},
]
payload={"chapters":chapters,
         "question_answers":q_rows,
         "medicines":medications,
         "sos":sos.get("items",[]) if isinstance(sos,dict) else [],
         "medical_terms":terms.get("items",[]) if isinstance(terms,dict) else [],
         "saek_checks":checks.get("items",[]) if isinstance(checks,dict) else [],
         "origin_commit":report["source_commit"]}
# V2 DOM/UI unchanged. This runs synchronously after V2's own data.js.
bridge = r"""/* Auto-generated from READ-ONLY EKAB1 published structured content. No interface modifications. */
(function(){
'use strict';
const imported=__PAYLOAD__;
if(typeof EKAB_V2_DATA==='undefined')throw Error('EKAB V2 data not loaded');
const d=EKAB_V2_DATA;
for(const course of d.courses||[]){
  const items=imported.chapters[course.id];
  if(Array.isArray(items)&&items.length){
    // Preserve existing unique sections; append missing chapter titles only.
    const titles=new Set(course.sections.map(s=>String(s.title).trim().toLowerCase()));
    for(const s of items) if(!titles.has(s.title.trim().toLowerCase())) {
      course.sections.push({title:s.title,content:s.content});
      titles.add(s.title.trim().toLowerCase());
    }
  }
}
const idMap=new Map((d.questions||[]).map(q=>[q.id,q]));
for(const row of imported.question_answers){
  const current=idMap.get(row.id);
  if(current&&row.answer&&row.question&&current.question.trim()===row.question.trim()){
    current.answer=row.answer;
  }
}
const names=new Set((d.medicines||[]).map(m=>String(m.name).trim().toLowerCase()));
for(const med of imported.medicines){
  if(!med||!med.name||!med.category)continue;
  const key=String(med.name).trim().toLowerCase();
  if(!names.has(key)){
    d.medicines.push(Object.assign({},med,{id:'v1-'+String(med.id||key)}));
    names.add(key);
  }
}
window.EKAB_V2_V1_IMPORT=Object.freeze({sourceCommit:imported.origin_commit,
  chapterCount:Object.values(imported.chapters).reduce((a,v)=>a+v.length,0),
  sosCount:imported.sos.length, termsCount:imported.medical_terms.length,
  studyChecksCount:imported.saek_checks.length});
})();"""
(site/"v1-content"/"v1-bridge.js").write_text(
    bridge.replace("__PAYLOAD__",json.dumps(payload,ensure_ascii=False,separators=(",",":"))),
    encoding="utf-8")
(site/"v1-content"/"migration-report.json").write_text(json.dumps(report,ensure_ascii=False,indent=2),encoding="utf-8")
(site/"v1-content"/"build-version.json").write_text(json.dumps({"v2":"web-v2","sourceCommit":report["source_commit"],"builtAt":report["generated_utc"]}),encoding="utf-8")
if report["counts"]["saek_chapters"]!=247 or report["counts"]["sos_questions"]!=314 or report["counts"]["medical_terms"]!=203:
    raise RuntimeError("V1 inventory unexpected: "+str(report["counts"]))
print("V1 migration summary",json.dumps(report["counts"],ensure_ascii=False))
print("Origin",report["source_commit"])
