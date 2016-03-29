//@see https://github.com/marcuslonnberg/sbt-docker
//@see https://github.com/marcuslonnberg/sbt-docker/blob/master/examples/package-spray/build.sbt
//@see https://velvia.github.io/Docker-Scala-Sbt/


import sbt.Keys.{artifactPath, libraryDependencies, mainClass, managedClasspath, name, organization, packageBin, resolvers, version}

name := "docker-inject"

organization := "logimethods"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq("com.datastax.killrweather" % "powerdata-app_2.11" % version.value)

enablePlugins(DockerPlugin)

// Define a Dockerfile
dockerfile in docker := {
  val classpath = (managedClasspath in Compile).value
  val libs = "/app/libs"

  new Dockerfile {
    // Use a base image that contain Java
    from("frolvlad/alpine-scala")
//    from("sequenceiq/spark:1.6.0")
 
    // Expose port 80
    expose(80)

    // Copy all dependencies to 'libs' in the staging directory
    classpath.files.foreach { depFile =>
      val target = file(libs) / depFile.name
      stageFile(depFile, target)
    }

    // Add the libs dir from the
    addRaw(libs, libs)

	//To start the main app:
	//sbt powerdata-app/aspectj-runner:run  -Dcassandra.connection.host="localhost" -mem 4096
//    cmd("scala", "-classpath", "app/libs/*.jar", "-e", "com.logimethods.powerdata.app.PowerDataApp")
//    cmd("spark-shell")
	cmd("bash")
  }
}