# Web-Log-Mining

### 웹 로그 마이닝을 통해 웹 로그 데이터에서 관심 있는 사용자들의 유용한 행동 패턴을 추출하고 해킹당한 서버를 위해 사용자들의 사용패턴을 통해 유해 봇들을 차단하고 싶어서 시작한 프로젝트입니다.

### 사용된 데이터는 2019년 1월 해킹을 당했던 웹 서버의 로그기록을 통해 게시판 형태의 웹 서비스를 타깃으로 하여 교회 사용자를 Hack, Search 에이전트로 분류하고, 컨텐츠 사용자들의 컨텐츠 사용 패턴을 추출합니다.

### 웹 로그 분석을 위해 Hadoop, Spark 사용하며, 데이터 전처리 및 저장을 위해 Filebeat, Logstash, Elasticsearch, Kibana를 사용합니다. 분석된 데이터를 시각화하기 위해 FLASK, Apache HTTP Server 를 사용합니다.

### 분석을 자동화하기위해 로그 데이터를 사용자 배치크기만큼 전송받아 자동으로 분석한 후 분석된 데이터를 시각화합니다.

* Hadoop Install
  * Java Install
  * ssh Setting
* Spark Install
* ELK Install
  * Logstash
  * Elasticsearch
  * Kibana
  * Filebeat
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
### 해당 스크릅트는 일주일 단위로 자동으로 전송해야하므로 crontab 스케쥴링에 포함시켜줍니다.
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
        

```sh
sbt package
```






