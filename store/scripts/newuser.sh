#!/bin/bash

echo "Creating new user"
#server=149.28.59.50
#server=192.168.1.11
server=localhost
userInfo='{ "identifier": "admin6", "password": "admin5", "email": "support@workspaceafrica.com", "firstName": "TestUser", "lastName": "TestLast"}'


json=$(curl -X POST "http://${server}:9000/api/auth/signin/credentials" -H "accept: application/json" -H "Content-Type: application/json" -d '{ "identifier": "admin4", "password": "admin4"}')
echo $json
tokenOrig=`echo ${json} | jq-linux64 .token`
temp="${tokenOrig%\"}"
token="${temp#\"}"
echo "$token"
res2=$(curl -X POST "http://${server}:9000/api/auth/signup"  -H "X-Auth-Token: ${token}"  -H "accept: application/json" -H "Content-Type: application/json" -d '{ "identifier": "admin8", "password": "admin5", "email": "support@workspaceafrica.com", "firstName": "TestUser", "lastName": "TestLast"}'
)
echo "$res2"
