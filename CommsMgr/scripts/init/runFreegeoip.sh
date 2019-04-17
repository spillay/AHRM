docker stop $(docker ps -q --filter ancestor=fiorix/freegeoip)
docker run --restart=always -p 4000:8080 -d fiorix/freegeoip
