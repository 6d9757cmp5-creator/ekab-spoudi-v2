'use strict';
const test=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),path=require('node:path');
const root=path.resolve(__dirname,'..');
const read=p=>fs.readFileSync(path.join(root,p),'utf8');
test('iOS WKWebView does not double apply safe areas',()=>{
 const swift=read('ios/AppDelegate.swift');
 const html=read('index.html');
 assert.match(swift,/contentInsetAdjustmentBehavior\s*=\s*\.never/);
 assert.doesNotMatch(swift,/contentInsetAdjustmentBehavior\s*=\s*\.automatic/);
 assert.match(swift,/scrollView\.contentInset\s*=\s*\.zero/);
 assert.match(html,/viewport-fit=cover/);
 assert.match(html,/safe-area-inset-top/);
 assert.match(html,/safe-area-inset-bottom/);
 assert.match(html,/overflow-x:hidden/);
});
test('the embedded website can update without a new IPA',()=>{
 const html=read('index.html'),script=read('auto-update.js');
 assert.match(html,/src="auto-update\.js/);
 assert.match(script,/v1-content\/build-version\.json/);
 assert.match(script,/setInterval\(/);
 assert.match(script,/visibilitychange/);
 assert.match(script,/Ενημέρωση τώρα/);
 assert.match(script,/activeStudy\(\)/);
});
test('every V2 push builds complete unsigned offline IPA',()=>{
 const yml=read('.github/workflows/ios-ipa.yml');
 assert.match(yml,/branches: \[main\]/);
 assert.doesNotMatch(yml,/^\s+paths:\s*$/m);
 for(const name of ['mentor-adaptive.js','auto-update.js','v1-study-library.js','v1-content/v1-bridge.js','import-v1-data.py','smoke-v2-web.cjs'])
  assert.ok(yml.includes(name),name);
 assert.match(yml,/--clobber/);
 assert.match(yml,/CODE_SIGNING_ALLOWED=NO/);
});
