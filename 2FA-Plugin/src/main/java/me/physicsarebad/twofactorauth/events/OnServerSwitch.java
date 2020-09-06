package me.physicsarebad.twofactorauth.events;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.physicsarebad.twofactorauth.PlayerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class OnServerSwitch implements Listener {
    private PlayerManager playerManager;

    public OnServerSwitch(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    /**
     * Prevent players from switching servers to evade 2FA freeze.
     * @param e Event
     */
    @EventHandler
    public void onServerSwitch(ServerConnectedEvent e) {
        if (playerManager.isFrozen(e.getPlayer())) {
            //Create data to send
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("freeze");
            out.writeUTF(e.getPlayer().getDisplayName());

            //Send data to all available servers
            for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
                server.sendData("twofactorauth:", out.toByteArray());
            }
        }

        if (playerManager.isNew(e.getPlayer())) {
            //Create data to send
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("qr");
            out.writeUTF(e.getPlayer().getDisplayName());
            out.writeUTF(playerManager.getKeyInfo(e.getPlayer()));
            e.getServer().sendData("twofactorauth:", out.toByteArray());
        }
    }
}
