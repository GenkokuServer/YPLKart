package main.java.com.github.erozabesu.YPLKart.Data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import main.java.com.github.erozabesu.YPLKart.YPLKart;
import main.java.com.github.erozabesu.YPLKart.Enum.EnumItem;
import main.java.com.github.erozabesu.YPLKart.Object.RaceManager;
import main.java.com.github.erozabesu.YPLKart.Utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class RaceData{
	public static YPLKart pl;

	private static String filename = "racedata.yml";
	private static File datafolder;
	private static File configFile;
	private static FileConfiguration config;

	private static boolean EnableThisPlugin = true;
	public static boolean EnableScoreboard = true;
	public static List<String> DisWorlds;

	public RaceData(YPLKart plugin){
		pl = plugin;
		datafolder = pl.getDataFolder();

		configFile = new File(datafolder, filename);
		config = YamlConfiguration.loadConfiguration(configFile);

		CreateConfig();
	}

	/* <circuit name>:
	 *   world: world
	 *   x: double
	 *   y: double
	 *   z: double
	 *   yaw: double
	 *   pitch: double
	 *   laptime:
	 *     <lapcount>:
	 *       <player name>: 123.456
	 *       <player name>: 123.456
	 *     <lapcount>:
	 *       <player name>: 123.456
	 *       <player name>: 123.456
	 * */

	public static void createCircuit(Player p, String circuitname){
		if(!getCircuitSet().contains(circuitname)){
			Location l = p.getLocation();
			config.set(circuitname + ".world", l.getWorld().getName());
			config.set(circuitname + ".x", l.getX());
			config.set(circuitname + ".y", l.getY());
			config.set(circuitname + ".z", l.getZ());
			config.set(circuitname + ".yaw", l.getYaw());
			config.set(circuitname + ".pitch", l.getPitch());
			Util.sendMessage(p, "#Greenサーキット：" + "#Gold" + circuitname + "#Greenを作成しました");
		}else{
			Util.sendMessage(p, "#Redサーキット：" + "#Gold" + circuitname + "#Redは既に作成済みです");
		}
		saveConfigFile();
	}

	public static void createCircuit(Player p, String circuitname, String worldname, double x, double y, double z, float yaw, float pitch){
		if(!getCircuitSet().contains(circuitname)){
			config.set(circuitname + ".world", worldname);
			config.set(circuitname + ".x", x);
			config.set(circuitname + ".y", y);
			config.set(circuitname + ".z", z);
			config.set(circuitname + ".yaw", yaw);
			config.set(circuitname + ".pitch", pitch);
			Util.sendMessage(p, "#Greenサーキット：" + "#Gold" + circuitname + "#Greenを作成しました");
		}else{
			Util.sendMessage(p, "#Redサーキット：" + "#Gold" + circuitname + "#Redは既に作成済みです");
		}
		saveConfigFile();
	}

	public static void deleteCircuit(Player p, String circuitname){
		for(String key : config.getKeys(false)){
			if(key.equalsIgnoreCase(circuitname)){
				config.set(key, null);
				for(World w : Bukkit.getWorlds()){
					for(Entity e : w.getEntities()){
						if(RaceManager.isCustomWitherSkull(e, circuitname))
							e.remove();
					}
				}
				saveConfigFile();
				Util.sendMessage(p, "#Greenサーキット：" + "#Gold" + circuitname + "#Greenを削除しました");
				return;
			}
		}
		Util.sendMessage(p, "#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
	}

	public static void listCricuit(Player p){
		Util.sendMessage(p, "#GoldCircuit List：\n" + getCircuitList());
	}

	public static void editCircuit(Player p, String circuitname){
		if(!getCircuitSet().contains(circuitname)){
			Util.sendMessage(p, "#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
		}else{
			p.getInventory().addItem(EnumItem.getCheckPointTool(EnumItem.CheckPoint, circuitname));
			Util.sendMessage(p, "サーキット：" + "#Gold" + circuitname + "#Greenのチェックポイントツールを配布しました");
		}
	}

	public static void renameCircuit(Player p, String name, String newname){
		if(!getCircuitSet().contains(name)){
			Util.sendMessage(p, "#Redサーキット：" + "#Gold" + name + "#Redは存在しません");
		}else if(getCircuitSet().contains(newname)){
			Util.sendMessage(p, "#Redサーキット：" + "#Gold" + newname + "#Redは既に作成済みです");
		}else{
			for(World w : Bukkit.getWorlds()){
				for(Entity e : w.getEntities()){
					if(RaceManager.isCustomWitherSkull(e, name))
						e.setCustomName(e.getCustomName().replace(name, newname));
				}
			}

			config.set(newname, config.get(name));
			config.set(name, null);
			saveConfigFile();

			Util.sendMessage(p, "#Greenサーキット：" + "#Gold" + name + "#Greenの名称を#Gold" + newname + "#Greenに変更しました");
		}
	}

	public static void setPosition(Player p, String circuitname){
		if(!getCircuitSet().contains(circuitname)){
			Util.sendMessage(p, "#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
		}else{
			Location l = p.getLocation();
			config.set(circuitname + ".world", l.getWorld().getName());
			config.set(circuitname + ".x", l.getX());
			config.set(circuitname + ".y", l.getY());
			config.set(circuitname + ".z", l.getZ());
			config.set(circuitname + ".yaw", l.getYaw());
			config.set(circuitname + ".pitch", l.getPitch());
			Util.sendMessage(p, "#Greenサーキット：" + "#Gold" + circuitname + "#Greenの開始座標を再設定しました");
			saveConfigFile();
		}
	}

	public static void setPosition(Player p, String circuitname, String worldname, double x, double y, double z, float yaw, float pitch){
		if(!getCircuitSet().contains(circuitname)){
			Util.sendMessage(p, "#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
		}else{
			config.set(circuitname + ".world", worldname);
			config.set(circuitname + ".x", x);
			config.set(circuitname + ".y", y);
			config.set(circuitname + ".z", z);
			config.set(circuitname + ".yaw", yaw);
			config.set(circuitname + ".pitch", pitch);
			Util.sendMessage(p, "#Greenサーキット：" + "#Gold" + circuitname + "#Greenの開始座標を再設定しました");
			saveConfigFile();
		}
	}

	public static void setGoalPosition(Player p, String circuitname){
		if(!getCircuitSet().contains(circuitname)){
			Util.sendMessage(p, "#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
		}else{
			Location l = p.getLocation();
			config.set(circuitname + ".goalposition.world", l.getWorld().getName());
			config.set(circuitname + ".goalposition.x", l.getX());
			config.set(circuitname + ".goalposition.y", l.getY());
			config.set(circuitname + ".goalposition.z", l.getZ());
			config.set(circuitname + ".goalposition.yaw", l.getYaw());
			config.set(circuitname + ".goalposition.pitch", l.getPitch());
			Util.sendMessage(p, "#Greenサーキット：" + "#Gold" + circuitname + "#Greenの終了座標を設定しました");
			saveConfigFile();
		}
	}

	public static void setGoalPosition(Player p, String circuitname, String worldname, double x, double y, double z, float yaw, float pitch){
		if(!getCircuitSet().contains(circuitname)){
			Util.sendMessage(p, "#Redサーキット：" + "#Gold" + circuitname + "#Redは存在しません");
		}else{
			config.set(circuitname + ".goalposition.world", worldname);
			config.set(circuitname + ".goalposition.x", x);
			config.set(circuitname + ".goalposition.y", y);
			config.set(circuitname + ".goalposition.z", z);
			config.set(circuitname + ".goalposition.yaw", yaw);
			config.set(circuitname + ".goalposition.pitch", pitch);
			Util.sendMessage(p, "#Greenサーキット：" + "#Gold" + circuitname + "#Greenの終了座標を設定しました");
			saveConfigFile();
		}
	}

	public static void addRunningRaceLapTime(Player p, String circuitname, double laptime){
		if(getCircuitSet().contains(circuitname)){

			String path = circuitname + ".laptime." + ((int)Settings.NumberOfLaps-1) + "." + p.getUniqueId().toString();
			if(!config.contains(path)){
				config.set(path, laptime);
				saveConfigFile();
				return;
			}

			if(laptime < config.getDouble(path, 0)){
				Util.sendMessage(p, "記録更新！#Yellow" + config.getDouble(path) + "#Green秒 --> #Yellow" + laptime + "#Green秒");
				config.set(path, laptime);
				saveConfigFile();
				return;
			}
		}
	}

	public static void addKartRaceLapTime(Player p, String circuitname, double laptime){
		if(getCircuitSet().contains(circuitname)){

			String path = circuitname + ".kartlaptime." + ((int)Settings.NumberOfLaps-1) + "." + p.getUniqueId().toString();
			if(!config.contains(path)){
				config.set(path, laptime);
				saveConfigFile();
				return;
			}

			if(laptime < config.getDouble(path, 0)){
				Util.sendMessage(p, "記録更新！#Yellow" + config.getDouble(path) + "#Green秒 --> #Yellow" + laptime + "#Green秒");
				config.set(path, laptime);
				saveConfigFile();
				return;
			}
		}
	}

	public static Location getPosition(String circuitname){
		if(!getCircuitSet().contains(circuitname))
			return null;
		if(Bukkit.getWorld(config.getString(circuitname + ".world")) == null)
			return null;

		return new Location(Bukkit.getWorld(config.getString(circuitname + ".world")),config.getDouble(circuitname + ".x"),config.getDouble(circuitname + ".y"),config.getDouble(circuitname + ".z"),(float)config.getDouble(circuitname + ".yaw"),(float)config.getDouble(circuitname + ".pitch"));
	}

	public static Location getGoalPosition(String circuitname){
		if(!getCircuitSet().contains(circuitname))
			return null;
		if(Bukkit.getWorld(config.getString(circuitname + ".world")) == null)
			return null;

		return new Location(Bukkit.getWorld(config.getString(circuitname + ".goalposition.world")),config.getDouble(circuitname + ".goalposition.x"),config.getDouble(circuitname + ".goalposition.y"),config.getDouble(circuitname + ".goalposition.z"),(float)config.getDouble(circuitname + ".goalposition.yaw"),(float)config.getDouble(circuitname + ".goalposition.pitch"));
	}

	public static String getRanking(Player p, String circuitname){
		if(!getCircuitSet().contains(circuitname))return null;
		try{
			//記憶されている周回数
			ArrayList<String> numberoflaps = new ArrayList<String>();
			for(String lap : config.getConfigurationSection(circuitname + ".laptime").getKeys(false)){
				numberoflaps.add(lap);
			}

			if(numberoflaps.isEmpty()){
				return null;
			}

			String ranking = "";

			//各週回数のデータ取得
			for(String lap : numberoflaps){
				HashMap<UUID, Double> count = new HashMap<UUID, Double>();
				for(String path : config.getConfigurationSection(circuitname + ".laptime." + lap).getKeys(false)){
					count.put(UUID.fromString(path), config.getDouble(circuitname + ".laptime." + lap + "." + path));
				}

				if(count.size() < 1)continue;

				//データを並び替える
				List<Map.Entry<UUID, Double>> entry = new ArrayList<Map.Entry<UUID, Double>>(count.entrySet());
				Collections.sort(entry, new Comparator<Map.Entry<UUID, Double>>() {
					@Override
					public int compare(Entry<UUID, Double> entry1, Entry<UUID, Double> entry2) {
						return ((Double) entry2.getValue()).compareTo((Double) entry1.getValue());
					}
				});

				ranking += "#DarkAqua====== " + "#Aqua" + circuitname.toUpperCase() + "#DarkAqua Running Race Ranking" + " - #Aqua" + lap + " #DarkAquaLaps" + " #DarkAqua======\n";
				int ownrank = 0;
				double ownsec = 0;
				for(int rank = 1; rank <= entry.size(); rank++){
					if(rank <= 10){
						entry.get(entry.size()-rank);
						ranking += "   #Yellow" + rank + ". #White" + Bukkit.getOfflinePlayer(entry.get(entry.size()-rank).getKey()).getName() + " : " + "#Yellow" + entry.get(entry.size()-rank).getValue() + " sec\n";
					}
					if(p.getUniqueId().toString().equalsIgnoreCase(entry.get(entry.size()-rank).getKey().toString())){
						ownrank = rank;
						ownsec = entry.get(entry.size()-rank).getValue();
					}
				}

				if(ownrank != 0 && ownsec != 0)
					ranking += "#White" + p.getName() + "#Greenさんの順位は#Yellow" + ownrank + "位 : " + ownsec + " sec" + "#Greenです\n";
				else
					ranking += "#White" + p.getName() + "#Greenさんのデータは存在しません";
			}

			return ranking;
		}catch(NullPointerException ex){
		}
		return null;
	}

	public static String getKartRanking(Player p, String circuitname){
		if(!getCircuitSet().contains(circuitname))return null;
		try{
			//記憶されている周回数
			ArrayList<String> numberoflaps = new ArrayList<String>();
			for(String lap : config.getConfigurationSection(circuitname + ".kartlaptime").getKeys(false)){
				numberoflaps.add(lap);
			}

			if(numberoflaps.isEmpty()){
				return null;
			}

			String ranking = "";

			//各週回数のデータ取得
			for(String lap : numberoflaps){
				HashMap<UUID, Double> count = new HashMap<UUID, Double>();
				for(String path : config.getConfigurationSection(circuitname + ".kartlaptime." + lap).getKeys(false)){
					count.put(UUID.fromString(path), config.getDouble(circuitname + ".kartlaptime." + lap + "." + path));
				}

				if(count.size() < 1)continue;

				//データを並び替える
				List<Map.Entry<UUID, Double>> entry = new ArrayList<Map.Entry<UUID, Double>>(count.entrySet());
				Collections.sort(entry, new Comparator<Map.Entry<UUID, Double>>() {
					@Override
					public int compare(Entry<UUID, Double> entry1, Entry<UUID, Double> entry2) {
						return ((Double) entry2.getValue()).compareTo((Double) entry1.getValue());
					}
				});

				ranking += "#DarkAqua====== " + "#Aqua" + circuitname.toUpperCase() + "#DarkAqua Kart Race Ranking" + " - #Aqua" + lap + " #DarkAquaLaps" + " #DarkAqua======\n";
				int ownrank = 0;
				double ownsec = 0;
				for(int rank = 1; rank <= entry.size(); rank++){
					if(rank <= 10){
						entry.get(entry.size()-rank);
						ranking += "   #Yellow" + rank + ". #White" + Bukkit.getOfflinePlayer(entry.get(entry.size()-rank).getKey()).getName() + " : " + "#Yellow" + entry.get(entry.size()-rank).getValue() + " sec\n";
					}
					if(p.getUniqueId().toString().equalsIgnoreCase(entry.get(entry.size()-rank).getKey().toString())){
						ownrank = rank;
						ownsec = entry.get(entry.size()-rank).getValue();
					}
				}
				if(ownrank != 0 && ownsec != 0)
					ranking += "#White" + p.getName() + "#Greenさんの順位は#Yellow" + ownrank + "位 : " + ownsec + " sec" + "#Greenです\n";
				else
					ranking += "#White" + p.getName() + "#Greenさんのデータは存在しません";
			}

			return ranking;
		}catch(NullPointerException ex){
		}
		return null;
	}

	public static Set<String> getCircuitSet(){
		return config.getKeys(false);
	}

	public static String getCircuitList(){
		String names = null;
		for(String circuitname : getCircuitSet()){
			if(names == null)
				names = "#White" + circuitname + "#Green / ";
			else
				names += "#White" + circuitname + "#Green / ";
		}
		return names;
	}

	//〓〓	ファイル生成		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
	public static void CreateConfig() {
		if(!(configFile.exists())){
			pl.saveResource(filename, true);
			configFile = new File(datafolder, filename);
			config = YamlConfiguration.loadConfiguration(configFile);
			Util.sendMessage(null, filename + ".ymlを生成しました");
		}
	}

	//〓〓	ファイル取得		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
	public static File getConfigFile(){
		return configFile;
	}

	//〓〓	コンフィグ取得		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
	public static FileConfiguration getConfig(){
		return config;
	}

	//〓〓	ファイル保存		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
	public static void saveConfigFile(){
		saveFile(configFile, config);
	}

	//〓〓	ファイル保存実行		〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
	public static void saveFile(File file, FileConfiguration config){
		try{
			config.save(file);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}
