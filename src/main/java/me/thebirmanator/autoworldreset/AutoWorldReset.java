package me.thebirmanator.autoworldreset;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AutoWorldReset extends JavaPlugin {

    public void onEnable() {
        Calendar calendar = new GregorianCalendar();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int resetDay = DayOfWeek.valueOf("MONDAY").getValue();

        getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "day of week: " + dayOfWeek + ", reset day: " + resetDay);
    }
}
