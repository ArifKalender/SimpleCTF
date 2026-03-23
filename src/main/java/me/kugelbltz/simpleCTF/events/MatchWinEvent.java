package me.kugelbltz.simpleCTF.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class MatchWinEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Collection<Player> winners;
    private final Collection<Player> losers;

    public MatchWinEvent(Collection<Player> winners, Collection<Player> losers) {
        this.winners = winners;
        this.losers = losers;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public Collection<Player> getWinners() {
        return winners;
    }

    public Collection<Player> getLosers() {
        return losers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
