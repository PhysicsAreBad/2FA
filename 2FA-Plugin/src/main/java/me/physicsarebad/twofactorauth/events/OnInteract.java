package me.physicsarebad.twofactorauth.events;

import me.physicsarebad.twofactorauth.PlayerManager;
import me.physicsarebad.twofactorauth.format.Format;
import me.physicsarebad.twofactorauth.generate.Generate;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;


public class OnInteract implements Listener {

    private Configuration config;
    private Configuration data;

    private PlayerManager playerManager;

    public OnInteract(Configuration config, Configuration data, PlayerManager playerManager) {
        this.config = config;
        this.data = data;
        this.playerManager = playerManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST) //Catch chat before command is processed
    public void onChat (ChatEvent e) {
        if (e.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) e.getSender();
            if (playerManager.isFrozen(player)) {
                e.setCancelled(true);
                if (!data.contains(player.getUniqueId().toString() + ".key")) {
                    if (e.getMessage().equals(Generate.getTOTPCode(playerManager.getKeyInfo(player)))) {
                        playerManager.removeFrozen(player);
                        player.sendMessage(Format.getColorMessage("messages.confirmed", config));
                        if (playerManager.isNew(player)) {
                            String key = playerManager.getKeyInfo(player);
                            data.set(player.getUniqueId().toString()+".key", key);
                            data.set(player.getDisplayName(), player.getUniqueId().toString());
                        }
                        if (playerManager.isIPChecked(player)) {
                            String ip = playerManager.removeIPCheck(player);
                            data.set(player.getUniqueId().toString()+".ip", ip);
                        }
                        if (playerManager.isTimeChecked(player)) {
                            long time = playerManager.removeTimeCheck(player);
                            data.set(player.getUniqueId().toString()+".time", time);
                        }
                    }
                } else if (e.getMessage().equals(Generate.getTOTPCode(data.getString(player.getUniqueId().toString() + ".key")))) {
                    playerManager.removeFrozen(player);
                    player.sendMessage(Format.getColorMessage("messages.confirmed", config));
                    if (playerManager.isNew(player)) {
                        String key = playerManager.getKeyInfo(player);
                        data.set(player.getUniqueId().toString()+".key", key);
                        data.set(player.getDisplayName(), player.getUniqueId().toString());
                    }
                    if (playerManager.isIPChecked(player)) {
                        String ip = playerManager.removeIPCheck(player);
                        data.set(player.getUniqueId().toString()+".ip", ip);
                    }
                    if (playerManager.isTimeChecked(player)) {
                        long time = playerManager.removeTimeCheck(player);
                        data.set(player.getUniqueId().toString()+".time", time);
                    }
                } else {
                    player.sendMessage(Format.getColorMessage("messages.incorrect", config));
                    playerManager.addAttempt(player);
                    if (playerManager.maxAttempts(player) && !playerManager.isNew(player)) {
                        player.disconnect(new TextComponent("Too many attempts at auth code."));
                    }
                }
                e.setMessage("");//Make sure message is not received as command
            }
        }
    }
}
