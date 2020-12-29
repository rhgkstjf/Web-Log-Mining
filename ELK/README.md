# Filebeat
### Filebeat는 경량 로그 수집기로 로그와 파일을 경량화된 방식으로 전달하고
### 중앙 집중화하여 작업을 보다 간편하게 만들어준다.

### Filebeat Download
### Elastic 공식 홈페이지
### https://www.elastic.co/kr/downloads/beats/filebeat

### Filebeat는 Deb 파일, tar.gz 파일 설치방식이 2가지가 존재
### 필자는 tar.gz 파일을통해 압축을 풀어서 사용
### Filebeat 압축 풀기 
### 마지막 줄은 폴더이름을 filebeat로 간략화
```sh
wget https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-7.10.1-linux-x86_64.tar.gz
tar -xzvf filebeat-7.10.1-linux-x86_64.tar.gz
mv filebeat-7.10.1-linux-x86_64 filebeat
```

### Filebeat 환경 변수 추가
### 파일 경로 : /home/data/.bashrc
```sh
export FileBeat=$HOME/filebeat
```

### Filebeat 환경설정 수정
### Output 은 Logstash로 설정 
```sh
filebeat.inputs:

# Each - is an input. Most options can be set at the input level, so
# you can use different inputs for various configurations.
# Below are the input specific configurations.

- type: log

  # Change to true to enable this input configuration.
  enabled: true

  # Paths that should be crawled and fetched. Glob based paths.
  paths:
    - /home/data/Week/access.log*

output.logstash:
  # The Logstash hosts
  hosts: ["localhost:5044"]

```
### Filebeat는 한번 보냈던 파일 이름을 기억해두고
### 다시 실행시 그 파일은 전송시키지않음
### $FileBeat/data/registry/filebeat/log.json 데이터를 초기화해줘야함
```sh
echo "[]" > $FileBeat/data/registry/filebeat/log.json
```


# Logstash
### Logstash는 실시간 파이프라인 기능을 가진 데이터 수집 오픈소스 엔진이며, 서로 다른 소스의 데이터를 동적으로 통합하고 원하는 대상으로 데이터 정규화를 위한 필터의 종류가 많다. Logstash는 다른 서버 및 데이터 소스의 로그를 처리하며 출력은 Elasticsearch와 같은 출력 대상에 저장된다.

### Logstash Download
### Elastic 공식 홈페이지
### https://www.elastic.co/kr/downloads/logstash

### Logstash linux X86_64 tar.gz 파일 다운
### Logstash 압축 풀기
```sh
wget https://artifacts.elastic.co/downloads/logstash/logstash-7.10.1-linux-x86_64.tar.gz
tar -xzvf logstash-7.10.1-linux-x86_64.tar.gz
mv logstash-7.10.1-linux-x86_64 logstash
```

### Logstash 환경변수 추가
### 파일 경로 : /home/data/.bashrc
```sh
export LogStash=$HOME/logstash
```

### Logstash 환경설정 파일 복사 및 수정
### 파일 경로 : $LogStash/config/logstash-sample.conf
```sh
cp logstash-sample.conf
```

### 환경설정 수정
### grok 필터에서 QS:user-agent는 Filebeat의 agent와 겹침
### user_agent로 할 시 겹치지않고 해당 필드값 제대로 가져와짐
### Elasticsearch에 인덱싱할 시 index 이름에 logstash가 들어가야 geoip의 위도 경도값 인식
```sh
# Sample Logstash configuration for creating a simple
# Beats -> Logstash -> Elasticsearch pipeline.
input {
  beats {
    port => 5044
  }
}
filter {
    grok {
        match => { "message" => "%{COMMONAPACHELOG} %{QS:referrer} %{QS:user_agent}"}
    }
    date {
        match => [ "timestamp","UNIX" ]
        target => "date_object"
    }
    geoip {
        source => "clientip"
    }
    useragent{
        source => "user_agent"
    }
    if [os_major]  {
                mutate {
                        add_field => {
                                os_combine => "%{os} %{os_major}.%{os_minor}"
                        }
                }
        } else {
                 mutate {
                        add_field => {
                                os_combine => "%{os}"
                        }
                }
        }
        if [os] =~ "Windows" {
                mutate {
                        update => {
                                "os_name" => "Windows"
                        }
                }
        }
        if [os] =~ "Mac" {
                mutate {
                        update => {
                                "os_name" => "Mac"
                        }
                }
        }
}

output {
  elasticsearch {
    hosts => ["Elasticsearch_ip:port"]
    index => "logstash-Auto"
  }
}
```

# Elasticsearch
### Elasitcsearch는 오픈소스 검색 라이브러리인 아파치 루씬(Apache Lucene)을 기반으로 구축된 Java 오픈소스 분산 검색엔진으로 텍스트, 숫자, 위치 기반 정보, 정형 및 비정형 데이터 등 모든 유형의 데이터를 위한 분산형 오픈 소스 검색 및 분석 엔진이다. 간단한 REST API, 분산형 특징, 속도, 확장성을 제공하며 데이터 수집, 보강, 저장, 분석, 시각화를 위한 오픈 소스 도구 모음인 Elastic Stack의 중심 구성 요소로 사용된다. ELK Stack은 Elasticssearch, Logstash, Kibana, 이 오픈 소스 프로젝트 세 개의 머리글자이며, Logstash는 여러 소스에서 동시에 데이터를 수집하여 변환한 후 Elasticsearch 같은 “stash"로 전송하는 서버 사이드 데이터 처리 파이프라인이다. Kibana는 사용자가 Elasticsearch에서 차트와 그래프를 이용해 데이터를 시각화할 수 있게 해준다.

### Elasticsearch Download
### Elastic 공식 홈페이지
### https://www.elastic.co/kr/downloads/elasticsearch

### Elasticsearch Deb 파일로 다운로드
```sh
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.10.1-amd64.deb
sudo dpkg -i elasticsearch-7.10.1-amd64.deb
```
### Elasticsearch start
```sh
sudo service elasticsearch start
```

### Elasticsearch 실행 확인
```sh
curl -XGET localhost:9200
```

### Elasticsearch 환경설정 수정
### 수정을 위해 root 권한이 필요
### 파일 위치 : /etc/elasticsearch/elasticsearch.yml
```sh
su
cd /etc/elasticsearch
vim elasticsearch.yml
```

### yml 파일에 아래처럼 추가
### Elasticsearch 해킹을 당할수 있으므로, 방화벽을 꼭 쳐두시고
### port 를 9200 말고 다른걸로 사용하시는걸 권장합니다.
### Night Lion Security라는 인덱스가 생성되면서 모든 인덱스가 날라간 경험이있으므로
### 꼭 X-Pack 을 사용하여 Elasticsearch 보안에 신경써주세요 
### X-Pack Install은 Kibana Install 후 나옵니다.
```sh
cluster.name: cluster-name
node.name: node-01
path.data: /var/lib/elasticsearch
path.logs: /var/log/elasticsearch
network.host: elasticsearch-ip
node.master: true
http.port: elasticsearch-port <default:9200>
cluster.initial_master_nodes: node-01

#snapshot 폴더는 유저가 생성해줘야함
path.repo: ["/home/data/elastic_snapshot"]
```

# Kibana
### Kibana는 Elasticsearch 데이터를 시각화하고 Elastic Stack을 탐색하게 해주는 무료 오픈 소스 인터페이스
### Elastic 공식 홈페이지
### https://www.elastic.co/kr/downloads/kibana

### Kibana Deb Download
### https://artifacts.elastic.co/downloads/kibana/kibana-7.10.1-amd64.deb
```sh
wget https://artifacts.elastic.co/downloads/kibana/kibana-7.10.1-amd64.deb
sudo dpkg -i kibana-7.10.1-amd64.deb
```












### Elasticsearch X-Pack Install
### Elasticsearch bin 폴더 경로 <Deb 설치시> : /usr/share/elasticsearch/bin/ 
```sh

```



