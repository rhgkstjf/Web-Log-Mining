# 1. Oracle Java 8 Install
   ## 구글 - Oracle Java 8 검색 후 Oracle 홈페이지에서 운영체제 linux-x64, 파일형식 tar.gz 다운 필자는 JDK 1.8.0_201 버전 사용
   
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
   ### 정상 설치시 java version 출력
# 2. ssh 세팅
   ## 호스트 파일 설정
   ### 파일 경로 : /etc/hosts에 아래와 같이 자신의 컴퓨터 노드 추가
   ### 예시
   ```sh
   1.2.3.1 master
   1.2.3.2 second
   1.2.3.3 slave1
   #1.2.3.4 slave2...
   ```
   
   ### Hadoop 사용시, 노드간 자동으로 ssh 통신을 하기 위한 작업
   ### ssh key 생성
   ### * 실행 호스트 : Master
   ### * 모든 서버에서 ssh-keygen 명령어를 사용하여 공개키 발급
   ### * /home/hadoop/.sshj 디렉토리 생성 및 생성 파일 확인
   ```sh
   ssh-keygen -t rsa
   ssh second 'ssh-keygen -t rsa'
   ```
   ### master - second ssh 인증 설정
   ```sh
   cat /home/hadoop/.ssh/id_rsa.pub >> /home/hadoop/.ssh/authorized_keys
   ssh second cat /home/hadoop/.ssh/id_rsa.pub >> /home/hadoop/.ssh/authorized_keys
   scp /home/hadoop/.ssh/authorized_keys second:/home/hadoop/.ssh/authorized_keys
   ```
   ### Hadoop 설치
   ```sh
   wget https://archive.apache.org/dist/hadoop/common/hadoop-2.7.7/hadoop-2.7.7.tar.gz
   tar -xvzf hadoop-2.7.7.tar.gz
   ```
   
   ### Hadoop 환경변수 추가
   ```sh
   vim /home/hadoop/.bashrc
   ```
   
   ### <아래 내용 파일에 입력>
   ```sh
   export HADOOP_PREFIX=$HOME/hadoop-2.7.7
   export HADOOP_HOME=$HOME/hadoop-2.7.7
   export PATH=$PATH:$HADOOP_PREFIX/bin
   export PATH=$PATH:$HADOOP_PREFIX/sbin
   export HADOOP_MAPRED_HOME=${HADOOP_PREFIX}
   export HADOOP_COMMON_HOME=${HADOOP_PREFIX}
   export HADOOP_HDFS_HOME=${HADOOP_PREFIX}
   export YARN_HOME=${HADOOP_PREFIX}
   export HADOOP_YARN_HOME=${HADOOP_PREFIX}
   export HADOOP_CONF_DIR=${HADOOP_PREFIX}/etc/hadoop
   export YARN_CONF_DIR=${HADOOP_PREFIX}/etc/hadoop
   
   #Native Path
   export HADOOP_COMMON_LIB_NATIVE_DIR=${YARN_HOME}/lib/native
   export HADOOP_OPTS="-Djava.library.path=$YARN_HOME/lib"
   ```
   
   ### 수정 후 source /home/hadoop/.bashrc 입력
   
   ### second, slave node 도메인 입력
   ### 경로 : $HADOOP_HOME/etc/hadoop/slaves
   ### <아래 내용 파일에 입력>
   ```sh
   master
   second
   slave1
   slave2
   #slave3...
   ```
   
   ### 네임노드 및 URL 설정
   ### 경로 : $HADOOP_HOME/etc/hadoop/core-site.xml
   ```sh
   <configuration>
        <property>
                <name>fs.default.name</name>
                <value>hdfs://master:9000</value>
        </property>
   </configuration>
   ```
   
   ### 네임노드, 세컨드 등 HDFS 데몬을 위한 환경설정
   ### 파일 경로 : $HADOOP_HOME/etc/hadoop/hdfs-site.xml
   ```sh
   <configuration>
        <property>
                <name>dfs.replication</name>
                <value>3</value>
        </property>
        <property>
                <name>dfs.name.dir</name>
                <value>/home/hadoop/hadoop-2.7.7/hdfs/name</value>
        </property>
        <property>
                <name>dfs.data.dir</name>
                <value>/home/hadoop/hadoop-2.7.7/hdfs/data</value>
        </property>
        <property>
                <name>dfs.http.address</name>
                <value>master:50070</value>
        </property>
        <property>
                <name>dfs.permissions</name>
                <value>false</value>
        </property>
        <property>
                <name>dfs.namenode.secondary.http-address</name>
                <value>second:50090</value>
        </property>
   </configuration>
   ```
   
   ### YARN 자원 관리자, 노드 관리자 환경설정
   ### 파일 경로 : $HADOOP_HOME/etc/hadoop/yarn-site.xml
   ```sh
   <property>
                <name>yarn.nodemanager.aux-services</name>
                <value>mapreduce_shuffle</value>
        </property>
        <property>
                <name>yarn.nodemanager.aux-services.mapreduce.shuffle.class</name>
                <value>org.apache.hadoop.mapred.ShuffleHandler</value>
        </property>
        <property>
                <name>yarn.resourcemanager.resource-tracker.address</name>
                <value>master:8025</value>
        </property>
        <property>
                <name>yarn.resourcemanager.scheduler.address</name>
                <value>master:8030</value>
        </property>
        <property>
                <name>yarn.resourcemanager.address</name>
                <value>master:8040</value>
        </property>
        <property>
                <name>yarn.resourcemanager.webapp.address</name>
                <value>master:8088</value>
        </property>
        <property>
                <name>yarn.nodemanager.resource.memory-mb</name>
                <value>2048</value>
        </property>
        <property>
                <name>yarn.nodemanager.resource.cpu-vcores</name>
                <value>1</value>
        </property>
        <property>
                <name>yarn.nodemanager.pmem-check-enabled</name>
                <value>false</value>
        </property>
        <property>
                <name>yarn.nodemanager.vmem-check-enabled</name>
                <value>false</value>
        </property>
   ```
   
