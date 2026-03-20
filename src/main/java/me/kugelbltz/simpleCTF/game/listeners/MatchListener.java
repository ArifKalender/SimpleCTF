package me.kugelbltz.simpleCTF.game.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MatchListener implements Listener {

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
    }

    // TODO: Implement flag interactions
    @EventHandler
    private void onInteract(PlayerInteractEvent event) {

    }

    // TODO: Prevention of friendly fire
    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {

    }
}
