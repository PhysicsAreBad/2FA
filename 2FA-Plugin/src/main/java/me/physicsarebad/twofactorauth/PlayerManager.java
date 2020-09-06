package me.physicsarebad.twofactorauth;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerManager implements Listener {
    private List<ProxiedPlayer> frozenPlayers = new ArrayList<>(); //Stores players who are frozen
    private HashMap<ProxiedPlayer, Integer> attempts = new HashMap<>(); //Stores failed attempts at entering code

    private HashMap<ProxiedPlayer, String> ipChanged = new HashMap<>(); //Circumvent a user logging on and off and back on to pass an IP check
    private HashMap<ProxiedPlayer, Long> timeChanged = new HashMap<>(); //Circumvent a user logging on and off and back on to pass a time check

    private HashMap<ProxiedPlayer, String> genKeys = new HashMap<>(); //Stores keys to prevent users who log out from the server before first-time verification was complete

    public boolean isFrozen(ProxiedPlayer p) {
        return frozenPlayers.contains(p);
    }

    public void addFrozen(ProxiedPlayer p) {
        //Add player to local array
        frozenPlayers.add(p);

        //Create data to send
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("freeze");
        out.writeUTF(p.getDisplayName());

        //Send data to all available servers
        for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
            server.sendData("twofactorauth:", out.toByteArray());
        }
    }

    public void removeFrozen(ProxiedPlayer p) {
        //Remove player from local arrays
        frozenPlayers.remove(p);
        attempts.remove(p);

        //Create data to send
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("unfreeze");
        out.writeUTF(p.getDisplayName());

        //Send data to all available servers
        for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
            server.sendData("twofactorauth:", out.toByteArray());
        }
    }

    public void addAttempt(ProxiedPlayer p) {
        if (attempts.containsKey(p)) {
            attempts.put(p, attempts.get(p)+1);
        } else {
            attempts.put(p, 1);
        }
    }

    public boolean maxAttempts(ProxiedPlayer p) {
        if (attempts.get(p) > 3) {
            return true;
        }
        return false;
    }

    public void addIPCheck(ProxiedPlayer p, String ip) {
        ipChanged.put(p, ip);
    }

    public String removeIPCheck(ProxiedPlayer p) {
        String ip = ipChanged.get(p);
        ipChanged.remove(p);
        return ip;
    }

    public boolean isIPChecked(ProxiedPlayer p) {
        return ipChanged.containsKey(p);
    }

    public void addTimeCheck(ProxiedPlayer p, Long time) {
        timeChanged.put(p, time);
    }

    public Long removeTimeCheck(ProxiedPlayer p) {
        long time = timeChanged.get(p);
        ipChanged.remove(p);
        return time;
    }

    public boolean isTimeChecked(ProxiedPlayer p) {
        return timeChanged.containsKey(p);
    }

    public void addKeyInfo(ProxiedPlayer p, String key) {
        genKeys.put(p, key);
    }

    public boolean isNew(ProxiedPlayer p) {
        return genKeys.containsKey(p);
    }

    public String getKeyInfo(ProxiedPlayer p) {
        if (genKeys.containsKey(p)) {
            return genKeys.get(p);
        } else {
            return null;
        }
    }
}
