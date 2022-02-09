package com.github.multipaper.worldguardsyncer;

import com.github.puregero.multilib.MultiLib;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

public class WorldGuardSyncerPlugin extends JavaPlugin implements Listener {

    private static final String COMMAND_TO_SYNC = "commandstosync.txt";
    private final HashSet<String> worldGuardCommands = new HashSet<>();

    @Override
    public void onEnable() {
        loadWorldGuardCommands();

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String[] args = event.getMessage().split(" ");
        String command = args[0];

        if (worldGuardCommands.contains(command.toLowerCase(Locale.ROOT)) && MultiLib.isLocalPlayer(player) && hasWorldGuardPermission(player)) {
            MultiLib.chatOnOtherServers(player, event.getMessage());
        }
    }

    private void loadWorldGuardCommands() {
       try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getResource(COMMAND_TO_SYNC))))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    worldGuardCommands.add(line);
                }
            }
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
    }

    private boolean hasWorldGuardPermission(CommandSender commandSender) {
        if (commandSender.isOp()) {
            return true;
        }

        for (PermissionAttachmentInfo permission : commandSender.getEffectivePermissions()) {
            if (permission.getPermission().contains("worldguard")) {
                return true;
            }
        }

        return false;
    }

}
