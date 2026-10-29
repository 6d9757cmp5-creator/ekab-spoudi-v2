'use strict';
const test=require('node:test');
const assert=require('node:assert/strict');
const fs=require('node:fs');
const path=require('node:path');
const M=require('../mentor-adaptive.js');
const base=new Date('2026-10-29T09:00:00.000Z');
test('single MENTOR routes all major legacy topics without choosing a persona',()=>{
 const cases=[
  ['ανατομία οστά','anatomy'],
  ['ΗΚΓ ρυθμός','ecg'],
  ['ΚΑΡΠΑ BLS','resuscitation'],
  ['πολυτραυματίας','trauma'],
  ['φάρμακα και δοσολογία','pharmacology'],
  ['ερωτήσεις ΕΟΠΠΕΠ','certification'],
  ['MCI triage','triage'],
  ['ιατρικός εξοπλισμός','equipment'],
  ['αποστολή ασθενοφόρου','missions']
 ];
 for(const [query,id] of cases)assert.equal(M.inferTopic(query),id,query);
 for(const id of Object.keys(M.TOPICS))assert.ok(M.TOPICS[id].route);
});
test('memory is self-reported and does not mutate official progress',()=>{
 const legacy={questions:{a:'wrong'},courses:['saek-1']};
 const original=JSON.stringify(legacy);
 const first=M.newMemory();
 const visit=M.recordVisit(first,'anatomy');
 const later=M.recordReview(visit,'anatomy','repeat',base);
 assert.equal(first.topics.anatomy,undefined);
 assert.equal(later.topics.anatomy.visits,1);
 assert.equal(later.topics.anatomy.selfReport,'repeat');
 assert.equal(later.topics.anatomy.due,'2026-10-30');
 assert.equal(JSON.stringify(legacy),original);
 assert.equal(M.recommend(later,legacy,new Date('2026-10-31T09:00:00.000Z')).id,'anatomy');
});
test('EOPPEP past incorrect answers are used read-only to suggest review',()=>{
 assert.equal(M.recommend(M.newMemory(),{questions:{q:'wrong'}}).id,'certification');
 assert.equal(M.legacyWrongCount({questions:{q:'wrong',r:'right'}}),1);
});
test('tone automatically adapts to a declared difficulty, not a fake exam grade',()=>{
 const first=M.newMemory();
 const visited=M.recordVisit(first,'anatomy');
 const changed=M.recordReview(visited,'anatomy','repeat',base);
 assert.equal(M.effectiveStyle(changed,'anatomy'),'simple');
 assert.equal(M.guide(changed,'ανατομία').style,'simple');
});
test('no free-form question stored in memory',()=>{
 const words='Περιέγραψε πραγματικό ασθενή με μοναδικό όνομα';
 const guided=M.guide(M.newMemory(),words);
 const recorded=M.recordVisit(M.newMemory(),guided.id);
 assert.equal(JSON.stringify(recorded).includes(words),false);
 assert.equal(M.safeMemory({version:1,topics:{anatomy:{visits:2,due:'2026-11-01',selfReport:'ok',streak:1}},recentTopics:['invalid','anatomy']}).recentTopics.length,1);
});
test('visible screen has one MENTOR and no legacy faculty picker',()=>{
 const source=fs.readFileSync(path.join(__dirname,'..','main.js'),'utf8');
 const section=source.split('function mentorTutor(){')[1].split('function mentorEquipment(){')[0];
 assert.ok(section.includes("sectionTitle('MENTOR'"));
 assert.equal(section.includes('X.faculty.map'),false);
 assert.equal(section.includes('choose-faculty'),false);
 assert.equal(section.includes('Η ομάδα των 5'),false);
 assert.ok(source.includes('mentor-review-repeat'));
 assert.ok(source.includes("mentorStoreKey='ekab-spoudi-v2-mentor-private-v1'"));
 assert.equal(source.includes('MENTOR OS'),false);
});
