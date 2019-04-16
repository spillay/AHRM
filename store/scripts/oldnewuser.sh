#!/bin/bash

echo "Creating new user"
server=149.28.59.50
userInfo='{ "identifier": "admin5", "password": "admin5", "email": "support@workspaceafrica.com", "firstName": "TestUser", "lastName": "TestLast"}'


json=$(curl -X POST "http://${server}:9000/api/auth/signin/credentials" -H "accept: application/json" -H "Content-Type: application/json" -d '{ "identifier": "admin4", "password": "admin4"}')
tokenOrig=`echo ${json} | jq-linux64 .token`
temp="${tokenOrig%\"}"
token="${temp#\"}"
echo "$token"
res=$(curl -X POST "http://${server}:9000/api/auth/register"  -H "X-Auth-Token: ${token}"  -H "accept: application/json" -H "Content-Type: application/json" -d '{ "info": "information" }')
echo $res
res2=$(curl -X POST "http://${server}:9000/api/auth/signup"  -H "X-Auth-Token: ${token}"  -H "accept: application/json" -H "Content-Type: application/json" -d ${userInfo} )
echo $res2
