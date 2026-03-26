# SimpleCTF
## A simple single-instance Capture the Flag minigame for minecraft.

## Get started
### Build steps
* Clone project
* Go into the project directory
* Execute `.\gradlew clean build` on Windows or `./gradlew clean build` on Unix
* Output jar will be at `projectRoot/build/libs/`

### Test Environment
**Project tested under the following conditions:**
* OS: Windows 11
* Minecraft server version: 1.21.8-R0.1-SNAPSHOT
* Java version: Java 24

## Design Decisions
* MinPlayersPerTeam in config was not enforced due to ease of use by the plugin tester and /ctf start
* This plugin is designed to handle a single match per server instance. This reflects how large networks deploy minigames - each match runs in its own isolated server rather than multiplexing multiple matches on one server.
* Added permissions for admin commands, therefore for executing admin commands the executor will need either operator privileges or `simplectf.admin.*`
