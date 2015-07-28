package com.github.erozabesu.yplkart.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.utils.Util;

public class ItemBananaTask extends BukkitRunnable {
    Circuit circuit;
    Entity banana;
    Location l;

    public ItemBananaTask(Circuit circuit, Entity banana, Location l) {
        this.circuit = circuit;
        this.banana = banana;
        this.l = l;
        circuit.addJammerEntity(banana);
    }

    @Override
    public void run() {
        if (!circuit.isRaceEnd()) {
            if (banana.isDead()) {
                if (circuit.isJammerEntity(this.banana)) {
                    circuit.removeJammerEntity(this.banana);

                    FallingBlock b = l.getWorld().spawnFallingBlock(l, Material.HUGE_MUSHROOM_1, (byte) 8);
                    b.setCustomName(ItemEnum.BANANA.getDisplayName());
                    b.setCustomNameVisible(false);
                    b.setDropItem(false);
                    Util.removeEntityCollision(b);

                    this.banana = b;
                    circuit.addJammerEntity(this.banana);
                    //誰かがバナナに接触してバナナが消滅していた場合
                } else {
                    this.cancel();
                    this.banana.remove();
                    return;
                }
            }
        } else {
            this.cancel();
            this.banana.remove();
            return;
        }

        banana.setVelocity(new Vector(0, 0.04, 0));
    }
}