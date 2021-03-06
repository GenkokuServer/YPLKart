package com.github.erozabesu.yplkart;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.erozabesu.yplutillibrary.config.LoaderAbstract;
import com.github.erozabesu.yplutillibrary.config.YamlLoader;

/**
 * 各コンフィグファイルのファイル管理を行うクラス
 * 設定データの取得はこのクラスからではなく
 * Enum、Configサフィックスの付いているクラスからstaticに取得すること
 *
 * Enumクラスは、ユーザ側で要素数を変更できない静的なコンフィグを扱う
 * コンフィグはEnumで管理する
 *
 * Configクラスは、ユーザ側で要素数を変更できる動的なコンフィグを扱う
 * コンフィグはオブジェクトで管理する
 *
 * @author erozabesu
 */
public class ConfigManager {
    private static List<LoaderAbstract> configList = new ArrayList<LoaderAbstract>();

    public static YamlLoader CONFIG;
    public static YamlLoader PERMISSION;
    public static YamlLoader ITEM;
    public static YamlLoader CHARACTER;
    public static YamlLoader KART;
    public static YamlLoader RACEDATA;
    public static YamlLoader DISPLAY;

    //〓 Util 〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

    /** 全設定データをローカルコンフィグファイルに保存する */
    public static void saveAllFile() {
        for (LoaderAbstract loader : configList) {
            loader.saveLocal();
        }
    }

    /** ローカルの全コンフィグファイルからYamlConfigurationを再読み込みする */
    public static void reloadAllFile() {
        saveAllFile();

        JavaPlugin plugin = YPLKart.getInstance();

        CONFIG = new YamlLoader(plugin, "config.yml");
        configList.add(CONFIG);

        PERMISSION = new YamlLoader(plugin, "permission.yml");
        configList.add(PERMISSION);

        ITEM = new YamlLoader(plugin, "item.yml");
        configList.add(ITEM);

        CHARACTER = new YamlLoader(plugin, "character.yml");
        configList.add(CHARACTER);

        KART = new YamlLoader(plugin, "kart.yml");
        configList.add(KART);

        RACEDATA = new YamlLoader(plugin, "racedata.yml");
        configList.add(RACEDATA);

        DISPLAY = new YamlLoader(plugin, "displaykart.yml");
        configList.add(DISPLAY);
    }
}
