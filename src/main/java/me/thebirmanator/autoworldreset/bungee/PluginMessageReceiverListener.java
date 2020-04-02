package me.thebirmanator.autoworldreset.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PluginMessageReceiverListener implements Listener {

    private Title title;

    public PluginMessageReceiverListener() {
        title = ProxyServer.getInstance().createTitle()
                .fadeIn(20)
                .stay(40)
                .fadeOut(20);
    }

    @EventHandler
    public void onReceive(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("BungeeCord")) {
            ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
            String subChannel = input.readUTF();
            if (!(subChannel.equalsIgnoreCase("startReset") || subChannel.equalsIgnoreCase("endReset"))) return;
            String world = input.readUTF();
            title.subTitle(new TextComponent(ChatColor.LIGHT_PURPLE + world));
            String actionBar = "";
            if (subChannel.equalsIgnoreCase("startReset")) {
                title.title(new TextComponent(ChatColor.DARK_PURPLE + "World Reset"));
                actionBar = "You will be able to join it within an hour or two.";
            } else if (subChannel.equalsIgnoreCase("endReset")) {
                title.title(new TextComponent(ChatColor.DARK_PURPLE + "Reset Complete!"));
                actionBar = "You may now go back into the world!";
            }
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                player.sendTitle(title);
                player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBar));
            }
        }
    }
}
