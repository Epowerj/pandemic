# Pandemic

This is an AI for the board game Pandemic. 
Made for a SSRP/REU research project  at Ohio Wesleyan University.

## Buiding

This project uses the Gradle build system. 

To run, use the included `run.sh` script or type `./gradlew run`

To compile a .jar file, do `./gradlew dist`

Most IDEs have support for importing gradle projects, which is probably a good idea for this project.
We used Intellij Idea.

## Data

Static map data is saved as a text in `cities.txt`

The format is `<Color Char> <City name> <City names of adjacent cities>`

The 'U' color code stands for black.

## Documents

Take a look at the `/doc` folder for the documents related to this research.
