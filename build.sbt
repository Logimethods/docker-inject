//@see https://github.com/marcuslonnberg/sbt-docker
//@see https://github.com/marcuslonnberg/sbt-docker/blob/master/examples/package-spray/build.sbt
//@see https://velvia.github.io/Docker-Scala-Sbt/


import sbt.Keys.{artifactPath, libraryDependencies, mainClass, managedClasspath, name, organization, packageBin, resolvers, version}

logLevel := Level.Debug

name := "docker-inject"

organization := "logimethods"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq("com.datastax.killrweather" % "powerdata-app_2.11" % version.value)

enablePlugins(DockerPlugin)

// Define a Dockerfile
dockerfile in docker := {
  val classpath = (managedClasspath in Compile).value
  val libs = "/app/libs"
  val sigar = "/sigar"

  new Dockerfile {
    // Use a base image that contain Java
    from("frolvlad/alpine-scala")
//    from("sequenceiq/spark:1.6.0")
 
    // Expose ports 8088 & 2552
    expose(8088)
    expose(2552)

    // Copy all dependencies to 'libs' in the staging directory
    classpath.files.foreach { depFile =>
      val target = file(libs) / depFile.name
      stageFile(depFile, target)
    }

    // Add the libs dir from the
    addRaw(libs, libs)

	add(baseDirectory.value / "sigar", libs)

	//To start the main app:
	//sbt powerdata-app/aspectj-runner:run  -Dcassandra.connection.host="localhost" -mem 4096
	//scala -classpath "app/libs/*" -Dcassandra.connection.host="cassandra" com.logimethods.powerdata.app.PowerDataApp
	//scala -classpath "app/libs/*" -Dcassandra.connection.host="cassandra" -Dakka.remote.netty.tcp.bind-hostname="localhost" -Dakka.remote.netty.tcp.bind-port="2552" -Dakka.remote.netty.tcp.hostname="localhost" -Dakka.remote.netty.tcp.port="2552" -J-Xmx4g com.logimethods.powerdata.app.PowerDataApp

    cmd("scala", "-classpath", s"app/libs/*", "-Dcassandra.connection.host=\"cassandra\"", "com.logimethods.powerdata.app.PowerDataApp")
//    cmd("spark-shell")
//	cmd("bash")
  }
}