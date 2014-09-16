name := "demo-solr"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.apache.commons" % "commons-lang3" % "3.1",
  "org.apache.solr" % "solr-solrj" % "4.7.2"
)     

play.Project.playJavaSettings
