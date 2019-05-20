#!/bin/sh 
curl -i -H "Content-type: application/json" -X POST -d "{\"emotions\":\"['Joy', 'Joy', 'Anger'] \"}" http://localhost:5000/emo/getburst
