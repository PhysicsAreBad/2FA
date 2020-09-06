package me.physicsarebad.twofactorauth;

import me.physicsarebad.twofactorauth.events.OnInteract;
import me.physicsarebad.twofactorauth.events.OnJoin;
import me.physicsarebad.twofactorauth.events.OnServerSwitch;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public final class TwoFactorAuth extends Plugin {
    //File path to YML files
    private File configFile = new File(getDataFolder(), "config.yml");
    private File dataFile = new File(getDataFolder(), "data.yml");

    //YML files
    private Configuration config;
    private Configuration data;

    //Plugin Messenger & holds frozen player data
    private PlayerManager playerManager = new PlayerManager();

    private static TwoFactorAuth instance;

    @Override
    public void onEnable() {
        instance = this;
        //Save config and data files
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        if (!configFile.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!dataFile.exists()) {
            try (InputStream in = getResourceAsStream("data.yml")) {
                Files.copy(in, dataFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            data = ConfigurationProvider.getProvider(YamlConfiguration.class).load(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Set-up autosave every 5 minutes
        getProxy().getScheduler().schedule(this, () -> {
            getLogger().info("Saving data file");
            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(data, dataFile);
            } catch (IOException e) {
                getLogger().warning("Unable to autosave data file! Next try will be at 5 minutes or sever shutdown, whichever is closer.");
            }
        }, 5, 5, TimeUnit.MINUTES);

        //Register Channel
        getProxy().registerChannel("twofactorauth:");

        //Register Events
        getProxy().getPluginManager().registerListener(this, new OnInteract(config, data, playerManager));
        getProxy().getPluginManager().registerListener(this, new OnJoin(config, data, playerManager));
        getProxy().getPluginManager().registerListener(this, new OnServerSwitch(playerManager));

        //Register Command
        getProxy().getPluginManager().registerCommand(this, new MainCommand(config, data));
    }

    @Override
    public void onDisable() {
        //Save data file
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(data, dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TwoFactorAuth getInstance() {
        return instance;
    }
}
