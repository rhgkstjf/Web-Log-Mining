import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

object Compare{
  def main(args: Array[String]){
     val spark =SparkSession.builder.appName("CompareApp").getOrCreate()
     import spark.implicits._
     import org.apache.spark.sql.functions._
     import java.time.LocalDate
     import java.time.format.DateTimeFormatter

     val sc = spark.sparkContext
     sc.setLogLevel("WARN")


     val now = LocalDate.now()
     val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
     val today = formatter.format(now)

     //val esConf = Map("es.nodes" -> "220.69.209.54:9481")
     //val df = spark.read.format("org.elasticsearch.spark.sql").options(esConf).load("logstash-autolog")
     //df.createOrReplaceTempView("table")
     
     val LogDF = spark.read.json("/Auto/total.json")
     LogDF.createOrReplaceTempView("table")

     val Country_code = spark.sql("SELECT geoip.country_code2 AS id, geoip.country_name AS name, count(geoip.country_code2) AS value FROM table WHERE geoip.ip IS NOT NULL group by id,name ORDER BY value DESC")
     Country_code.coalesce(1).write.format("json").save("/Auto/Country")
     
     val allDF = spark.sql("SELECT geoip,user_agent,os,request,timestamp FROM table GROUP BY geoip, user_agent, os, request, timestamp HAVING geoip.ip IS NOT NULL")
     allDF.createOrReplaceTempView("all")
     allDF.cache()

     val Crawler = spark.sql("SELECT * FROM all a WHERE a.user_agent LIKE '%bingbot%' OR a.user_agent LIKE '%GalaxyBot%' OR a.user_agent LIKE '%GoogleBot%' OR a.user_agent LIKE '%Googlebot-Image%' OR a.user_agent LIKE '%Yeti%' OR a.user_agent LIKE '%kakaotalk%' OR a.user_agent LIKE '%Scrapy%' OR a.user_agent LIKE '%KISA%' OR a.user_agent LIKE '%Knowledge AI%' OR a.user_agent LIKE '%serpstatbot%'")
     val CrawlerDF = Crawler.withColumn("classfication",lit("Search"))
     CrawlerDF.cache()

     val Hacker = spark.sql("SELECT * FROM all a WHERE a.user_agent LIKE '%Baiduspider%' OR a.user_agent LIKE '%MJ12bot%' OR a.user_agent LIKE '%mj12bot%' OR a.user_agent LIKE 'Java%' OR a.user_agent LIKE '%SemrushBot%' OR a.user_agent LIKE '%DomainCrawler%' OR a.user_agent LIKE '%MegaIndex.ru%' OR a.user_agent LIKE '%AlphaBot%' OR a.user_agent LIKE '%AhrefsBot%' OR a.user_agent LIKE '%DotBot%' OR a.user_agent LIKE '%backlink%' OR a.request LIKE '%wp-%'  OR a.request LIKE '%phpmyadmin%' OR a.request LIKE '%index.php%'")
     val HackDF = Hacker.withColumn("classfication",lit("Hack"))
     HackDF.cache()

     val User = spark.sql("SELECT a.* FROM all a WHERE a.user_agent NOT LIKE '%Baiduspider%' AND a.user_agent NOT LIKE '%MJ12bot%' AND a.user_agent NOT LIKE '%mj12bot%' AND a.user_agent NOT LIKE 'Java%' AND a.user_agent NOT LIKE '%SemrushBot%' AND a.user_agent NOT LIKE '%DomainCrawler%' AND a.user_agent NOT LIKE '%MegaIndex.ru%' AND a.user_agent NOT LIKE '%AlphaBot%' AND a.user_agent NOT LIKE '%AhrefsBot%' AND a.user_agent NOT LIKE '%DotBot%' AND a.user_agent NOT LIKE '%backlink%' AND a.request NOT LIKE '%wp-%' AND a.request NOT LIKE '%phpmyadmin%' AND a.request NOT LIKE '%index.php%' AND a.user_agent NOT LIKE '%bingbot%' AND a.user_agent NOT LIKE '%GalaxyBot%' AND a.user_agent NOT LIKE '%GoogleBot%' AND a.user_agent NOT LIKE '%Googlebot-Image%' AND a.user_agent NOT LIKE '%Yeti%' AND a.user_agent NOT LIKE '%kakaotalk%' AND a.user_agent NOT LIKE '%Scrapy%' AND a.user_agent NOT LIKE '%KISA%' AND a.user_agent NOT LIKE '%Knowledge AI%' AND a.user_agent NOT LIKE '%serpstatbot%'")

     User.createOrReplaceTempView("User")
     val UserDF = User.withColumn("classfication",lit("User"))
     UserDF.cache()

     val union_f = UserDF.union(CrawlerDF)
     val union_s = union_f.union(HackDF)
     union_s.cache()
     CrawlerDF.unpersist()
     HackDF.unpersist()
     UserDF.unpersist()
     allDF.unpersist()

     union_s.createOrReplaceTempView("union")
     val urlDF = spark.read.json("/urldata/urldata.json")
     urlDF.createOrReplaceTempView("url")
     urlDF.cache()


     val Class_user = spark.sql("SELECT classfication, count(classfication) AS connect FROM union GROUP BY classfication")
     Class_user.coalesce(1).write.format("json").save("/Auto/Classfication")


     val patternKR = spark.sql("SELECT a.*,b.uid,b.postnum,b.postname FROM union a, url b where a.request LIKE CONCAT('%','uid=',b.uid,'%') AND a.geoip.country_code2 LIKE 'KR'")
     val patternORTHER = spark.sql("SELECT a.*,b.uid,b.postnum,b.postname FROM union a, url b where a.request LIKE CONCAT('%','uid=',b.uid,'%') AND a.geoip.country_code2 NOT LIKE 'KR'")
     val patternX = patternKR.withColumn("classfication_area",lit("KOREA"))
     val patternY = patternORTHER.withColumn("classfication-_area",lit("ORTHER"))

     val REALpattern = patternX.unionAll(patternY).distinct
     REALpattern.cache()
     REALpattern.createOrReplaceTempView("pattern")
     union_s.unpersist()

     val HackTop10 = spark.sql("SELECT geoip.country_code2, user_agent AS HackAgent, count(user_agent) AS AttackCount FROM union WHERE classfication LIKE 'Hack' GROUP BY user_agent, geoip.country_code2 ORDER BY AttackCount DESC LIMIT 10")
     HackTop10.coalesce(1).write.format("json").save("/Auto/Hack")


     val ContentsTop20 = spark.sql("SELECT postname AS Contents, postnum, uid, count(postname) AS connection_count FROM pattern GROUP BY postname,postnum,uid ORDER BY connection_count DESC LIMIT 20")
     ContentsTop20.coalesce(1).write.format("json").save("/Auto/Content")
    
     REALpattern.unpersist()
  }
}


