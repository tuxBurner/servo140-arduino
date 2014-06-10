name := "carrera-servo"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  cache,
  "com.github.tuxBurner" %% "play-twbs3" % "0.3",
  "org.webjars" %% "webjars-play" % "2.2.1-2",
  "org.webjars" % "bootstrap" % "3.1.0",
  "org.webjars" % "jquery" % "1.11.0-1",
  "org.webjars" % "font-awesome" % "4.0.3",
  "com.github.tuxBurner" %% "play-jsannotations" % "1.2.0",
  "com.github.tuxBurner" %% "play-neo4jplugin" % "1.3.5",
  "com.github.tuxBurner" %% "play-twbs3" % "0.1",
  "org.imgscalr" % "imgscalr-lib" % "4.2",
  "org.scream3r" % "jssc" % "2.8.0",
  "com.google.code.gson" % "gson" % "2.2.4"
)     

resolvers ++= Seq(
  "tuxburner.github.io" at "http://tuxburner.github.io/repo",
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
  "Neo4j" at "http://m2.neo4j.org/content/repositories/releases/"
)


play.Project.playScalaSettings
