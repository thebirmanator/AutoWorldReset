package me.thebirmanator.autoworldreset;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.wimbli.WorldBorder.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WorldResetListener implements Listener {
    private AutoWorldReset main;

    public WorldResetListener(AutoWorldReset main) {
        this.main = main;
    }

    @EventHandler
    public void onReset(WorldResetEvent event) {
        // send players to main world
        for(Player player : Bukkit.getOnlinePlayers()) {
            main.getServer().getConsoleSender().sendMessage("sending people to spawn");
            if(player.getWorld().equals(event.getWorld())) {
                player.teleport(main.getServer().getWorld("world").getSpawnLocation());
            }
        }

        // take away permission to enter this world
        main.getServer().dispatchCommand(Bukkit.getConsoleSender(), "lp group default permission unset multiverse.access." + event.getWorld().getName());

        MultiverseCore mv = main.getMVPlugin();

        // regenerating the world
        mv.regenWorld(event.getWorld().getName(), true, true, null);

        // start chunk regen
        // TODO: find out what this actually means
        Config.RestoreFillTask(event.getWorld().getName(), 200, 20,20, 20, 20, 20, 20);
    }
}
