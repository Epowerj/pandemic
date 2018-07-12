#!/bin/sh

./gradlew clean dist
java -jar build/libs/pandemic-all-1.0-SNAPSHOT.jar
