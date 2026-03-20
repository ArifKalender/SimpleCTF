package me.kugelbltz.simpleCTF.game.listeners;

import me.kugelbltz.simpleCTF.commands.player.CTFLeave;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QueueListener implements Listener {

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CTFLeave.removePlayer(player, false);
    }

}
