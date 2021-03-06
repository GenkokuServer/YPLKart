package com.github.erozabesu.yplkart.object;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.erozabesu.yplkart.ConfigManager;
import com.github.erozabesu.yplkart.data.ConfigEnum;
import com.github.erozabesu.yplkart.data.MessageEnum;
import com.github.erozabesu.yplutillibrary.config.YamlLoader;
import com.github.erozabesu.yplutillibrary.util.CommonUtil;
import com.github.erozabesu.yplutillibrary.util.ReflectionUtil;

/**
 * Characterの各設定を格納するオブジェクトクラス
 * @author erozabesu
 */
public class Character {

    /** キャラクター名 */
    private String characterName;

    /** nmsEntityクラス */
    private Class<?> nmsClass;

    /** ヘッドブロックのプレイヤー名 */
    private String menuHeadBlockPlayerName;

    /** メニュークリック時の効果音 */
    private Sound menuClickSound;

    /** メニュークリック時の効果音の音量 */
    private float menuClickSoundVolume;

    /** メニュークリック時の効果音のピッチ */
    private float menuClickSoundPitch;

    /** 最大スロット数補正 */
    private int adjustMaxSlotSize;

    /** 最大スタック数補正 */
    private int adjustMaxStackSize;

    /** スピードエフェクトの秒数補正 */
    private int adjustPositiveEffectSecond;

    /** スピードエフェクトのLV補正 */
    private int adjustPositiveEffectLevel;

    /** スロウエフェクトの秒数補正 */
    private int adjustNegativeEffectSecond;

    /** スロウエフェクトのLV補正 */
    private int adjustNegativeEffectLevel;

    /** 攻撃力補正 */
    private int adjustAttackDamage;

    /** 最大体力 */
    private double maxHealth;

    /** 歩行速度 */
    private float walkSpeed;

    /** 死亡後無敵状態になる秒数 */
    private int penaltyAntiReskillSecond;

    /** デスペナルティの秒数 */
    private int penaltySecond;

    /** デスペナルティ時の歩行速度 */
    private float penaltyWalkSpeed;

    //〓 Main 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /**
     * コンストラクタ。<br>
     * 設定をメンバ変数へ格納する。<br>
     * デフォルト値はConfigManager.getDefaultConfig()からコンフィグキー「Steve.xxxxxx」の値を取得する。
     * @param key コンフィグキー
     */
    public Character(String key) {
        this.setCharacterName(key);

        YamlLoader config = ConfigManager.CHARACTER;
        String defaultKey = "Steve";
        YamlConfiguration defaultConfig = config.getDefaultConfig();

        String defaultEntityType = defaultConfig.getString(defaultKey + ".entity_type");
        Class<?> nmsClass = ReflectionUtil.getNMSClass("Entity" + config.getString(key + ".entity_type", defaultEntityType));
        this.setNmsClass(nmsClass);

        String defaultMenuHeadItemPlayerName = defaultConfig.getString(defaultKey + ".menu_head_item_player_name");
        this.setMenuHeadBlockPlayerName(config.getString(key + ".menu_head_item_player_name", defaultMenuHeadItemPlayerName));

        String defaultMenuClickSound = defaultConfig.getString(defaultKey + ".menu_click_sound");
        this.setMenuClickSound(config.getSound(key + ".menu_click_sound", defaultMenuClickSound));

        float defaultMenuClickSoundVolume = (float) defaultConfig.getDouble(defaultKey + ".menu_click_sound_volume");
        this.setMenuClickSoundVolume(config.getFloat(key + ".menu_click_sound_volume", defaultMenuClickSoundVolume));

        float defaultMenuClickSoundPitch = (float) defaultConfig.getDouble(defaultKey + ".menu_click_sound_pitch");
        this.setMenuClickSoundPitch(config.getFloat(key + ".menu_click_sound_pitch", defaultMenuClickSoundPitch));

        int defaultItemAdjustMaxSlot = defaultConfig.getInt(defaultKey + ".item_adjust_max_slot");
        this.setAdjustMaxSlotSize(config.getInt(key + ".item_adjust_max_slot", defaultItemAdjustMaxSlot));

        int defaultItemAdjustMaxStackSize = defaultConfig.getInt(defaultKey + ".item_adjust_max_stack_size");
        this.setAdjustMaxStackSize(config.getInt(key + ".item_adjust_max_stack_size", defaultItemAdjustMaxStackSize));

        int defaultItemAdjustPositiveEffectSecond = defaultConfig.getInt(defaultKey + ".item_adjust_positive_effect_second");
        this.setAdjustPositiveEffectSecond(config.getInt(key + ".item_adjust_positive_effect_second", defaultItemAdjustPositiveEffectSecond));

        int defaultItemAdjustPositiveEffectLevel = defaultConfig.getInt(defaultKey + ".item_adjust_positive_effect_level");
        this.setAdjustPositiveEffectLevel(config.getInt(key + ".item_adjust_positive_effect_level", defaultItemAdjustPositiveEffectLevel));

        int defaultItemAdjustNegativeEffectSecond = defaultConfig.getInt(defaultKey + ".item_adjust_negative_effect_second");
        this.setAdjustNegativeEffectSecond(config.getInt(key + ".item_adjust_negative_effect_second", defaultItemAdjustNegativeEffectSecond));

        int defaultItemAdjustNegativeEffectLevel = defaultConfig.getInt(defaultKey + ".item_adjust_negative_effect_level");
        this.setAdjustNegativeEffectLevel(config.getInt(key + ".item_adjust_negative_effect_level", defaultItemAdjustNegativeEffectLevel));

        int defaultItemAdjustAttackDamage = defaultConfig.getInt(defaultKey + ".item_adjust_attack_damage");
        this.setAdjustAttackDamage(config.getInt(key + ".item_adjust_attack_damage", defaultItemAdjustAttackDamage));

        double defaultMaxHealth = defaultConfig.getDouble(defaultKey + ".max_health");
        this.setMaxHealth(config.getDouble(key + ".max_health", defaultMaxHealth));

        float defaultWalkSpeed = (float) defaultConfig.getDouble(defaultKey + ".walk_speed");
        this.setWalkSpeed(config.getFloat(key + ".walk_speed", defaultWalkSpeed));

        int defaultDeathPenaltyAntiReskillSecond = defaultConfig.getInt(defaultKey + ".death_penalty.anti_reskill_second");
        this.setPenaltyAntiReskillSecond(config.getInt(key + ".death_penalty.anti_reskill_second", defaultDeathPenaltyAntiReskillSecond));

        int defaultDeathPenaltyPenaltySecond = defaultConfig.getInt(defaultKey + ".death_penalty.penalty_second");
        this.setPenaltySecond(config.getInt(key + ".death_penalty.penalty_second", defaultDeathPenaltyPenaltySecond));

        float defaultDeathPenaltyWalkSpeed = (float) defaultConfig.getDouble(defaultKey + ".death_penalty.walk_speed");
        this.setPenaltyWalkSpeed(config.getFloat(key + ".death_penalty.walk_speed", defaultDeathPenaltyWalkSpeed));
    }

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return メニュー用アイテム */
    public ItemStack getMenuItem() {
        ItemStack item = CommonUtil.createPlayerSkullByName(this.getMenuHeadBlockPlayerName());
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.GREEN + getCharacterName());
        meta.setLore(MessageEnum.replaceLine(
                MessageEnum.replaceChatColor(getParameter())));
        item.setItemMeta(meta);

        return item;
    }

    /** @return キャラクターのパラメーター一覧 */
    public String getParameter() {
        MessageParts textParts = MessageParts.getMessageParts(
                String.valueOf(this.getMaxHealth()),
                String.valueOf(this.getWalkSpeed()),
                String.valueOf(this.getAdjustMaxSlotSize() + ConfigEnum.settings$item_slot),
                CommonUtil.convertSignNumber(this.getAdjustMaxStackSize()),
                CommonUtil.convertSignNumber(this.getAdjustAttackDamage()),
                String.valueOf(this.getPenaltyAntiReskillSecond()),
                String.valueOf(this.getPenaltySecond()),
                String.valueOf(this.getPenaltyWalkSpeed()),
                CommonUtil.convertSignNumber(this.getAdjustPositiveEffectLevel()),
                CommonUtil.convertSignNumber(this.getAdjustPositiveEffectSecond()),
                CommonUtil.convertSignNumberR(this.getAdjustNegativeEffectLevel()),
                CommonUtil.convertSignNumberR(this.getAdjustNegativeEffectSecond()));

        return MessageEnum.tableCharacterParameter.getConvertedMessage(textParts);
    }

    public void playMenuSelectSound(Player player) {
        player.playSound(player.getLocation(), this.getMenuClickSound()
                , this.getMenuClickSoundVolume(), this.getMenuClickSoundPitch());
    }

    //〓 Getter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @return キャラクター名 */
    public String getCharacterName() {
        return this.characterName;
    }

    /** @return nmsEntityクラス */
    public Class<?> getNmsClass() {
        return this.nmsClass;
    }

    /** @return menuHeadBlockPlayerName ヘッドブロックのプレイヤー名 */
    public String getMenuHeadBlockPlayerName() {
        return this.menuHeadBlockPlayerName;
    }

    /**  @return メニュークリック時の効果音 */
    public Sound getMenuClickSound() {
        return this.menuClickSound;
    }

    /** @return メニュークリック時の効果音の音量 */
    public float getMenuClickSoundVolume() {
        return this.menuClickSoundVolume;
    }

    /** @return メニュークリック時の効果音のピッチ */
    public float getMenuClickSoundPitch() {
        return this.menuClickSoundPitch;
    }

    /** @return 最大スロット数補正 */
    public int getAdjustMaxSlotSize() {
        //アイテムスロットの上限は9
        if (9 < this.adjustMaxSlotSize + ConfigEnum.settings$item_slot)
            return 9;
        return this.adjustMaxSlotSize;
    }

    /** @return 最大スタック数補正 */
    public int getAdjustMaxStackSize() {
        return this.adjustMaxStackSize;
    }

    /** @return スピードエフェクトの秒数補正 */
    public int getAdjustPositiveEffectSecond() {
        return this.adjustPositiveEffectSecond;
    }

    /** @return スピードエフェクトのLV補正 */
    public int getAdjustPositiveEffectLevel() {
        return this.adjustPositiveEffectLevel;
    }

    /** @return スロウエフェクトの秒数補正 */
    public int getAdjustNegativeEffectSecond() {
        return this.adjustNegativeEffectSecond;
    }

    /** @return スロウエフェクトのLV補正 */
    public int getAdjustNegativeEffectLevel() {
        return this.adjustNegativeEffectLevel;
    }

    /** @return 攻撃力補正 */
    public int getAdjustAttackDamage() {
        return this.adjustAttackDamage;
    }

    /** @return 最大体力 */
    public double getMaxHealth() {
        return this.maxHealth;
    }

    /** @return 歩行速度 */
    public float getWalkSpeed() {
        return this.walkSpeed;
    }

    /** @return 死亡後無敵状態になる秒数 */
    public int getPenaltyAntiReskillSecond() {
        return this.penaltyAntiReskillSecond;
    }

    /** @return デスペナルティの秒数 */
    public int getPenaltySecond() {
        return this.penaltySecond;
    }

    /** @return デスペナルティ時の歩行速度 */
    public float getPenaltyWalkSpeed() {
        return this.penaltyWalkSpeed;
    }

    //〓 Setter 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** @param characterName キャラクター名 */
    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    /** @param nmsClass nmsEntityクラス */
    public void setNmsClass(Class<?> nmsClass) {
        this.nmsClass = nmsClass;
    }

    /** @param menuHeadBlockPlayerName ヘッドブロックのプレイヤー名 */
    public void setMenuHeadBlockPlayerName(String menuHeadBlockPlayerName) {
        this.menuHeadBlockPlayerName = menuHeadBlockPlayerName;
    }

    /** @param menuClickSound メニュークリック時の効果音 */
    public void setMenuClickSound(Sound menuClickSound) {
        this.menuClickSound = menuClickSound;
    }

    /** @param menuClickSoundVolume メニュークリック時の効果音の音量 */
    public void setMenuClickSoundVolume(float menuClickSoundVolume) {
        this.menuClickSoundVolume = menuClickSoundVolume;
    }

    /** @param menuClickSoundPitch メニュークリック時の効果音のピッチ */
    public void setMenuClickSoundPitch(float menuClickSoundPitch) {
        this.menuClickSoundPitch = menuClickSoundPitch;
    }

    /** @param adjustMaxSlotSize 最大スロット数補正 */
    public void setAdjustMaxSlotSize(int adjustMaxSlotSize) {
        this.adjustMaxSlotSize = adjustMaxSlotSize;
    }

    /** @param adjustMaxStackSize 最大スタック数補正 */
    public void setAdjustMaxStackSize(int adjustMaxStackSize) {
        this.adjustMaxStackSize = adjustMaxStackSize;
    }

    /** @param adjustPositiveEffectSecond スピードエフェクトの秒数補正 */
    public void setAdjustPositiveEffectSecond(int adjustPositiveEffectSecond) {
        this.adjustPositiveEffectSecond = adjustPositiveEffectSecond;
    }

    /** @param adjustPositiveEffectLevel スピードエフェクトのLV補正 */
    public void setAdjustPositiveEffectLevel(int adjustPositiveEffectLevel) {
        this.adjustPositiveEffectLevel = adjustPositiveEffectLevel;
    }

    /** @param adjustNegativeEffectSecond スロウエフェクトの秒数補正 */
    public void setAdjustNegativeEffectSecond(int adjustNegativeEffectSecond) {
        this.adjustNegativeEffectSecond = adjustNegativeEffectSecond;
    }

    /** @param adjustNegativeEffectLevel スロウエフェクトのLV補正 */
    public void setAdjustNegativeEffectLevel(int adjustNegativeEffectLevel) {
        this.adjustNegativeEffectLevel = adjustNegativeEffectLevel;
    }

    /** @param adjustAttackDamage 攻撃力補正 */
    public void setAdjustAttackDamage(int adjustAttackDamage) {
        this.adjustAttackDamage = adjustAttackDamage;
    }

    /** @param maxHealth 最大体力 */
    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    /** @param walkSpeed 歩行速度 */
    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    /** @param penaltyAntiReskillSecond 死亡後無敵状態になる秒数 */
    public void setPenaltyAntiReskillSecond(int penaltyAntiReskillSecond) {
        this.penaltyAntiReskillSecond = penaltyAntiReskillSecond;
    }

    /** @param penaltySecond デスペナルティの秒数 */
    public void setPenaltySecond(int penaltySecond) {
        this.penaltySecond = penaltySecond;
    }

    /** @param penaltyWalkSpeed デスペナルティ時の歩行速度 */
    public void setPenaltyWalkSpeed(float penaltyWalkSpeed) {
        this.penaltyWalkSpeed = penaltyWalkSpeed;
    }
}
