#!/bin/bash

## To create with security disabled remove -u username:password to curl commands

HOST='localhost'
PORT=9200
INDEX_NAME='logstash-emails'
URL="http://${HOST}:${PORT}"
USERNAME=elastic
PASSWORD=changeme
printf "\n== Script for  uploading data == \n \n"

for i in `seq 1 97`;
do
     BAT=../batchfile$i.json
     curl -s -u ${USERNAME}:${PASSWORD} -X POST -H "Content-Type: application/json" ${URL}/${INDEX_NAME}/_bulk --data-binary "@${BAT}"
     printf "\nuploaded ${BAT}"
done    
#curl -s -u ${USERNAME}:${PASSWORD} -X POST -H "Content-Type: application/json" ${URL}/${INDEX_NAME}/_bulk --data-binary "@batchfile1.json"
