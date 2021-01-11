# Spark Install

Spark 환경변수 추가
    
 ```sh
 vim .bashrc
    
 <아래 내용 파일에 입력>
export SPARK_HOME=$HOME/spark-2.3.3-bin-hadoop2.7
export LD_LIBRARY_PATH=$HADOOP_HOME/lib/native
    
export PYTHONPATH=$SPARK_HOME/python:$SPARK_HOME/python/lib/py4j-0.10.7-src.zip:$PYTHONPATH
export PYSPARK_PYTHON=python3
아래 2줄은 Pyspark 사용을 위한 python3 버전 설정
```
    
    
    Spark tgz 다운로드 후 압축 해제
    
    ```sh
    wget https://archive.apache.org/dist/spark/spark-2.3.3/spark-2.3.3-bin-hadoop2.7.tgz
    tar -xzvf spark-2.3.3-bin-hadoop2.7.tgz
    ```
    
    Spark 기존 파일 복사
    파일 경로 : $SPARK_HOME/conf/
    
    ```sh
    cp slaves.template slaves
    cp spark-defaults.conf.template spark-defaults.conf
    cp spark-env.sh.template spark-env.sh
    ```
    
    복사한 설정파일 수정
    파일 경로 : $SPARK_HOME/conf/spark-defaults.conf
    마지막 줄 jar packages 추가는 Es-Hadoop 인터페이스 사용을 위해 추가
    
    ```sh
    spark.master            spark://master:7077
    spark.yarn.jars         hdfs://master:9000/jar/spark-jars/*.jar
    spark.jars.packages org.elasticsearch:elasticsearch-spark-20_2.11:7.3.2
    ```
    
    Spark 내부 환경변수 설정
    파일 경로 : $SPARK_HOME/conf/spark-env.sh
    
    ```sh
    export HADOOP_CONF_DIR=${HADOOP_DIR}/etc/hadoop
    export HADOOP_HOME=$HADOOP_HOME
    export YARN_CONF_DIR=${HADOOP_HOME}/etc/hadoop
    export SPARK_WORKER_MEMORY=4g
    ```
    
    HDFS 실행 중, 모든 Spark jar 파일을 HDFS 경로에 업데이트
    처음 put 명령어 실행 시 오류가 발생할 수 있음, 다시 실행시 정상 동작
    yarn cluster 모드 실행을 위한 작업
    
    ```sh
    hdfs dfs –mkdir /jar
    hdfs dfs –mkdri /jar/spark-jars
    hdfs dfs –put $SPARK_HOME/jars/* /jar/spark-jars/
    ```
    
    
    복사 된 Spark jar 파일을 확인 
    <Hadoop Web 에서  Utilities 목록의 Browse the file system 클릭>
    Name jar 클릭시 Spark jar 파일이 들어갔는지 확인
    
    Cluster의 Spark를 한번에 실행
    Spark conf 폴더내의 slaves 파일을 복사
    
    ```sh
    cp slaves.template slaves
    vim slaves
    ```
    <파일 내에 자신의 클러스터 이름을 추가>
    /etc/hosts 경로에 작성한 별칭 추가하시면됩니다.
    Ex 파일 경로 : $SPARK_HOME/conf/slaves
    
    ```sh
    master
    second
    slave1
    #slave2....
    ```
    Spark 모든 노드 실행 방법
    
    ```sh
    $SPARK_HOME/sbin/start-all.sh
    ```
    
    Master에서 환경변수, 및 수정한 파일 및 폴더 각 노드에 배포
    
    ```sh
    scp .bashrc second:/home/hadoop/.bashrc
    scp -r spark-2.3.3-bin-hadoop2.7 second:/home/hadoop/
    ```
    
    Spark Shell 실행
    
    ```sh
    #stand alone 실행
    $SPARK_HOME/bin/spark-shell
    
    #yarn cluster mode 실행
    $SPARK_HOME/bin/spark-shell --master yarn
    ```
    
    Spark 종료
    
    ```sh
    $SPARK_HOME/sbin/stop-all.sh
    ```
    
    그 외 
    sparkContext 는 1개이상 만드려면 설정을 바꿔줘야한다.
    SparkSession에 sparkContext가 포함되어있으므로 sparkSession객체의 sparkContext로 접근하면 가능
    sparkContext를 사용해야하는곳 sc.... -> spark.sparkContext....
    
