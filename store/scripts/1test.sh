#!/bin/bash

echo "Creating new user"
#server=149.28.59.50
#server=192.168.1.11
server=localhost
userInfo='{ "identifier": "admin6", "password": "admin5", "email": "support@workspaceafrica.com", "firstName": "TestUser", "lastName": "TestLast"}'

token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxLWZBZTNOSUVHcklQaDVXTkpyU0FRZ3MweHUrc0d0Ykw5RDZnbEhtZkkrSjFPeFdHZ0xUQWZFbjFKbVwvYzc3c0tlTFhOQ2E2d1VYT3Q4Mll0YzlqVjVveUJJRVE9PSIsImlzcyI6InBsYXktc2lsaG91ZXR0ZSIsImV4cCI6MTU0OTExNzI1OSwiaWF0IjoxNTQ5MDc0MDU5LCJqdGkiOiI4ZGU1NTVjYjIxZmQzZDY5MGMzODViODU4YTdjZTVhMTk5M2I2NzQyMTEyNWE0MWVlNjg3ZmM1MTYzYjQ3NGRjMjhjN2NhOTYwZjQyMzFkNzZhY2NiZTg4NDFhMDY1MWIwOWM3M2ViMDM4ZjY3OGYxNDM3OWMyOTcxOTJjMTljYjYwYTMxNGVlZWIyZDA3MzliYmViZGM0NGY0ZDkyMGVlYzIyYmRkZTUyN2Q4NDY2NzZlZTk4ZmQzMTU4YWUyN2NjOTE0NTk3NTExZjJlZDQ3ZTM5YzhmZmJmZjI4ODA5NGQ4ZTkxM2JlZmY3Yjk0YjI4NTdiMGE4ZDRkNDgwODQ3In0.Sf-98yPlpaYls5hta2zH-UvX1P31C-laCd1x4HAPhOY"
res2=$(curl -X POST "http://${server}:9000/api/auth/signup"  -H "X-Auth-Token: ${token}"  -H "accept: application/json" -H "Content-Type: application/json" -d '{ "identifier": "admin8", "password": "admin5", "email": "support@workspaceafrica.com", "firstName": "TestUser", "lastName": "TestLast"}'
)
echo $res2
