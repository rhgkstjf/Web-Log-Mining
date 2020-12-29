# Filebeat
## Filebeat는 경량 로그 수집기로 로그와 파일을 경량화된 방식으로 전달하고
## 중앙 집중화하여 작업을 보다 간편하게 만들어준다.

### Filebeat Download
### Elastic 공식 홈페이지
### https://www.elastic.co/kr/downloads/beats/filebeat

### Filebeat는 Deb 파일, tar.gz 파일 설치방식이 2가지가 존재
### 필자는 tar.gz 파일을통해 압축을 풀어서 사용
### Filebeat 압축 풀기 
### 마지막 줄은<폴더이름을 filebeat로 간략화>
```sh
wget https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-7.10.1-linux-x86_64.tar.gz
tar -xzvf filebeat-7.10.1-linux-x86_64.tar.gz
mv filebeat-7.10.1-linux-x86_64.tar.gz filebeat
```

### Filebeat 환경 변수 추가
### 파일 경로 : /home/data/
