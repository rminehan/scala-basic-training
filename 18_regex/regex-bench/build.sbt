name := "regex-bench"

version := "1.0"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  "org.typelevel" %% "spire" % "0.17.0",
  "org.scalatest" %% "scalatest" % "3.2.2" % "test",
  "com.storm-enroute" %% "scalameter" % "0.19" % "test"
)

// Related to scalameter - https://scalameter.github.io/home/gettingstarted/0.7/sbt/
testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")
logBuffered := false
parallelExecution in Test := false
