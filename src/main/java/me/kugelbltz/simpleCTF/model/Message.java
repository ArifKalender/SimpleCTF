package me.kugelbltz.simpleCTF.model;

import me.kugelbltz.simpleCTF.SimpleCTF;

/**
 * Messages from config.yml
 */
public enum Message {

    PREFIX("SimpleCTF.Strings.Prefix", "<#eb6434>SimpleCTF » <#e8cd33>"),
    NO_PERMISSION("SimpleCTF.Strings.Warnings.NoPermission", "<red>You don't have the permission to execute that command!"),
    NO_FRIENDLY_FIRE("SimpleCTF.Strings.Warnings.NoFriendlyFire", "You can not attack your teammates!"),
    INCORRECT_SYNTAX("SimpleCTF.Strings.Warnings.IncorrectSyntax", "That's an invalid command, are you sure you typed that correctly?"),
    BOSS_BAR_TEXT("SimpleCTF.Strings.BossBarText", "<red>Red score: <green>%red_score% <reset>| <blue>Blue score: <green>%blue_score%"),

    WRONG_BANNER_TEAM("SimpleCTF.Strings.Warnings.WrongBannerTeam", "You cannot break that flag!"),
    TEAM_JOIN("SimpleCTF.Strings.Queue.TeamJoin", "You joined the %color% team!"),
    TEAM_LEAVE("SimpleCTF.Strings.Queue.TeamLeave", "You left the team."),
    PLAYER_JOINED_TEAM("SimpleCTF.Strings.Queue.PlayerJoinedTeam", "%player% joined the team!"),
    PLAYER_LEFT_TEAM("SimpleCTF.Strings.Queue.PlayerLeftTeam", "%player% left the team."),
    ALREADY_IN_QUEUE("SimpleCTF.Strings.Queue.AlreadyInQueue", "You are already in a queue! Use /ctf leave"),
    TEAM_ALREADY_FULL("SimpleCTF.Strings.Queue.TeamAlreadyFull", "Sorry, but that team is full!"),
    NOT_IN_TEAM("SimpleCTF.Strings.Queue.NotInTeam", "You are not in a team."),

    MATCH_WIN("SimpleCTF.Strings.Match.MatchWin", "The %color% team won the match!"),
    MATCH_TIME_OUT("SimpleCTF.Strings.Match.MatchTimeOut", "The match ran out of time!"),
    MATCH_START("SimpleCTF.Strings.Match.MatchStart", "<bold>The match just started!"),
    PLAYER_CAUGHT_FLAG("SimpleCTF.Strings.Match.PlayerCaughtFlag", "%player% CAUGHT THE %color% FLAG!"),
    PLAYER_RETURN_FLAG("SimpleCTF.Strings.Match.PlayerReturnFlag", "%player% returned with %opposite_color% team's flag!"),
    PLAYER_PLACE_FLAG("SimpleCTF.Strings.Match.PlayerPlaceFlag", "%player% placed back their flag!"),
    MATCH_OCCUPIED("SimpleCTF.Strings.Match.MatchOccupied", "There already is a match going on in this server, sorry!"),
    CURRENT_SCORE("SimpleCTF.Strings.Match.CurrentScore", "Blue score: %blue_score% | Red score: %red_score%"),
    FLAG_DROPPED_AT("SimpleCTF.Strings.Match.FlagDroppedAt", "%player% dropped %color% flag at %coordinates%!"),
    FLAG_WAS_SAVED("SimpleCTF.Strings.Match.FlagWasSaved", "%color% flag was saved!");

    private final String configPath;
    private final String defaultMessage;

    Message(String configPath, String defaultMessage) {
        this.configPath = configPath;
        this.defaultMessage = defaultMessage;
    }

    public String getConfigPath() {
        return configPath;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    /**
     * Fetches the message from the configuration and prepends the prefix.
     */
    public String get() {
        String message = SimpleCTF.getInstance().getConfig().getString(this.configPath, this.defaultMessage);

        if (this == PREFIX) {
            return message;
        }

        String prefix = SimpleCTF.getInstance().getConfig().getString(PREFIX.configPath, PREFIX.defaultMessage);
        return prefix + message;
    }

    public String getNoPrefix() {
        return SimpleCTF.getInstance().getConfig().getString(this.configPath, this.defaultMessage);
    }
}