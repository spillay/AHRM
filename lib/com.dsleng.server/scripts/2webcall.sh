#!/bin/sh 
curl -i -H "Content-type: application/json" -X POST -d '{"tokens":[]}' http://localhost:9011/emo --header Origin:www.dsleng.com

