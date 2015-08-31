package com.github.erozabesu.yplkart.task;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.github.erozabesu.yplkart.RaceManager;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.ItemEnum;
import com.github.erozabesu.yplkart.enumdata.Particle;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplkart.utils.Util;

public class ItemDyedTurtleTask extends BukkitRunnable {
    int life = 0;

    Entity projectile;
    Player shooter;
    Player target;

    double motX;
    double motY;
    double motZ;

    boolean isthornedturtle;
    boolean targetreverse;

    int adjustdamage;
    int lap;
    String lastStepBlock;

    //リアルタイムに取得するとこうらが逆走する場合がある
    ArrayList<String> shooterpassedcheckpoint = new ArrayList<String>();
    ArrayList<String> turtlepassedcheckpoint = new ArrayList<String>();

    public ItemDyedTurtleTask(Player shooter, Player target, Entity projectile, boolean isthornedturtle,
            boolean targetreverse) {
        this.projectile = projectile;
        this.target = target;

        this.isthornedturtle = isthornedturtle;
        this.targetreverse = targetreverse;

        this.target = target;
        this.shooter = shooter;
        this.adjustdamage = RaceManager.getRacer(shooter).getCharacter().getAdjustAttackDamage();
        this.lap = RaceManager.getRacer(shooter).getCurrentLaps();
        this.lastStepBlock = Util.getGroundBlockID(this.projectile.getLocation(), 5);

        shooterpassedcheckpoint = RaceManager.getRacer(shooter).getPassedCheckPointList();

        Util.removeEntityCollision(this.projectile);
    }

    private void die() {
        this.projectile.remove();
        this.cancel();
    }

    @Override
    public void run() {
        life++;

        Racer targerRacer = RaceManager.getRacer(target);

        //ターゲットがゴールしている場合デスポーンしタスクを終了
        if (targerRacer != null) {
            if (targerRacer.isGoal()) {
                die();
                return;
            }
        }

        if (!Util.isOnline(target.getName()) || 60 < life / 20) {
            die();
            return;
        }

        if (isthornedturtle) {
            Util.createSafeExplosion(this.shooter, this.projectile.getLocation()
                    , ItemEnum.THORNED_TURTLE.getMovingDamage() + this.adjustdamage, 5, 0.0F, 0.0F, Particle.CRIT_MAGIC, Particle.PORTAL);
        }

        //モーション 読み込まれていないチャンクに居る場合はMotionの値ずつテレポートで移動させる
        if (this.motX != 0 && this.motY != 0 && this.motZ != 0) {
            this.projectile.setVelocity(new Vector(this.motX, this.motY, this.motZ));

            if (!this.projectile.getLocation().getChunk().isLoaded()) {
                this.projectile.teleport(this.projectile.getLocation().clone().add(this.motX, this.motY, this.motZ));
            }
        }

        //周回数の更新
        if (lastStepBlock.equalsIgnoreCase(
                (String) ConfigEnum.START_BLOCK_ID.getValue())) {
            if (Util.getGroundBlockID(this.projectile.getLocation(), 5).equalsIgnoreCase(
                    (String) ConfigEnum.GOAL_BLOCK_ID.getValue())) {
                lap++;
            }
        }
        this.lastStepBlock = Util.getGroundBlockID(this.projectile.getLocation(), 5);

        //targetを発見したら突撃return
        List<LivingEntity> livingentity = Util.getNearbyLivingEntities(this.projectile.getLocation(), 20);
        for (LivingEntity target : livingentity) {
            if (this.target.getUniqueId().toString().equalsIgnoreCase(target.getUniqueId().toString())) {
                Vector v = Util.getVectorToLocation(this.projectile.getLocation(), target.getLocation())
                        .multiply(3);
                this.motX = v.getX();
                this.motY = v.getY();
                this.motZ = v.getZ();

                if (target.getLocation().distance(this.projectile.getLocation()) < 3) {
                    Util.createSafeExplosion(this.shooter, target.getLocation()
                            , ItemEnum.RED_TURTLE.getHitDamage() + this.adjustdamage, 3, 0.4F, 2.0F, Particle.EXPLOSION_LARGE);
                    die();
                }
                return;
            }
        }

        //チェックポイントの更新
        Racer r = RaceManager.getRacer(this.shooter);
        ArrayList<Entity> checkpointlist = new ArrayList<Entity>();

        //アカこうらを1位から2位に向け発射した場合
        if (this.targetreverse) {
            List<Entity> templist = RaceManager.getNearbyCheckpoint(
                    r.getCircuitName(), this.projectile.getLocation().clone().add(-this.motX * 3, 0, -this.motZ * 3), 30);
            if (templist == null)
                return;

            for (Entity e : templist) {
                if (this.shooterpassedcheckpoint.contains(lap + e.getUniqueId().toString())) {
                    if (!targerRacer.getPassedCheckPointList()
                            .contains(lap + e.getUniqueId().toString()))
                        if (!this.turtlepassedcheckpoint.contains(lap + e.getUniqueId().toString()))
                            checkpointlist.add(e);
                }
            }
            //その他
        } else {
            List<Entity> templist = RaceManager.getNearbyCheckpoint(
                    r.getCircuitName(), this.projectile.getLocation().clone().add(this.motX * 3, 0, this.motZ * 3), 30);
            if (templist == null)
                return;

            for (Entity e : templist) {
                if (!this.shooterpassedcheckpoint.contains(lap + e.getUniqueId().toString())) {
                    if (!this.turtlepassedcheckpoint.contains(lap + e.getUniqueId().toString()))
                        checkpointlist.add(e);
                }
            }
        }

        /*if(checkpoint.isEmpty()){
        	for (org.bukkit.entity.Entity e : templist) {
        		if(!r.getFirstPassedCheckPoint().equalsIgnoreCase(e.getUniqueId().toString())){
        			checkpoint.add(e);
        		}
        	}
        }*/
        if (checkpointlist.isEmpty())
            return;

        Entity checkpoint = Util.getNearestEntity(checkpointlist, this.projectile.getLocation());
        this.turtlepassedcheckpoint.add(lap + checkpoint.getUniqueId().toString());
        Vector v = Util.getVectorToLocation(this.projectile.getLocation(),
                checkpoint.getLocation().clone().add(0, -RaceManager.checkPointHeight, 0)).multiply(3);
        this.motX = v.getX();
        this.motY = v.getY();
        this.motZ = v.getZ();
    }
}
