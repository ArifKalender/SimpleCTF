# SimpleCTF
## A simple single-instance Capture the Flag minigame for minecraft.

## Get started
### Build steps
* Clone project
* Go into the project directory
* Execute `gradlew clean build`
* Output jar will be at `projectRoot/build/libs/`

## Design Decisions
* MinPlayersPerTeam in config was not enforced due to ease of use by the plugin tester and /ctf start
* This plugin is designed to handle a single match per server instance. This reflects how large networks deploy minigames - each match runs in its own isolated server rather than multiplexing multiple matches on one server.
