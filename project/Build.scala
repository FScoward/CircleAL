import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "CircleAL"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "postgresql" % "postgresql" % "9.1-902.jdbc4",
      "org.twitter4j" % "twitter4j-core" % "2.2.6",
      "org.scalaquery" %% "scalaquery" % "0.10.0-M1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here
    )

}
