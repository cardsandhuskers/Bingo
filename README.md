# Bingo
This is Minecraft Bingo for 1.19.x
It is a game where players need to collect resources and craft as many items in the bingo card as they can in the provided time.
All players have speed 2 and efficiency 2 iron tools given to them on game start.

The config contains a list of item types, should be at least 25 items long but can be longer and it will randomly pick 25 items from the list.
Less than 25 items will not break anything

You can specify the number of teams that are allowed to earn points for crafting an item before it becomes locked out (numItemsObtainable)

Radius is for a square that the world border encapsulates

Players are teleported to the highest non-air block at 0,0 in the bingo world (this may be water and is an issue that needs to be fixed)
kv
# Commands:

**/reloadbingoworld**
- Deletes the old bingo world and then generates a new world with a random seed (player inputted seed planned for future)
- Uses Chunky to preload all chunks inside the worldborder
- Make sure no players are in the world when you run this (it will not work otherwise)!
  
**/setbingolobby**
- Sets the main lobby that the plugin will teleport all players to after the game ends to where the player is standing
  
**/startbingo [multiplier]**
- Starts the bingo game. The multiplier can be any double, represents the multiplied points

# Dependencies
- [Teams Plugin](https://github.com/cardsandhuskers/TeamsPlugin)

## Compilation 

Download the teams plugin and set Dfile to the location of the jar file

```
mvn install:install-file -Dfile="TeamsPlugin.jar" -DgroupId=io.github.cardsandhuskers -DartifactId=Teams -Dversion=1.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
```

Once the neccessary project has been established, type:

```
mvn package
```
