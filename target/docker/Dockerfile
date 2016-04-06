FROM frolvlad/alpine-scala
EXPOSE 8088
EXPOSE 2552
ADD /app/libs /app/libs
ADD 208/sigar /sigar
CMD ["scala", "-classpath", "app\/libs\/*;\/sigar\/*", "-Dcassandra.connection.host=\"cassandra\"", "com.logimethods.powerdata.app.PowerDataApp"]