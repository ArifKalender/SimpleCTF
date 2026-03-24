package me.kugelbltz.simpleCTF.game.listeners.matchListeners;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Message;
import me.kugelbltz.simpleCTF.model.Team;
import me.kugelbltz.simpleCTF.util.GeneralUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CombatListener implements Listener {


    /**
     * Drop flags if the player dies during a match
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        Player player = event.getPlayer();
        if (!match.isPlayerInMatch(player)) return;
        GeneralUtils.dropAllFlags(player);
    }

    /**
     * Teleport the player after death to their flag location if they're in a match
     */
    @EventHandler
    public void onRespawn(PlayerPostRespawnEvent event) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        Player player = event.getPlayer();
        Team team = match.getTeam(player);
        if (!Team.playableTeams().contains(team)) return;
        Bukkit.getScheduler().runTaskLater(SimpleCTF.getInstance(), () -> player.teleport(match.getFlagManager().getFlagLocation(team)), 1);
    }


    /**
     * Prevent friendly fire
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        if (!(event.getDamager() instanceof Player attacker) || !(event.getEntity() instanceof Player victim)) return;
        if (!match.isPlayerInMatch(victim) || !match.isPlayerInMatch(attacker)) return;
        boolean sameTeam = match.getTeam(victim) == match.getTeam(attacker);
        if (sameTeam) {
            event.setCancelled(true);
            attacker.sendMessage(SimpleCTF.getInstance().getMM().deserialize(Message.NO_FRIENDLY_FIRE.get()));
        }
    }

}
