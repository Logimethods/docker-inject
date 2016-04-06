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

//dockerCommands += ExecCmd("RUN", "mv", s"${(defaultLinuxInstallLocation in Docker).value}/sigar/libsigar-amd64-linux.so", "/app/libs/libsigar-amd64-linux.so")
//unmanagedResourceDirectories in Compile <+= (baseDirectory) {_ / "sigar"}

///resourceDirectories in Compile <+= baseDirectory / "sigar"

///mappings in (Compile, packageBin) ~= (_.filter { case (file, outpath) => outpath.startsWith("/sigar")} )

/*  lazy val sigarSettings = Seq(
    unmanagedSourceDirectories in (Compile,run) += baseDirectory.value.getParentFile / "sigar",
    javaOptions in run ++= {
      System.setProperty("java.library.path", file("./sigar").getAbsolutePath)
      Seq("-Xms128m", "-Xmx1024m")
    })*/

unmanagedSourceDirectories in (Compile,run) += baseDirectory.value.getParentFile / "sigar"

// Define a Dockerfile
dockerfile in docker := {
  val classpath = (managedClasspath in Compile).value
  val libs = "/app/libs"
  val sigar = "/sigar"

  
    // Add the sigar files to the libs
    // @see http://stackoverflow.com/questions/28676006/add-copy-files-with-sbt-native-packagers-docker-support
//    mappings in Universal += file(sigar + "/libsigar-amd64-linux.so") -> libs + "/libsigar-amd64-linux.so"
// map of (relativeName -> File) of all files in resources/docker dir, for convenience
  val dockerFiles = {
    val resources = (unmanagedResources in Runtime).value
println(resources.head.getPath)
    val dockerFilesDir = resources.find(_.getPath.endsWith("/sigar")).get
    resources.filter(_.getPath.contains("/sigar/")).map(r => dockerFilesDir.toURI.relativize(r.toURI).getPath -> r).toMap
  }

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

    add(dockerFiles("libsigar-amd64-linux.so"), s"$sigar/libsigar-amd64-linux.so")
//cmd("RUN", "mv", s"/sigar/libsigar-amd64-linux.so", "/app/libs/libsigar-amd64-linux.so")

	//To start the main app:
	//sbt powerdata-app/aspectj-runner:run  -Dcassandra.connection.host="localhost" -mem 4096
	//scala -classpath "app/libs/*" -Dcassandra.connection.host="cassandra" com.logimethods.powerdata.app.PowerDataApp
	//scala -classpath "app/libs/*" -Dcassandra.connection.host="cassandra" -Dakka.remote.netty.tcp.bind-hostname="localhost" -Dakka.remote.netty.tcp.bind-port="2552" -Dakka.remote.netty.tcp.hostname="localhost" -Dakka.remote.netty.tcp.port="2552" -J-Xmx4g com.logimethods.powerdata.app.PowerDataApp

    cmd("scala", "-classpath", s"app/libs/*;$sigar/*", "-Dcassandra.connection.host=\"cassandra\"", "com.logimethods.powerdata.app.PowerDataApp")
//    cmd("spark-shell")
//	cmd("bash")
  }
}