package me.physicsarebad.twofactorauth.events;

import me.physicsarebad.twofactorauth.PlayerManager;
import me.physicsarebad.twofactorauth.format.Format;
import me.physicsarebad.twofactorauth.generate.Generate;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

public class OnJoin implements Listener {

    private Configuration config;
    private Configuration data;

    private PlayerManager playerManager;

    public OnJoin(Configuration config, Configuration data, PlayerManager playerManager) {
        this.config = config;
        this.data = data;
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onJoin (PostLoginEvent e) {
        if (e.getPlayer().hasPermission("twofa.staff")) { //Don't 2FA regular users
            String uuid = e.getPlayer().getUniqueId().toString();
            String ip = e.getPlayer().getAddress().getAddress().toString();
            long time = System.currentTimeMillis()/3600000; //In hours
            if (data.contains(uuid+".key")) {
                long lastTime = data.getLong(uuid+".time");
                if (!data.getString(uuid+".ip").equals(ip)) { //IP Check
                    e.getPlayer().sendMessage(Format.getColorMessage("messages.login", config));
                    playerManager.addIPCheck(e.getPlayer(), ip);
                    playerManager.addTimeCheck(e.getPlayer(), time);
                } else if ((time-lastTime) >= config.getInt("timeout")) {
                    e.getPlayer().sendMessage(Format.getColorMessage("messages.login", config));
                    playerManager.addTimeCheck(e.getPlayer(), time);
                } else {
                    e.getPlayer().sendMessage(Format.getColorMessage("messages.session-active", config));
                    return;
                }
            } else if (data.contains(e.getPlayer().getDisplayName())) { //Prevent Cracked players using a name workaround
                String uuidPointer = data.getString(e.getPlayer().getDisplayName());
                long lastTime = data.getLong(uuidPointer+".time");
                if (!data.getString(uuidPointer+".ip").equals(ip)) { //IP Check
                    e.getPlayer().sendMessage(Format.getColorMessage("messages.login", config));
                    playerManager.addIPCheck(e.getPlayer(), ip);
                    playerManager.addTimeCheck(e.getPlayer(), time);
                } else if ((time-lastTime) >= config.getInt("timeout")) {
                    e.getPlayer().sendMessage(Format.getColorMessage("messages.login", config));
                    playerManager.addTimeCheck(e.getPlayer(), time);
                } else {
                    return;
                }
            } else {
                String key = Generate.generateSecretKey();
                playerManager.addKeyInfo(e.getPlayer(), key);
                playerManager.addIPCheck(e.getPlayer(), ip);
                playerManager.addTimeCheck(e.getPlayer(), time);
                e.getPlayer().sendMessage(Format.getColorMessage("messages.first-time", config));
                e.getPlayer().sendMessage(Format.getColorString("Your private key is: "+ ChatColor.YELLOW + key, config));
            }
            playerManager.addFrozen(e.getPlayer());
        }
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent e) {
        if (playerManager.isFrozen(e.getPlayer())) {
            playerManager.removeFrozen(e.getPlayer());
        }
    }
}
