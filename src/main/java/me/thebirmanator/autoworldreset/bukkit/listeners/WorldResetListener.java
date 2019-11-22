package me.thebirmanator.autoworldreset.bukkit.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.thebirmanator.autoworldreset.bukkit.AutoWorldReset;
import me.thebirmanator.autoworldreset.bukkit.WorldResetEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class WorldResetListener implements Listener {
    private AutoWorldReset main;
    public WorldResetListener(AutoWorldReset main) {
        this.main = main;
    }

    @EventHandler
    public void onReset(WorldResetEvent event) {
        // send plugin message to bungee saying that a world is resetting
        AutoWorldReset.getInstance().sendData("startReset", event.getWorld().getName());

        // send players to main world
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getWorld().equals(event.getWorld())) {
                player.performCommand("spawn");
            }
        }

        // get commands before fill
        List<String> cmds = main.getConfig().getStringList(event.getWorld().getName() + ".before-fill-run");
        // append the fill command to the end of commands to run
        cmds.addAll(main.getConfig().getStringList(event.getWorld().getName() + ".fill-cmds"));

        for(String cmd : cmds) {
            main.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
    }
}
