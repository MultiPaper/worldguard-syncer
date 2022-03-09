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
    private static final String COMMAND_TO_EXCLUDE = "commandstoexclude.txt";
    private final HashSet<String> commandsToSync = new HashSet<>();
    private final HashSet<String> commandsToExclude = new HashSet<>();

    @Override
    public void onEnable() {
        loadCommandsToSync();
        loadCommandsToExclude();

        this.getServer().getPluginManager().registerEvents(this, this);

        new AddProfilesToWorldGuardCache(this);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (shouldSync(event.getMessage().toLowerCase(Locale.ROOT)) && MultiLib.isLocalPlayer(player) && hasWorldGuardPermission(player)) {
            MultiLib.chatOnOtherServers(player, event.getMessage());
        }
    }

    private boolean shouldSync(String message) {
        String[] args = message.split(" ");
        String command = args[0];

        return commandsToSync.contains(command) && !shouldNotSync(message);
    }

    private boolean shouldNotSync(String message) {
        for (String toExclude : commandsToExclude) {
            if (message.startsWith(toExclude)) {
                return true;
            }
        }

        return false;
    }

    private void loadCommandsToSync() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getResource(COMMAND_TO_SYNC))))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    commandsToSync.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCommandsToExclude() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getResource(COMMAND_TO_EXCLUDE))))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    commandsToExclude.add(line);
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
