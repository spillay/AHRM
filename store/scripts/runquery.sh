#!/bin/bash

echo "Running Query"
#server=149.28.59.50
server=localhost
username=admin
password=admin5

json=$(curl -X POST "http://${server}:9000/api/auth/signin/credentials" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"identifier\": \"${username}\", \"password\": \"${password}\"}")
tokenOrig=`echo ${json} | jq-linux64 .token`
temp="${tokenOrig%\"}"
token="${temp#\"}"
echo "$token"
resQuery=$(curl -X POST "http://${server}:9000/api/emotion"  -H "X-Auth-Token: ${token}"  -H "accept: application/json" -H "Content-Type: application/json" -d '{ "text": "I had a bad day today, cause the team just seem to not understand the culture of the company. I am also happy that this happened at the beginning of the project, or I might be in for a huge nightmare. All will be good, if we can remove some people, what do you think." }')
echo $resQuery

