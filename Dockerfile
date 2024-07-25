FROM openjdk


COPY /build/libs/financetracker-0.0.1-SNAPSHOT.jar tracker.jar

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=default","tracker.jar"]