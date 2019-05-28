#!/bin/sh 
curl -i -H "Content-type: application/json" -X POST -d "{\"tokens\":\"[]\"}" http://localhost:9000/emo --header Origin:www.dsleng.com
