# Web-Log-Mining

### 웹 로그 마이닝을 통해 웹 로그 데이터에서 관심 있는 사용자들의 유용한 행동 패턴을 추출하여 웹 기반 서비스를 개선하고자한다. 

### 사용된 데이터는 2019년 1월 해킹을 당했던 웹 서버의 로그기록을 통해 게시판 형태의 웹 서비스를 타깃으로 하여 교회 사용자를 Hack, Search 에이전트로 분류하고, 컨텐츠 사용자들의 컨텐츠 사용 패턴을 추출한다. 

### 웹 로그 분석을 위해 Hadoop, Spark 사용하며, 데이터 전처리 및 저장을 위해 Filebeat, Logstash, Elasticsearch, Kibana를 사용한다. 분석된 데이터를 시각화하기 위해 FLASK, Apache HTTP Server 를 사용한다.

* Hadoop Install
  * Java Install
  * ssh Setting
* Spark Install
* ELK Install
  * Logstash
  * Elasticsearch
  * Kibana
  * Filebeat
* Apache HTTP Server Install
* Flask Install

### 시스템 구성도

![시스템구성](https://user-images.githubusercontent.com/44472886/103399858-5e04b600-4b86-11eb-84d1-9d411fe99c32.JPG)


### Apache 웹 서버로부터 원시 웹 액세스 로그를 데이터 전처리 서버로 전송합니다. 전송된 로그 데이터는 Filebeat를 통해 Logstash로 전달되며 전달된 원시 로그 데이터는 Logstash를 통해 필터링 되어 지리데이터를 포함하게 되고, 데이터 포맷이 Json형식으로 바뀌어 Elasticsearch에 색인됩니다. 색인된 데이터를 통해 데이터 분석을 시작하며, Apache Hadoop와 Elasticsearch의 연동 인터페이스 ES-HADOOP을 사용하여 Elasticsearch와 Hadoop 간에 데이터를 더욱 쉽게 이동할 수 있게 할 수 있습니다.. Apache Spark 분석 엔진을 통해 로그 데이터를 분석하여 분석된 데이터를 시각화 웹 서버로 전송한 뒤 마이크로 웹 프레임워크 Flask와 Apache HTTP Server를 통해 분석된 데이터를 차트로 시각해줍니다. Kibana를 통해 시각화하는 방법도 있습니다.



