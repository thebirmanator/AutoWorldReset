package me.thebirmanator.autoworldreset.bukkit;

import me.thebirmanator.autoworldreset.bukkit.events.WorldResetEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ForceResetCmd implements CommandExecutor {

    public String forcereset = "forcereset";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender.hasPermission("autoworldreset.forcereset")) {
            if(args.length > 0) {
                World world = Bukkit.getWorld(args[0]);
                if(world != null && AutoWorldReset.getInstance().getResettingWorlds().contains(world)) {
                    Bukkit.getServer().getPluginManager().callEvent(new WorldResetEvent(world));
                    sender.sendMessage(ChatColor.GREEN + "Forcing world " + args[0] + " to reset!");
                } else {
                    sender.sendMessage(ChatColor.RED + "World " + args[0] + " not recognised.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Please provide the world to reset.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Sorry, you do not have permission to use this command.");
        }
        return true;
    }
}
