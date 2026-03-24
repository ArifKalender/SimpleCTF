package me.kugelbltz.simpleCTF.game.managers;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBarViewer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
        String title = "<#e30b00>Red score: <yellow>" + match.getScoreManager().getScore(Team.RED) + " <reset>| <#188adb>Blue score: <yellow>" + match.getScoreManager().getScore(Team.BLUE);
        float timeLeftNormalized = timeLeft / (float) StaticVariables.getMatchTime();
        this.bossBar.progress(timeLeftNormalized);
        this.bossBar.name(SimpleCTF.getInstance().getMM().deserialize(title));
    }

    public void createBossBar() {
        Component title = SimpleCTF.getInstance().getMM().deserialize(
                "<#e30b00>Red score: <yellow>" + match.getScoreManager().getScore(Team.RED) + " <reset>| <#188adb>Blue score: <yellow>" + match.getScoreManager().getScore(Team.BLUE));
        if (this.bossBar == null) {
            this.bossBar = BossBar.bossBar(title, 1, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
            match.getPlayers(Team.RED).forEach(this.bossBar::addViewer);
            match.getPlayers(Team.BLUE).forEach(this.bossBar::addViewer);
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
            List<Audience> audience = new ArrayList<>();
            for (BossBarViewer viewer : this.bossBar.viewers())
                if (viewer instanceof Audience) audience.add((Audience) viewer);
            for (Audience viewer : audience) {
                this.bossBar.removeViewer(viewer);
            }
            this.bossBar = null;
        }
    }

    /**
     * Removes the given player from the bossbar list
     */
    public void removePlayerFromBossBar(Player player) {
        if (getBossBar() == null) return;
        this.bossBar.removeViewer(player);
    }
}
