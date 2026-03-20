package me.kugelbltz.simpleCTF.game.listeners;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.util.UtilizationMethods;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static me.kugelbltz.simpleCTF.SimpleCTF.BANNER_ITEMS;

public class MatchListener implements Listener {

    Set<UUID> quitDuringMatch = new HashSet<>();
    // Implement match consequences
    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match.isPlayerInMatch(player)) quitDuringMatch.add(player.getUniqueId());
        match.getRedPlayers().remove(player);
        match.getBluePlayers().remove(player);
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) continue;
            player.getWorld().dropItem(player.getLocation(), itemStack);
        }
        match.broadcastMessage(MiniMessage.miniMessage().deserialize(ConfigManager.PLAYER_LEFT_TEAM.replaceAll("%player%", player.getName())));
        player.getInventory().clear();
    }
    @EventHandler
    private void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(quitDuringMatch.contains(player.getUniqueId())) {
            player.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation());
            player.getInventory().clear();
            quitDuringMatch.remove(player.getUniqueId());
        }
    }

    // Handle item drop
    @EventHandler
    private void onDrop(PlayerDropItemEvent event) {
        boolean isRedFlag = event.getItemDrop().getItemStack() == BANNER_ITEMS.redFlag;
        boolean isBlueFlag = event.getItemDrop().getItemStack() == BANNER_ITEMS.blueFlag;
        if (!isRedFlag && !isBlueFlag) return;
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (isRedFlag) match.setRedFlagCarrier(null);
        if (isBlueFlag) match.setBlueFlagCarrier(null);
    }

    // Handle item pickup
    @EventHandler
    private void onPickup(PlayerAttemptPickupItemEvent event) {
        boolean isRedFlag = event.getItem().getItemStack() == BANNER_ITEMS.redFlag;
        boolean isBlueFlag = event.getItem().getItemStack() == BANNER_ITEMS.blueFlag;
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        Player player = event.getPlayer();
        if (!isRedFlag && !isBlueFlag) return;
        if (!match.isPlayerInMatch(player)) {
            event.setCancelled(true);
            return;
        }
        if (isBlueFlag) match.setBlueFlagCarrier(player);
    }

    // Implement flag interactions
    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        switch (clickedBlock.getType()) {
            case Material.BLUE_BANNER -> {
                handleFlag(event, "BLUE");
                return;
            }
            case Material.RED_BANNER -> {
                handleFlag(event, "RED");
                return;
            }
        }
    }
    
    private void handleFlag(PlayerInteractEvent event, String flagColor) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        Player player = event.getPlayer();
        String playerColor = match.getBluePlayers().contains(player) ? "BLUE" : "RED";
        flagColor = flagColor.toUpperCase();
        if (playerColor.equalsIgnoreCase(flagColor)) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.WRONG_BANNER_TEAM));
            return;
        }
        event.getClickedBlock().setType(Material.AIR);
        String message;
        if (flagColor.equalsIgnoreCase("BLUE")) {
            UtilizationMethods.addItem(player, BANNER_ITEMS.blueFlag);
            message = ConfigManager.PLAYER_CAUGHT_FLAG.replaceAll("%player%", player.getName()).replaceAll("%color%", "BLUE");
            match.setBlueFlagCarrier(player);
        } else if (flagColor.equalsIgnoreCase("RED")) {
            UtilizationMethods.addItem(player, BANNER_ITEMS.redFlag);
            message = ConfigManager.PLAYER_CAUGHT_FLAG.replaceAll("%player%", player.getName()).replaceAll("%color%", "RED");
            match.setRedFlagCarrier(player);
        } else message="";
        match.broadcastMessage(MiniMessage.miniMessage().deserialize(message));
    }

    // Prevent friendly fire
    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker) || !(event.getEntity() instanceof Player victim)) return;
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        boolean bothBlue = match.getBluePlayers().contains(victim) && match.getBluePlayers().contains(attacker);
        boolean bothRed = match.getRedPlayers().contains(victim) && match.getRedPlayers().contains(attacker);
        boolean sameTeam = bothBlue || bothRed;
        if (sameTeam) {
            event.setCancelled(true);
            attacker.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.NO_FRIENDLY_FIRE));
        }
    }
}
