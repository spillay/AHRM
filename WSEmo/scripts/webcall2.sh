#!/bin/sh 
curl -i -H "Content-type: application/json" -X POST -d '{"tokens":[]}' http://localhost:9000/emo2 --header Origin:www.dsleng.com
