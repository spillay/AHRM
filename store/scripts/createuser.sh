#!/bin/bash

echo "Creating new user"
#server=149.28.59.50
#server=192.168.1.11
server=localhost
res2=$(curl -X POST "http://${server}:9000/api/auth/signup"  -H "accept: application/json" -H "Content-Type: application/json" -d '{ "identifier": "admin", "password": "admin5", "email": "support@workspaceafrica.com", "firstName": "TestUser", "lastName": "TestLast"}'
)
echo "$res2"
