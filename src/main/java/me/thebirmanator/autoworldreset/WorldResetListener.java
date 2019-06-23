package me.thebirmanator.autoworldreset;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        // send players to main world
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(ChatColor.DARK_PURPLE + "World Reset", ChatColor.LIGHT_PURPLE + event.getWorld().getName(), 20, 40, 20);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("We'll let you know when it's back up!").create());
            if(player.getWorld().equals(event.getWorld())) {
                player.teleport(main.getServer().getWorld("world").getSpawnLocation());
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
