/*

    LocalTime Expansion - Provides PlaceholderAPI placeholders to give player's local time
    Copyright (C) 2020 aBooDyy

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

 */

package net.aboodyy.localtime;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateManager implements Listener {

    private Map<UUID, String> timezones;

    DateManager() {
        timezones = new HashMap<>();
    }

    public String getDate(String format, String timezone) {
        Date date = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));

        return dateFormat.format(date);
    }

    public String getTimeZone(Player p) {
        final String FAILED = "[LocalTime] Couldn't get " + p.getName() + "'s timezone. Will use default timezone.";
        String timezone = TimeZone.getDefault().getID();

        if (timezones.containsKey(p.getUniqueId()))
            return timezones.get(p.getUniqueId());

        InetSocketAddress address = p.getAddress();
        if (address == null) {
            Bukkit.getLogger().info(FAILED);

            timezones.put(p.getUniqueId(), timezone);
            return timezone;
        }

        try {
            URL api = new URL("https://ipapi.co/" + address.getAddress().getHostAddress() + "/timezone/");
            URLConnection connection = api.openConnection();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            timezone = bufferedReader.readLine();
        } catch (Exception e) {
            Bukkit.getLogger().info(FAILED);
        }

        if (timezone.equalsIgnoreCase("undefined")) {
            Bukkit.getLogger().info(FAILED);
            timezone = TimeZone.getDefault().getID();
        }

        timezones.put(p.getUniqueId(), timezone);
        return timezone;
    }

    public void clear() {
        timezones.clear();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        timezones.remove(e.getPlayer().getUniqueId());
    }
}
