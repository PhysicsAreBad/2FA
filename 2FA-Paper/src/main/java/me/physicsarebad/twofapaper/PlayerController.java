package me.physicsarebad.twofapaper;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.zxing.WriterException;
import me.physicsarebad.twofapaper.Utils.QRMapUtil;
import me.physicsarebad.twofapaper.Utils.QRUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PlayerController implements PluginMessageListener {

    private List<Player> frozen = new ArrayList<>();
    private HashMap<Player, BufferedImage> imageMap = new HashMap<>();

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String command = in.readUTF();
        String name = in.readUTF();
        Bukkit.getScheduler().scheduleSyncDelayedTask(TwoFAPaper.getInstance(), () -> { //Schedule task for later because message comes before player join.
            Player p = Bukkit.getPlayer(name);
            if (command.equals("freeze") && !frozen.contains(p)) {
                frozen.add(p);
            } else if (command.equals("unfreeze")) {
                frozen.remove(p);
                if (p != null && p.getInventory().getItem(1) != null && p.getInventory().getItem(1).getType() == Material.FILLED_MAP) {
                    p.getInventory().setItem(1, null);
                }
                p.getInventory().setItem(0, null);
            } else if (command.equals("qr")) {
                if (p != null) {
                    String key = in.readUTF();
                    String url = QRUtil.getGoogleAuthenticatorBarCode(key, "VenomMC-2FA", "VenomMC");
                    try {
                        imageMap.put(p, QRUtil.createQRCode(url));
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }

                    ItemStack is = new ItemStack(Material.FILLED_MAP);
                    p.getInventory().setItem(0, is);

                    MapMeta meta = (MapMeta) p.getInventory().getItem(0).getItemMeta();
                    MapView view = Bukkit.createMap(player.getWorld());
                    Iterator<MapRenderer> iter = view.getRenderers().iterator();
                    while (iter.hasNext()) {
                        view.removeRenderer(iter.next());
                    }

                    QRMapUtil renderer = new QRMapUtil();
                    view.addRenderer(renderer);
                    meta.setMapView(view);
                    is.setItemMeta(meta);

                    p.getInventory().setItem(1, is);
                    p.getInventory().setHeldItemSlot(1);
                }
            }
        }, 5L);
    }

    public boolean isFrozen(Player p) {
        return frozen.contains(p);
    }

    public BufferedImage getImage(Player p) {
        return imageMap.get(p);
    }
}
