#!/bin/sh 
curl -i -H "Content-type: application/json" -X POST -d '{"tokens":["happy","good"]}' http://localhost:9011/emo 

