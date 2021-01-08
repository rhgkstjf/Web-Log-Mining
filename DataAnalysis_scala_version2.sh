#!/bin/bash
P=$(date "+%Y-%m-%d")
mkdir [UserHomePath]/AutoLab/Week_DF/$P
echo {"-------------오늘은 $P입니다. 데이터 배치 분석 자동화 시작합니다.-------------"}

echo "-------------url 크롤링 시작 ----------------------"
python3 crw.py
sleep 2m
hadoop fs -put urldata.json /urldata/urldata.json
echo "-------------url 데이터 갱신 완료 -------------------"

echo "-------------데이터 분석 시작------------"
$SPARK_HOME/bin/spark-submit --class Auto --master yarn [UserHomePath]/AutoLab/Auto/target/scala-2.11/autolog-project_2.11-1.3.jar
echo "-------------데이터 분석 완료------------"

echo "-------------ES INDEX 삭제 시작----------"
sleep 5s
curl -X DELETE "[ElasticsearchURL]/[Index]?pretty"
echo "-------------ES INDEX 삭제 완료 ---------"

echo "-------------HDFS -> LocalFS 로 복사 시작 -----"
hadoop fs -copyToLocal /Auto/$P/*.json /home/hadoop/AutoLab/Week_DF/$P/Total.json
hadoop fs -copyToLocal /Auto/Class/*.json /home/hadoop/AutoLab/Week_DF/$P/Class.json
hadoop fs -copyToLocal /Auto/Content/*.json /home/hadoop/AutoLab/Week_DF/$P/Content.json
hadoop fs -copyToLocal /Auto/Hack/*.json /home/hadoop/AutoLab/Week_DF/$P/Hack.json
hadoop fs -copyToLocal /Auto/User/*.json /home/hadoop/AutoLab/Week_DF/$P/User.json
hadoop fs -copyToLocal /Auto/Korea/*.json /home/hadoop/AutoLab/Week_DF/$P/Korea.json

hadoop fs -rm /Auto/$P/*
hadoop fs -rm /Auto/Class/*
hadoop fs -rm /Auto/Content/*
hadoop fs -rm /Auto/Hack/*
hadoop fs -rm /Auto/User/*
hadoop fs -rm /Auto/Korea/*

hadoop fs -rmdir /Auto/$P
hadoop fs -rmdir /Auto/Class
hadoop fs -rmdir /Auto/Content
hadoop fs -rmdir /Auto/Hack
hadoop fs -rmdir /Auto/User
hadoop fs -rmdir /Auto/Korea
echo "-------------LocalFS로 복사 완료 ----------"
sleep 5s

echo "-------------url data 초기화 위해 삭----------------"
rm urldata.json
hadoop fs -rm /urldata/urldata.json
echo "-------------삭제 완료---------------------"

echo "---------------웹 서버에 데이터 전송 중--------------"
ssh -p [web-server-port] [web-server-user]@[web-server-ip] "echo '[sudo pwd]' | sudo -S mkdir /var/www/html/FLASKAPPS/Data/$P"
ssh -p [web-server-port][web-server-user]@[web-server-ip] "sh /home/[user]/DataLoad.sh
echo "---------------웹 서버에 데이터 전송 완료--------------"
