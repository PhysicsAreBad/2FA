package me.physicsarebad.twofapaper;

import me.physicsarebad.twofapaper.listeners.OnInteract;
import org.bukkit.plugin.java.JavaPlugin;

public final class TwoFAPaper extends JavaPlugin {

    private PlayerController playerController = new PlayerController();
    private static TwoFAPaper instance;

    public static TwoFAPaper getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        //Register incoming channel from master
        getServer().getMessenger().registerIncomingPluginChannel(this, "twofactorauth:", playerController);

        //Register Events
        getServer().getPluginManager().registerEvents(new OnInteract(playerController), this);
    }

    public PlayerController getPlayerController() {
        return playerController;
    }
}
