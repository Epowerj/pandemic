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
The poster is probably the best way to get a quick understanding of how the program works.

## Future Work

There's definitely a lot more work to do. A good way to find stuff that needs work is to search
for the `//TODO` comments.
 
 - Certain game mechanics are ignored
   - Player roles are ignored by the ai even though many are supported by code
   - Event cards are not in the code, but it should be relatively easy to add by extending Card
 - The game starts with a bunch of extra research stations for debugging
 - The game uses 4 epidemic cards by default, but the code should automatically support up to 6
 - The current AI is limited to certain actions
   - The AI calculates time to lose using simulation and time to lose using probability
   - Both TTW and TTL could be improved
   - The AI can treat cubes and go for a cure
   - It can't place research stations or trade
 - In general, there needs to be more testing done to find out how the AI acts in different situations
