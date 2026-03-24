package me.kugelbltz.simpleCTF.game.managers;

import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class MessageManager {

    private final Match match;
    private BossBar bossBar;

    public MessageManager(Match match) {
        this.match = match;
    }

    /**
     * Updates the bossbar
     *
     * @param timeLeft To update the health left of the boss bar
     */
    public void updateBossBar(int timeLeft) {
        if (this.bossBar == null) return;
        String title = "Red score: " + match.getScoreManager().getScore(Team.RED) + " | Blue score: " + match.getScoreManager().getScore(Team.BLUE);
        double timeLeftNormalized = timeLeft / (double) StaticVariables.getMatchTime();
        this.bossBar.setProgress(timeLeftNormalized);
        this.bossBar.setTitle(title);
    }

    public void createBossBar() {
        String title = "Red score: " + match.getScoreManager().getScore(Team.RED) + " | Blue score: " + match.getScoreManager().getScore(Team.BLUE);
        if (this.bossBar == null) {
            this.bossBar = Bukkit.createBossBar(title, BarColor.YELLOW, BarStyle.SOLID);
            match.getPlayers(Team.RED).forEach(this.bossBar::addPlayer);
            match.getPlayers(Team.BLUE).forEach(this.bossBar::addPlayer);
        }
    }

    /**
     * Broadcasts the given component to the players in the match
     *
     * @param component Message to send
     */
    public void broadcastMessage(Component component) {
        for (Team playableTeam : Team.playableTeams()) {
            match.getPlayers(playableTeam).forEach(player -> player.sendMessage(component));
        }
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    /**
     * Removes the players from the bossbar and sets it null
     */
    public void unloadBossBar() {
        if (getBossBar() != null) {
            this.bossBar.removeAll();
            this.bossBar = null;
        }
    }

    /**
     * Removes the given player from the bossbar list
     */
    public void removePlayerFromBossBar(Player player) {
        if (getBossBar() == null) return;
        this.bossBar.removePlayer(player);
    }
}
