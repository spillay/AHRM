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

