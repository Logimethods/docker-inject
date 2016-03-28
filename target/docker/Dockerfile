FROM frolvlad/alpine-scala
EXPOSE 80
ADD /app/libs /app/libs
CMD ["scala", "-classpath", "app\/libs", "-e", "com.logimethods.powerdata.app.PowerDataApp"]