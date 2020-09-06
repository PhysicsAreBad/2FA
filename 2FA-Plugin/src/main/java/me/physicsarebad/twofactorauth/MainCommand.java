package me.physicsarebad.twofactorauth;

import me.physicsarebad.twofactorauth.format.Format;
import me.physicsarebad.twofactorauth.generate.Generate;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class MainCommand extends Command {

    private Configuration config;
    private Configuration data;

    public MainCommand(Configuration config, Configuration data) {
        super("twofactorauthentication", "twofactorauth.admin", "2fa");
        this.config = config;
        this.data = data;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("reset")) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[1]);
                if (player != null) {
                    String uuid = player.getUniqueId().toString();
                    if (data.contains(uuid)) {
                        String key = Generate.generateSecretKey();
                        player.sendMessage(new TextComponent(Format.getColorString("Your new key is: " + ChatColor.YELLOW + key, config)));
                        data.set(uuid+".key", key);
                    }
                }
            }
        }
    }
}
