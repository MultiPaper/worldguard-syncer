package com.github.multipaper.worldguardsyncer;

import com.github.puregero.multilib.MultiLib;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.util.profile.Profile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class AddProfilesToWorldGuardCache implements Listener {

    public AddProfilesToWorldGuardCache(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        MultiLib.onString(plugin, "worldguardsyncer:addprofile", string -> {
            String[] parts = string.split("\t");
            String username = parts[0];
            UUID uuid = UUID.fromString(parts[1]);

            WorldGuard.getInstance().getExecutorService().submit(() ->
                    WorldGuard.getInstance().getProfileCache().put(new Profile(uuid, username)));
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        MultiLib.notify("worldguardsyncer:addprofile", event.getPlayer().getName() + "\t" + event.getPlayer().getUniqueId());
    }

}
