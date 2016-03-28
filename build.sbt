//@see https://github.com/marcuslonnberg/sbt-docker
//@see https://github.com/marcuslonnberg/sbt-docker/blob/master/examples/package-spray/build.sbt
//@see https://velvia.github.io/Docker-Scala-Sbt/


import sbt.Keys.{artifactPath, libraryDependencies, mainClass, managedClasspath, name, organization, packageBin, resolvers, version}

name := "docker-inject"

organization := "logimethods"

version := "0.1.0"

resolvers += "spray repo" at "http://repo.spray.io/"

libraryDependencies ++= Seq(
  "io.spray" % "spray-can" % "1.2.0",
  "io.spray" % "spray-routing" % "1.2.0",
  "com.typesafe.akka" %% "akka-actor" % "2.2.3")

enablePlugins(DockerPlugin)

// Define a Dockerfile
dockerfile in docker := {
  val classpath = (managedClasspath in Compile).value
  val libs = "/app/libs"

  new Dockerfile {
    // Use a base image that contain Java
    from("java")
    // Expose port 8080
    expose(8080)

    // Copy all dependencies to 'libs' in the staging directory
    classpath.files.foreach { depFile =>
      val target = file(libs) / depFile.name
      stageFile(depFile, target)
    }

    // Add the libs dir from the
    addRaw(libs, libs)

//    cmd("java", "-cp", classpathString, mainclass)
  }
}