package me.kugelbltz.simpleCTF.game.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static me.kugelbltz.simpleCTF.SimpleCTF.getQueueHandler;

public class QueueListener implements Listener {

    /**
     * Removing the queue players if they leave when they're queued
     */
    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        getQueueHandler().removePlayer(player, false);
    }

}
