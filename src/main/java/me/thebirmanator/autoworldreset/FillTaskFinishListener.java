package me.thebirmanator.autoworldreset;

import com.wimbli.WorldBorder.Events.WorldBorderFillFinishedEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FillTaskFinishListener implements Listener {

    @EventHandler
    public void onFinish(WorldBorderFillFinishedEvent event) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(ChatColor.DARK_PURPLE + "Reset Complete!", ChatColor.LIGHT_PURPLE + event.getWorld().getName(), 20, 40, 20);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("You may now go back into the world!").create());
        }
    }
}
