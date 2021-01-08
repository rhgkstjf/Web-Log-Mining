#!/bin/bash
P=$(date "+%Y-%m-%d")

echo "[]" > $FileBeat/data/registry/filebeat/log.json
echo "--------------FileBeat 넣은 데이터 초기화-------------------"

echo "-------------파일비트로부터 데이터를 읽고 Logstash에서 필터링하여 보내기 시작합니다.----------"

$LogStash/bin/logstash -f $LogStash/config/access-apache.conf &
$FileBeat/filebeat -e -c $FileBeat/filebeat.yml -d "publish" &
sleep 30m

logstashpid=$(lsof -i:5044 | grep 'LISTEN' | awk '{print $2}')

echo "-------------파일비트 로그스태시 종료 시작-----"
pkill -9 -ef filebeat
kill -9 $logstashpid
echo "-------------파일비트 로그스태시 종료 완료-----"
sleep 5s
