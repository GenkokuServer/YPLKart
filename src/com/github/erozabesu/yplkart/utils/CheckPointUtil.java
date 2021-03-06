package com.github.erozabesu.yplkart.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.inventory.ItemStack;

import com.github.erozabesu.yplkart.data.CircuitConfig;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.object.Circuit;
import com.github.erozabesu.yplkart.object.Racer;
import com.github.erozabesu.yplutillibrary.reflection.Constructors;
import com.github.erozabesu.yplutillibrary.reflection.Methods;
import com.github.erozabesu.yplutillibrary.util.CommonUtil;
import com.github.erozabesu.yplutillibrary.util.ReflectionUtil;

/**
 * チェックポイントエンティティの取得、操作を行うユーティリティクラス。
 * @author erozabesu
 */
public class CheckPointUtil {

    final public static int checkPointHeight = 6;

    final private static ItemStack checkPointDisplayItem = new ItemStack(Material.PRISMARINE_SHARD);
    final private static ItemStack visibleCheckPointDisplayItem = new ItemStack(Material.PRISMARINE_CRYSTALS);

    // 〓 Get 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数circuitNameが名称のサーキットに設置されたチェックポイントのうち、引数locationを基点に半径radiusブロック以内に設置されたチェックポイント全てを配列で返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param circuitName サーキット名
     * @param location 基点となる座標
     * @param detectRadius チェックポイントを検出する範囲の半径
     * @param ignoreEntities 検出から除外するエンティティ
     * @return チェックポイントの配列
     */
    public static List<Entity> getNearbyCheckPoints(String circuitName, Location location, double detectRadius, Entity... ignoreEntities) {
        List<Entity> entityList = CommonUtil.getNearbyEntities(location.clone().add(0, CheckPointUtil.checkPointHeight, 0), detectRadius);
        List<Entity> ignoreList = Arrays.asList(ignoreEntities);

        Iterator<Entity> iterator = entityList.iterator();
        Entity tempEntity;
        while (iterator.hasNext()) {
            tempEntity = iterator.next();

            // 除外リストに含まれるエンティティの場合配列から削除
            if (ignoreList.contains(tempEntity)) {
                iterator.remove();
                continue;
            }

            // 引数circuitNameのサーキットに設置されたチェックポイントではないエンティティを配列から削除
            if (!CheckPointUtil.isSpecificCircuitCheckPointEntity(tempEntity, circuitName)) {
                iterator.remove();
                continue;
            }
        }

        if (entityList == null || entityList.isEmpty()) {
            return null;
        }

        return entityList;
    }

    /**
     * 引数circuitNameが名称のサーキットに設置されたチェックポイントのうち、引数locationを基点に半径radiusブロック以内に設置された最寄のチェックポイントを返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param circuitName サーキット名
     * @param location 基点となる座標
     * @param detectRadius チェックポイントを検出する範囲の半径
     * @param ignoreEntities 検出から除外するエンティティ
     * @return チェックポイントエンティティ
     */
    public static Entity getNearestCheckpoint(String circuitName, Location location, double detectRadius, Entity... ignoreEntities) {
        List<Entity> checkPointList = getNearbyCheckPoints(circuitName, location, detectRadius, ignoreEntities);
        if (checkPointList == null || checkPointList.isEmpty()) {
            return null;
        }

        return CommonUtil.getNearestEntity(checkPointList, location);
    }

    // 〓 Get InSight 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数circuitNameが名称のサーキットに設置されたチェックポイントのうち、
     * 引数locationを基点に視認できるチェックポイントを配列で返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param circuitName サーキット名
     * @param entity 基点となるエンティティ
     * @param sightThreshold 視野
     * @param ignoreEntities 検出から除外するエンティティ
     * @return チェックポイントの配列
     */
    public static List<Entity> getInSightNearbyCheckPoints(String circuitName, Location location, float sightThreshold, Entity... ignoreEntities) {
        int detectCheckPointRadius = ConfigEnum.item$detect_checkpoint_radius$tier3;
        List<Entity> entityList = CommonUtil.getNearbyEntities(location.clone().add(0, checkPointHeight, 0), detectCheckPointRadius);
        List<Entity> ignoreList = Arrays.asList(ignoreEntities);

        Iterator<Entity> iterator = entityList.iterator();
        Entity tempEntity;
        while (iterator.hasNext()) {
            tempEntity = iterator.next();

            // 除外リストに含まれるエンティティの場合配列から削除
            if (ignoreList.contains(tempEntity)) {
                iterator.remove();
                continue;
            }

            //引数circuitNameのサーキットに設置されたチェックポイントではないエンティティを配列から削除
            if (!isSpecificCircuitCheckPointEntity(tempEntity, circuitName)) {
                iterator.remove();
                continue;
            }

            // 視野に含まれていない場合配列から削除
            if (!CommonUtil.isLocationInSight(location, tempEntity.getLocation(), sightThreshold)) {
                iterator.remove();
                continue;
            }

            // 視線とチェックポイントの座標間に固形ブロックが存在する場合配列から削除
            if (!isVisibleCheckPointEntity(tempEntity)) {
                if (!CommonUtil.canSeeLocation(location, tempEntity.getLocation())) {
                    iterator.remove();
                    continue;
                }
            }
        }

        if (entityList == null || entityList.isEmpty()) {
            return null;
        }

        return entityList;
    }

    /**
     * 引数circuitNameが名称のサーキットに設置されたチェックポイントのうち、
     * 引数locationを基点に視認できる最寄のチェックポイントを返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param circuitName サーキット名
     * @param entity 基点となるエンティティ
     * @param sightThreshold 視野
     * @param ignoreEntities 検出から除外するエンティティ
     * @return チェックポイントエンティティ
     */
    public static Entity getInSightNearestCheckpoint(String circuitName, Location location, float sightThreshold, Entity... ignoreEntities) {
        List<Entity> checkPointList = getInSightNearbyCheckPoints(circuitName, location, sightThreshold, ignoreEntities);
        if (checkPointList == null || checkPointList.isEmpty()) {
            return null;
        }

        return CommonUtil.getNearestEntity(checkPointList, location);
    }

    // 〓 Get InSight / Detectable 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数circuitNameが名称のサーキットに設置されたチェックポイントのうち、引数entityの座標を基点に、
     * 引数entityから視認でき、かつ、検出範囲内のチェックポイントを配列で返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param circuitName サーキット名
     * @param eyeLocation 基点となる座標
     * @param sightThreshold 視野
     * @param ignoreEntities 検出から除外するエンティティ
     * @return チェックポイントの配列
     */
    public static List<Entity> getInSightAndDetectableNearbyCheckPoints(String circuitName, Location eyeLocation, float sightThreshold, Entity... ignoreEntities) {
        int detectCheckPointRadius = ConfigEnum.item$detect_checkpoint_radius$tier3;
        List<Entity> entityList = CommonUtil.getNearbyEntities(eyeLocation.clone().add(0, checkPointHeight, 0), detectCheckPointRadius);
        List<Entity> ignoreList = Arrays.asList(ignoreEntities);

        Iterator<Entity> iterator = entityList.iterator();
        Entity tempEntity;
        while (iterator.hasNext()) {
            tempEntity = iterator.next();

            // 除外リストに含まれるエンティティの場合配列から削除
            if (ignoreList.contains(tempEntity)) {
                iterator.remove();
                continue;
            }

            //引数circuitNameのサーキットに設置されたチェックポイントではないエンティティを配列から削除
            if (!isSpecificCircuitCheckPointEntity(tempEntity, circuitName)) {
                iterator.remove();
                continue;
            }

            // 視野に含まれていない場合配列から削除
            if (!CommonUtil.isLocationInSight(eyeLocation, tempEntity.getLocation(), sightThreshold)) {
                iterator.remove();
                continue;
            }

            // 視線とチェックポイントの座標間に固形ブロックが存在する場合配列から削除
            if (!isVisibleCheckPointEntity(tempEntity)) {
                if (!CommonUtil.canSeeLocation(eyeLocation, tempEntity.getLocation())) {
                    iterator.remove();
                    continue;
                }
            }

            // チェックポイントの検出範囲
            Integer detectRadius = getDetectCheckPointRadiusByCheckPointEntity(tempEntity);

            // 検出範囲が取得できなかった場合は配列から削除
            if (detectRadius == null) {
                iterator.remove();
                continue;
            }

            // エンティティとチェックポイントの距離が検出距離を超えている場合は配列から削除
            if (detectRadius * detectRadius < eyeLocation.distanceSquared(tempEntity.getLocation())) {
                iterator.remove();
                continue;
            }
        }

        if (entityList == null || entityList.isEmpty()) {
            return null;
        }

        return entityList;
    }

    /**
     * 引数circuitNameが名称のサーキットに設置されたチェックポイントのうち、引数entityの座標を基点に、
     * 引数entityから視認でき、かつ、検出範囲内の最寄のチェックポイントを返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param circuitName サーキット名
     * @param eyeLocation 基点となる座標
     * @param sightThreshold 視野
     * @param ignoreEntities 検出から除外するエンティティ
     * @return チェックポイントエンティティ
     */
    public static Entity getInSightAndDetectableNearestCheckpoint(String circuitName, Location eyeLocation, float sightThreshold, Entity... ignoreEntities) {
        List<Entity> checkPointList = getInSightAndDetectableNearbyCheckPoints(circuitName, eyeLocation, sightThreshold, ignoreEntities);
        if (checkPointList == null || checkPointList.isEmpty()) {
            return null;
        }

        return CommonUtil.getNearestEntity(checkPointList, eyeLocation);
    }

    // 〓 Get Unpassed 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数racerが参加中のサーキットに設置されたチェックポイントのうち、引数locationを基点に半径radiusブロック以内に設置された未通過のチェックポイント全てを配列で返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param racer 参加中のプレイヤーのRacerインスタンス
     * @param location 基点となる座標
     * @param radius 半径
     * @param ignoreEntities 検出から除外するエンティティ
     * @return チェックポイントの配列
     */
    public static List<Entity> getNearbyUnpassedCheckPoints(Racer racer, Location location, double radius, Entity... ignoreEntities) {
        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return null;
        }

        String currentLaps = racer.getCurrentLaps() <= 0 ? "" : String.valueOf(racer.getCurrentLaps());
        List<Entity> entityList = CommonUtil.getNearbyEntities(location.clone().add(0, CheckPointUtil.checkPointHeight, 0), radius);
        List<Entity> ignoreList = Arrays.asList(ignoreEntities);

        Iterator<Entity> iterator = entityList.iterator();
        Entity tempEntity;
        while (iterator.hasNext()) {
            tempEntity = iterator.next();

            // 除外リストに含まれるエンティティの場合配列から削除
            if (ignoreList.contains(tempEntity)) {
                iterator.remove();
                continue;
            }

            // 引数circuitNameのサーキットに設置されたチェックポイントではないエンティティを配列から削除
            if (!CheckPointUtil.isSpecificCircuitCheckPointEntity(tempEntity, circuit.getCircuitName())) {
                iterator.remove();
                continue;
            }

            // 通過済みのチェックポイントを配列から削除
            if (racer.getPassedCheckPointList().contains(currentLaps + tempEntity.getUniqueId().toString())) {
                iterator.remove();
                continue;
            }
        }

        if (entityList == null || entityList.isEmpty()) {
            return null;
        }

        return entityList;
    }

    /**
     * 引数racerが参加中のサーキットに設置されたチェックポイントのうち、引数locationを基点に半径radiusブロック以内に設置された最寄の未通過のチェックポイントを返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param racer 参加中のプレイヤーのRacerインスタンス
     * @param location 基点となる座標
     * @param detectRadius チェックポイントを検出する範囲の半径
     * @param ignoreEntities 検出から除外するエンティティ
     * @return チェックポイントエンティティ
     */
    public static Entity getNearestUnpassedCheckpoint(Racer racer, Location location, double detectRadius, Entity... ignoreEntities) {
        List<Entity> checkPointList = getNearbyUnpassedCheckPoints(racer, location, detectRadius, ignoreEntities);
        if (checkPointList == null || checkPointList.isEmpty()) {
            return null;
        }

        return CommonUtil.getNearestEntity(checkPointList, location);
    }

    // 〓 Get Unpassed / InSight / Detectable 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数racerが参加中のサーキットに設置されたチェックポイントのうち、引数locationを基点に、
     * 未通過かつ、引数racerインスタンスのプレイヤーから視認でき、かつ、検出範囲内のチェックポイントを配列で返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param racer 参加中のプレイヤーのRacerインスタンス
     * @param location 基点となる座標
     * @param sightThreshold 視野
     * @param ignoreEntities 検出から除外するエンティティ
     * @return チェックポイントの配列
     */
    public static List<Entity> getInSightAndDetectableNearbyUnpassedCheckPoints(Racer racer, Location location, float sightThreshold, Entity... ignoreEntities) {
        Circuit circuit = racer.getCircuit();
        if (circuit == null) {
            return null;
        }

        Player player = racer.getPlayer();

        String currentLaps = racer.getCurrentLaps() <= 0 ? "" : String.valueOf(racer.getCurrentLaps());
        int detectCheckPointRadius = ConfigEnum.item$detect_checkpoint_radius$tier3;
        List<Entity> entityList = CommonUtil.getNearbyEntities(location.clone().add(0, CheckPointUtil.checkPointHeight, 0), detectCheckPointRadius);
        List<Entity> ignoreList = Arrays.asList(ignoreEntities);

        Iterator<Entity> iterator = entityList.iterator();
        Entity tempEntity;
        while (iterator.hasNext()) {
            tempEntity = iterator.next();

            // 除外リストに含まれるエンティティの場合配列から削除
            if (ignoreList.contains(tempEntity)) {
                iterator.remove();
                continue;
            }

            // 引数circuitNameのサーキットに設置されたチェックポイントではないエンティティを配列から削除
            if (!CheckPointUtil.isSpecificCircuitCheckPointEntity(tempEntity, circuit.getCircuitName())) {
                iterator.remove();
                continue;
            }

            // 通過済みのチェックポイントを配列から削除
            if (racer.getPassedCheckPointList().contains(currentLaps + tempEntity.getUniqueId().toString())) {
                iterator.remove();
                continue;
            }

            // 視野に含まれていない場合配列から削除
            if (!CommonUtil.isLocationInSight(player, tempEntity.getLocation(), sightThreshold)) {
                iterator.remove();
                continue;
            }

            // 視線とチェックポイントの座標間に固形ブロックが存在する場合配列から削除
            if (!CheckPointUtil.isVisibleCheckPointEntity(tempEntity)) {
                if (!CommonUtil.canSeeLocation(player.getEyeLocation(), tempEntity.getLocation())) {
                    iterator.remove();
                    continue;
                }
            }

            // チェックポイントの検出範囲
            Integer detectRadius = getDetectCheckPointRadiusByCheckPointEntity(tempEntity);

            // 検出範囲が取得できなかった場合は配列から削除
            if (detectRadius == null) {
                iterator.remove();
                continue;
            }

            // プレイヤーとチェックポイントの距離が検出距離を超えている場合は配列から削除
            if (detectRadius < player.getLocation().distance(tempEntity.getLocation())) {
                iterator.remove();
                continue;
            }
        }

        if (entityList == null || entityList.isEmpty()) {
            return null;
        }

        return entityList;
    }

    /**
     * 引数racerが参加中のサーキットに設置されたチェックポイントのうち、引数locationを基点に、
     * 引数racerインスタンスのプレイヤーから視認でき、かつ、検出範囲内の最寄のチェックポイントを返す。<br>
     * チェックポイントが検出されなかった場合は{@code null}を返す。
     * @param racer 参加中のプレイヤーのRacerインスタンス
     * @param location 基点となる座標
     * @param sightThreshold 視野
     * @param ignoreEntities 検出から除外するエンティティ
     * @return チェックポイントエンティティ
     */
    public static Entity getInSightAndDetectableNearestUnpassedCheckpoint(Racer racer, Location location, float sightThreshold, Entity... ignoreEntities) {
        List<Entity> checkPointList = getInSightAndDetectableNearbyUnpassedCheckPoints(racer, location, sightThreshold, ignoreEntities);
        if (checkPointList == null || checkPointList.isEmpty()) {
            return null;
        }

        return CommonUtil.getNearestEntity(checkPointList, location);
    }

    // 〓 Get Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数entityのカスタムネームのChatColorから、対応するチェックポイントTierを返す。<br>
     * 対応するTierが存在しない場合は{@code null}を返す。
     * @param entity 取得するエンティティ
     * @return チェックポイントのTier数。対応するTierが存在しない場合は{@code null}を返す。
     */
    public static Integer getCheckPointEntityTier(Entity entity) {
        String nameColor = ChatColor.getLastColors(entity.getCustomName());

        if (nameColor.contains("a")) {
            return 1;
        } else if (nameColor.contains("9")) {
            return 2;
        } else if (nameColor.contains("c")) {
            return 3;
        }

        return null;
    }

    /**
     * 引数entityのカスタムネームカラーから、対応する階級のチェックポイントの検出距離を返す。<br>
     * 対応する設定がない場合は{@code null}を返す。
     * @param entity 取得するエンティティ
     * @return チェックポイントの検出距離
     */
    public static Integer getDetectCheckPointRadiusByCheckPointEntity(Entity entity) {
        Integer tier = getCheckPointEntityTier(entity);

        if (tier == null) {
            return null;
        }

        switch(tier) {
            case 1:
                return ConfigEnum.item$detect_checkpoint_radius$tier1;

            case 2:
                return ConfigEnum.item$detect_checkpoint_radius$tier2;

            case 3:
                return ConfigEnum.item$detect_checkpoint_radius$tier3;
        }

        return null;
    }

    // 〓 Is 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * 引数entityがチェックポイントエンティティかどうかを返す。<br>
     * ウィザースカル、もしくはアーマースタンドエンティティであり、かつ存在するサーキットと同名のカスタムネームを所有しているエンティティの場合trueを返す。
     * @param entity チェックするエンティティ
     * @return 引数entityがチェックポイントエンティティかどうか
     */
    public static boolean isCheckPointEntity(Entity entity) {
        if (!(entity instanceof WitherSkull) && !(entity instanceof ArmorStand)) {
            return false;
        }
        if (entity.getCustomName() == null) {
            return false;
        }

        String entityName = ChatColor.stripColor(entity.getCustomName());
        for (String circuitName : CircuitConfig.keyToArray()) {
            if (entityName.equalsIgnoreCase(circuitName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 引数entityが引数circuitNameが名称のサーキットに設置されたチェックポイントエンティティかどうかを返す。<br>
     * ウィザースカル、もしくはアーマースタンドエンティティであり、かつ引数circuitNameと同名のカスタムネームを所有しているエンティティの場合trueを返す。
     * @param entity チェックするエンティティ
     * @param circuitName サーキットの名称
     * @return 引数entityが引数circuitNameが名称のサーキットに設置されたチェックポイントエンティティかどうか
     */
    public static boolean isSpecificCircuitCheckPointEntity(Entity entity, String circuitName) {
        if (!(entity instanceof WitherSkull) && !(entity instanceof ArmorStand)) {
            return false;
        }
        if (entity.getCustomName() == null) {
            return false;
        }
        if (!ChatColor.stripColor(entity.getCustomName()).equalsIgnoreCase(ChatColor.stripColor(circuitName))) {
            return false;
        }
        return true;
    }

    /**
     * 引数entityが引数circuitNameが名称のサーキットに設置されたチェックポイントエンティティであり、かつTierが引数tierに一致しているかどうかを返す。<br>
     * ウィザースカル、もしくはアーマースタンドエンティティであり、かつ引数circuitNameと同名のカスタムネームを所有しているエンティティであり、<br>
     * かつTierが引数tierに一致している場合trueを返す。
     * @param entity チェックするエンティティ
     * @param circuitName サーキットの名称
     * @param tier チェックポイントエンティティの階級
     * @return 引数entityが引数circuitNameが名称のサーキットに設置されたチェックポイントエンティティであり、かつTierが引数tierに一致しているかどうか
     */
    public static boolean isCheckPointEntity(Entity entity, String circuitName, int tier) {
        if (!isSpecificCircuitCheckPointEntity(entity, circuitName)) {
            return false;
        }
        if (tier < 1 && 3 < tier) {
            return false;
        }

        String nameColor = ChatColor.getLastColors(entity.getCustomName());
        Integer entityTier = CheckPointUtil.getCheckPointEntityTier(entity);
        if (entityTier == null) {
            return false;
        }

        if (entityTier != tier) {
            return false;
        }

        return true;
    }

    /**
     * 引数entityが透過チェックポイントエンティティかどうかを返す。<br>
     * アーマースタンドエンティティではなく、旧チェックポイントであるウィザースカルエンティティの場合はfalseを返す。
     * @param entity チェックするエンティティ
     * @return 引数entityが透過チェックポイントエンティティかどうか
     */
    public static boolean isVisibleCheckPointEntity(Entity entity) {
        if (!isCheckPointEntity(entity)) {
            return false;
        }

        if (!(entity instanceof ArmorStand)) {
            return false;
        }

        ItemStack itemInHand = ((ArmorStand) entity).getItemInHand();
        if (itemInHand == null) {
            return false;
        }

        if (itemInHand.isSimilar(CheckPointUtil.checkPointDisplayItem)) {
            return false;
        }

        return true;
    }

    // 〓 Edit Entity 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    public static Entity createCheckPointEntity(Location location, String circuitname, boolean isVisible) {
        ArmorStand armorStand = location.getWorld().spawn(location.add(0, CheckPointUtil.checkPointHeight, 0), ArmorStand.class);

        armorStand.getLocation().setYaw(location.getYaw());
        armorStand.getLocation().setPitch(location.getPitch());
        armorStand.setCustomName(circuitname);
        armorStand.setCustomNameVisible(true);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);

        if (isVisible) {
            armorStand.setItemInHand(CheckPointUtil.visibleCheckPointDisplayItem);
        } else {
            armorStand.setItemInHand(CheckPointUtil.checkPointDisplayItem);
        }

        Object kartEntity = CommonUtil.getCraftEntity(armorStand);
        Object vector3f = ReflectionUtil.newInstance(Constructors.nmsVector3f, -26.0F + location.getPitch(), 1.00F, 0.0F);
        ReflectionUtil.invoke(Methods.nmsEntityArmorStand_setRightArmPose, kartEntity, vector3f);

        return armorStand;
    }
}
