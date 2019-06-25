package me.thebirmanator.autoworldreset;

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.Config;
import com.wimbli.WorldBorder.Events.WorldBorderFillFinishedEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class FillTaskFinishListener implements Listener {

    private AutoWorldReset main;
    public FillTaskFinishListener(AutoWorldReset main) {
        this.main = main;
    }

    @EventHandler
    public void onFinish(WorldBorderFillFinishedEvent event) {
        World world = event.getWorld();
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(ChatColor.DARK_PURPLE + "Reset Complete!", ChatColor.LIGHT_PURPLE + world.getName(), 20, 40, 20);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("You may now go back into the world!").create());
        }

        // set the vanilla world border
        BorderData pluginBorder = Config.getBorders().get(event.getWorld().getName());
        WorldBorder vanillaBorder = world.getWorldBorder();
        vanillaBorder.setCenter(pluginBorder.getX(), pluginBorder.getZ());
        // vanilla border uses a side length, not a radius. So multiply by two
        // we also want the vanilla border slightly outside the plugin border, so add a couple extra blocks
        vanillaBorder.setSize(pluginBorder.getRadiusX() * 2 + 2);

        // run all the commands for after the fill has finished
        List<String> cmds = main.getConfig().getStringList(world.getName() + ".after-fill-run");
        for(String cmd : cmds) {
            main.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
    }
}
