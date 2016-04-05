FROM frolvlad/alpine-scala
EXPOSE 8088
ADD /app/libs /app/libs
CMD ["scala", "-classpath", "app\/libs\/*", "-Dcassandra.connection.host=\"cassandra\"", "com.logimethods.powerdata.app.PowerDataApp"]