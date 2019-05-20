#!/bin/sh 
curl -i -H "Content-type: application/json" -X POST -d "{\"text\":\"i sure hope we make good use of the bad news about skilling's resignation and do some house cleaning -- can we write down some problem assets and unwind raptor \"}" http://localhost:5000/emo/get
curl -i -H "Content-type: application/json" -X POST -d "{\"emotions\":\"['Joy', 'Joy', 'Anger'] \"}" http://localhost:5000/emo/getburst
curl -i -H "Content-type: application/json" -X POST -d "{\"emotionset\":\"{ 'mike': ['Joy', 'Joy', 'Joy', 'Joy', 'Joy'], 'judy': ['Joy', 'Joy', 'Joy', 'Anger', 'Anger', 'Joy', 'Anger'], 'jade': ['Joy', 'Joy', 'Joy', 'Anger','Joy', 'Joy'], 'mark': ['Joy', 'Joy', 'Anger', 'Anger','Anger', 'Anger', 'Joy'], } \"}" http://localhost:5000/emo/getColley
