package me.kugelbltz.simpleCTF.events;

import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FlagScoreEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player scoringPlayer;
    private final Team capturingTeam;
    private final Team capturedTeam;

    public FlagScoreEvent(Player scoringPlayer, Team capturingTeam, Team capturedTeam) {
        this.scoringPlayer = scoringPlayer;
        this.capturingTeam = capturingTeam;
        this.capturedTeam = capturedTeam;
    }

    public Player getScoringPlayer() {
        return scoringPlayer;
    }

    public Team getCapturingTeam() {
        return capturingTeam;
    }

    public Team getCapturedTeam() {
        return capturedTeam;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
