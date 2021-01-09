# Web-Log-Mining

### Spark 2.3.3 -> Spark 3.0.1 버전 교체가 이루어졌습니다.
### scala version 2.11 -> 2.12

### 웹 로그 마이닝을 통해 웹 로그 데이터에서 관심 있는 사용자들의 유용한 행동 패턴을 추출하고 해킹당한 서버를 위해 유해 봇들을 차단하고 싶어서 시작한 프로젝트입니다.

### 사용된 데이터는 2019년 1월 해킹을 당했던 웹 서버의 로그기록을 통해 게시판 형태의 웹 서비스를 타깃으로 하여 교회 사용자를 Hack, Search 에이전트로 분류하고, 컨텐츠 사용자들의 컨텐츠 사용 패턴을 추출합니다.

### 웹 로그 분석을 위해 Hadoop, Spark 사용하며, 데이터 전처리 및 저장을 위해 Filebeat, Logstash, Elasticsearch, Kibana를 사용합니다. 분석된 데이터를 시각화하기 위해 FLASK, Apache HTTP Server 를 사용합니다.

### 분석을 자동화하기위해 로그 데이터를 사용자 배치크기만큼 전송받아 자동으로 분석한 후 분석된 데이터를 시각화합니다.

https://github.com/rhgkstjf/Web-Log-Mining/tree/main/Hadoop
* Hadoop Install
  * Java Install
  * ssh Setting
 
https://github.com/rhgkstjf/Web-Log-Mining/tree/main/spark
* Spark Install

https://github.com/rhgkstjf/Web-Log-Mining/tree/main/ELK
* ELK Install
  * Logstash
  * Elasticsearch
  * Kibana
  * Filebeat
  
https://github.com/rhgkstjf/Web-Log-Mining/tree/main/Flask
* Flask Install

### 시스템 구성도

![시스템구성](https://user-images.githubusercontent.com/44472886/103399858-5e04b600-4b86-11eb-84d1-9d411fe99c32.JPG)


### Apache 웹 서버로부터 원시 웹 액세스 로그를 데이터 전처리 서버로 전송합니다.   전송된 로그 데이터는 Filebeat를 통해 Logstash로 전달되며 전달된 원시 로그 데이터는 Logstash를 통해 필터링 되어 지리데이터를 포함하게 되고,   데이터 포맷이 Json형식으로 바뀌어 Elasticsearch에 색인됩니다.   색인된 데이터를 통해 데이터 분석을 시작하며, Apache Hadoop와 Elasticsearch의 연동 인터페이스 ES-HADOOP을 사용하여 Elasticsearch와 Hadoop 간에 데이터를 더욱 쉽게 이동할 수 있게 할 수 있습니다.. Apache Spark 분석 엔진을 통해 로그 데이터를 분석하여 분석된 데이터를 시각화 웹 서버로 전송한 뒤 마이크로 웹 프레임워크 Flask와 Apache HTTP Server를 통해 분석된 데이터를 차트로 시각해줍니다. Kibana를 통해 시각화하는 방법도 있습니다.


# Raw Apache Web Access Log 전송
### 원시 접속 로그를 데이터 전처리 서버로 전송해야합니다.
```sh
declare -a array
array=$(echo [SaveFolderPath]/*)
P=$(date "+%Y-%m-%d")

ssh [user]@[ip] 'mkdir [SaveFolderPath]$(date "+%Y-%m-%d")'
ssh [user]@[ip] 'rm [SaveFolderPath]/Week/*'

for A in ${array}; do
        if [[ "$A" =~ "access.log" ]]; then
                if [[ "$A" =~ "gz" || "$A" =~ "other" ]]; then
                        continue
                else
                        echo ${A:20}
                        scp -rP [port] $A [user]@[ip]:[SaveFolderPath]/$(date "+%Y-%m-%d")/${A:20}
                        scp -rP [port] $A [user]@[ip]:[SaveFolderPath]/${A:20}

                fi
        fi
done

#txt file 을 만들어 두셔야합니다. 용도는 전송한 날짜 저장을 위함입니다.
ssh -p [port] [user]@[ip] 'echo $(date "+%Y-%m-%d") >> [Folderpath]/[txt file name]'
```

### 전송받은 데이터 전처리 서버는 Week 폴더안에 분석을 시작할 주의 웹 로그 데이터가 들어있으며,   해당 주의 폴더에 Log 데이터를 백업을합니다.
### 해당 스크립트는 일주일 단위로 자동으로 전송해야하므로 crontab 스케쥴링에 포함시켜줍니다.
```sh
export VISUAL=vim; crontab -e
```
### crontab 스케쥴링시 자신이 원하는 시간, 날짜를 정해서 수정하시면됩니다.
```sh
0 12 * * 1 sh $HOME/Log-transport.sh
```

# 원시 로그 데이터를 받아 데이터 전처리를 해야합니다.
### 전처리 서버의 시스템 구성
![noname01](https://user-images.githubusercontent.com/44472886/103401573-bccd2e00-4b8c-11eb-826c-0ec5ddbb5fbd.png)

### 전처리 서버의 핵심은 원시 로그 데이터에 들어있는 필드값을 grok 필터를 통해 필트값을 가져오고,   geoip 를 통해서 지리 데이터를 추가하는것입니다.
![dddd](https://user-images.githubusercontent.com/44472886/103401675-23eae280-4b8d-11eb-8c7a-a1c1ea0fbdf4.JPG)
### 위 그림과 같이 지리 정보 데이터가 추가하여 더욱 효과적으로 지도를 통해 유용한 정보를 보여줄 수 있습니다.

### 전처리는 전단계인 원시 로그데이터가 전송한 후에 작업이 이루어져야합니다.
### 스케쥴링시에 약간의 시간을 두시고 스케쥴링해주세요.


# 전처리된 데이터를 통해 데이터 분석을 시작합니다.

### 분석을 시작하기 전에 웹 서버의 게시판을 크롤링해야합니다.
### 사용자의 접속과 게시판 연관을 위해 필요합니다.
### 크롤링한 데이터를 HDFS상에 저장합니다.
```sh
python3 crw.py
hadoop fs -put urldata.json /urldata/urldata.json
```

### Scala Code Source 를 sbt를 통해 패키징합니다.
### Sbt version 1.3
### Scala Version 2.11.8
### libraryDependencies += spark-core 2.3.3 , spark-sql 2.3.3

### sbt 프로젝트 구조
* sbt-project-folder
  * src
    * main
      * scala
        * scala-source
        
### Scala Source는 해당 프로젝트 폴더에 있습니다.
```sh
sbt package
```

### jar 파일을 Spark에 제출하여 실행합니다.
```sh
$SPARK_HOME/bin/spark-submit --class Auto --master yarn $HOME/AutoLab/Auto/target/scala-2.11/autolog-project_2.11-1.3.jar
```

### 실행되고난 후 HDFS에 생된 json파일들은 모두 분석을 통해 의미있는 정보를 담고있습니다.
### 해당 데이터들을 모두 Local에 저장합니다.
### 저장한 후에 사용된 폴더와 파일도 모두 지웁니다.
### ES에 저장되있는 데이터도 모두 지우고 index도 삭제합니다.
```sh
curl -X DELETE "[es-ip]:[es-port]/[es-index-name]?pretty"

hadoop fs -copyToLocal /Auto/$P/*.json /home/hadoop/AutoLab/Week_DF/$P/Total.json
hadoop fs -copyToLocal /Auto/Class/*.json /home/hadoop/AutoLab/Week_DF/$P/Class.json
hadoop fs -copyToLocal /Auto/Content/*.json /home/hadoop/AutoLab/Week_DF/$P/Content.json
hadoop fs -copyToLocal /Auto/Hack/*.json /home/hadoop/AutoLab/Week_DF/$P/Hack.json
hadoop fs -copyToLocal /Auto/User/*.json /home/hadoop/AutoLab/Week_DF/$P/User.json
hadoop fs -copyToLocal /Auto/Korea/*.json /home/hadoop/AutoLab/Week_DF/$P/Korea.json
hadoop fs -copyToLocal /Auto/Classfication/*.json /home/hadoop/AutoLab/Week_DF/$P/Classfication.json
hadoop fs -copyToLocal /Auto/Country/*.json /home/hadoop/AutoLab/Week_DF/$P/Country.json

hadoop fs -rm /Auto/$P/*
hadoop fs -rm /Auto/Class/*
hadoop fs -rm /Auto/Content/*
hadoop fs -rm /Auto/Hack/*
hadoop fs -rm /Auto/User/*
hadoop fs -rm /Auto/Korea/*
hadoop fs -rm /Auto/Classfication/*
hadoop fs -rm /Auto/Country/*

hadoop fs -rmdir /Auto/$P
hadoop fs -rmdir /Auto/Class
hadoop fs -rmdir /Auto/Content
hadoop fs -rmdir /Auto/Hack
hadoop fs -rmdir /Auto/User
hadoop fs -rmdir /Auto/Korea
hadoop fs -rmdir /Auto/Classfication
hadoop fs -rmdir /Auto/Country
```

### Local에 저장한 데이터를 웹 서버에서 가져갑니다.

```sh
P=$(date "+%Y-%m-%d")
ssh -p [web-server-port] [web-server-user]@[web-server-ip] "echo '[sudo pwd]' | sudo -S mkdir /var/www/html/FLASKAPPS/Data/$P"
ssh -p [web-server-port][web-server-user]@[web-server-ip] "sh /home/[user]/DataLoad.sh
```

### 가져가는 스크립트
```sh
P=$(date "+%Y-%m-%d")
scp -P [Hadoop-master-port] hadoop@[hadoop-master-ip]:/home/hadoop/AutoLab/Week_DF/$P/* $HOME/Data/
echo '[sudo pwd]' | sudo -S mv $HOME/Data/* /var/www/html/FLASKAPPS/Data/$P/
```

### 해당 데이터를 FLASK에서 사용하여 html파일을 렌더링시키면서, html에 데이터를 전송합니다.

### 웹을 통해 데이터를 차트로 시각화합니다.

# 데이터 분석

### 분석하고자 하는 웹 접속 로그의 형식은 그림과같습니다.
### Combined 접속 로그와 Common 접속 로그 형식 2가지 중 Combined형식을 사용합니다.
### Combined 접속 로그 예시
![log](https://user-images.githubusercontent.com/44472886/103403409-51d32580-4b93-11eb-893e-9f8a05f98a8f.png)
### Combined 접속 로그 필드
![dfsdfasdf](https://user-images.githubusercontent.com/44472886/103403416-57c90680-4b93-11eb-87f0-dd6b5287da64.JPG)

### 이 데이터들은 전처리를 거쳐 지리정보를 포함하므로 분석시에 지리정보를 이용합니다.
### 분석의 순서는 다음 그림과같습니다.
![분류플로우](https://user-images.githubusercontent.com/44472886/103403489-9eb6fc00-4b93-11eb-8c19-966d7b18b2a9.png)

### 먼저 Hack 봇, search 봇을 분류해야합니다.
### 해당 링크의 작성자분의 Hack List를 참고하여 특정 Agent와 쿼리문 , admin 접근 등의 Request를 통해 사용자를 Hack으로 분류하였습니다.
### https://xetown.com/tips/1130812

### search 봇은 Google, BingBot, ... 등 유명한 검색 봇을 구글에 검색한 기준으로 분류하였습니다.

### 사용자와 게시판을 연관시켜서 사용자들에게 인기가 많은 게시판
### 국가별 게시판 사용률 유해봇이 가장 많이 접속한 게시판 등의 정보를 얻을 수 있습니다.

![web1](https://user-images.githubusercontent.com/44472886/103403733-85fb1600-4b94-11eb-8d8b-2badbb32fe71.JPG)
![web2](https://user-images.githubusercontent.com/44472886/103403736-872c4300-4b94-11eb-81c0-5ce5a9cf7efe.JPG)
![web3](https://user-images.githubusercontent.com/44472886/103403737-872c4300-4b94-11eb-82ec-f1d597429feb.JPG)
![web4](https://user-images.githubusercontent.com/44472886/103403739-87c4d980-4b94-11eb-81c1-8f13f11b676a.JPG)
![web5](https://user-images.githubusercontent.com/44472886/103403740-87c4d980-4b94-11eb-9d12-9f4a4c3833d8.JPG)




