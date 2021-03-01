build:
	mvn clean compile

jar:
	mvn package -DskipTests

link:
	mkdir -p bin ; ln -sf `find $(shell pwd) -name *impl*jar` bin/app.jar

drop-migrate:
	mvn -pl car-entry-control-db liquibase:dropAll liquibase:update

migrate:
	mvn -pl car-entry-control-db liquibase:update
