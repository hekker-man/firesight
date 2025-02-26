# Firesight Mod

Firesight is a Minecraft Fabric mod that provides visualization to help players understand fire spread mechanics in Minecraft. It uses Litematica and MaLiLib and offers real-time block highlighting to show where fire can spread.

An air block will turn orange if fire could spread to it. This works in a 10 block radius around the player.
This mod is pretty jank for the moment. There is no off switch, the colour can't be changed and the range is hard coded, but hey it's my first mod. I am not responsible for killing your frames, although if you have so many places for the fire to spread that you get lag, you probably have bigger issues.

This mod was made by yelling at ChatGPT for two days and now "I made a mod". Feel free to edit this mod or "make your own", the license is pretty free.

## Development
To build Firesight locally, follow these steps:
```sh
git clone https://github.com/hekker-man/firesight.git
cd firesight
./gradlew build
```
- The mod jar file will be located in `build/libs`.
