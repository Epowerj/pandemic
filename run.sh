#!/bin/sh

./gradlew dist
java -jar build/libs/pandemic-all-1.0-SNAPSHOT.jar
