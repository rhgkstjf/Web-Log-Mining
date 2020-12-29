1. Oracle Java 8 Install
   구글 - Oracle Java 8 검색 후 Oracle 홈페이지에서 운영체제 linux-x64 파일형식 tar.gz 다운
   필자는 JDK 1.8.0_201 버전 사용
   ### JDK tar.gz 압축 풀기
   ```sh
   tar -xvzf jdk-8u201-linux-x64.tar.gz
   sudo mkdir /usr/lib/jvm
   sudo mv jdk1.8.0_201 /usr/lib/jvm
   ```
   
   ### Java 환경변수 추가
   ```sh
   vim /home/hadoop/.bashrc
   ```
   
   ### <아래 내용 파일에 추가>
   ```sh
   export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_201
   export PATH=$JAVA_HOME/bin:$PATH
   export CLASSPATH=$JAVA_HOME/lib:$CLASSPATH
   ```
   
   ### 설치할 Java 버전 심볼릭 링크 생성
   ```sh
   sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/jdk1.8.0_201/bin/java 1
   sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/jdk1.8.0_201/bin/javac 1
   sudo update-alternatives --install /usr/bin/javaws javaws /usr/lib/jvm/jdk1.8.0_201/bin/javaws 1
   
   java -version
   ```
   정상 설치시 java version 출력
