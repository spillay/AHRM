#!/bin/bash

## To create with security disabled remove -u username:password to curl commands

HOST='localhost'
PORT=9200
INDEX_NAME='logstash-emails'
URL="http://${HOST}:${PORT}"
USERNAME=elastic
PASSWORD=changeme
printf "\n== Script for creating index and uploading data == \n \n"
printf "\n== Deleting old index == \n\n"
curl -s -u ${USERNAME}:${PASSWORD} -X DELETE ${URL}/${INDEX_NAME}

printf "\n== Creating Index - ${INDEX_NAME} == \n\n"
curl -s  -u ${USERNAME}:${PASSWORD} -X PUT -H 'Content-Type: application/json' ${URL}/${INDEX_NAME} -d '{  
   "settings":{  
      "number_of_shards":1,
      "number_of_replicas":0
   },
   "mappings":{
     "logstash-emails":{
       "properties":{
         "model":{
           "properties":{
             "date":{
	       "type":"date"
             }
           }
          },
	  "location": {
	     "type": "geo_point"
          },
	  "timestamp": {
	     "type": "date"
          }
       }
     }
   }
}'

