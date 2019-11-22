package me.thebirmanator.autoworldreset.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class AutoWorldReset extends Plugin {

    private static AutoWorldReset instance;

    public void onEnable() {
        instance = this;
        getProxy().registerChannel("BungeeCord");
        getProxy().getPluginManager().registerListener(this, new PluginMessageReceiverListener());
    }

    public static AutoWorldReset getInstance() {
        return instance;
    }
}
