//@see https://github.com/marcuslonnberg/sbt-docker
enablePlugins(DockerPlugin)

dockerfile in docker := new Dockerfile {
 from("sequenceiq/spark:latest")
}
