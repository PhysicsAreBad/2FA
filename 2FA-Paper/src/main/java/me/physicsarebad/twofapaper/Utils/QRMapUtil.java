package me.physicsarebad.twofapaper.Utils;

import me.physicsarebad.twofapaper.TwoFAPaper;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class QRMapUtil extends MapRenderer {
    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        BufferedImage img = TwoFAPaper.getInstance().getPlayerController().getImage(player);
        map.setScale(MapView.Scale.NORMAL);
        canvas.drawImage(0,0,img);
    }
}
