package main.java.com.github.erozabesu.YPLKart.Enum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import main.java.com.github.erozabesu.YPLKart.Data.Settings;
import main.java.com.github.erozabesu.YPLKart.Utils.Util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum EnumKarts{
	Kart1(Settings.KartName1, Material.HUGE_MUSHROOM_2, (byte)0, Settings.KartSetting1, Material.BOWL, (byte)0),
	Kart2(Settings.KartName2, Material.HUGE_MUSHROOM_2, (byte)1, Settings.KartSetting2, Material.STRING, (byte)0),
	Kart3(Settings.KartName3, Material.HUGE_MUSHROOM_2, (byte)2, Settings.KartSetting3, Material.FEATHER, (byte)0),
	Kart4(Settings.KartName4, Material.HUGE_MUSHROOM_2, (byte)3, Settings.KartSetting4, Material.SULPHUR, (byte)0),
	Kart5(Settings.KartName5, Material.HUGE_MUSHROOM_2, (byte)4, Settings.KartSetting5, Material.SEEDS, (byte)0),
	Kart6(Settings.KartName6, Material.HUGE_MUSHROOM_2, (byte)5, Settings.KartSetting6, Material.WHEAT, (byte)0),
	Kart7(Settings.KartName7, Material.HUGE_MUSHROOM_2, (byte)6, Settings.KartSetting7, Material.FLINT, (byte)0),
	Kart8(Settings.KartName8, Material.HUGE_MUSHROOM_2, (byte)7, Settings.KartSetting8, Material.LEATHER, (byte)0);

	private double[] setting;
	private String name;
	private Material displayblock;
	private byte displaydata;
	private Material itemmaterial;
	private byte itemdata;

	private EnumKarts(String name, Material displayblock, byte displaydata, double[] classsetting, Material itemmaterial, byte itemdata){
		this.setting = classsetting;
		this.name = name;
		this.displayblock = displayblock;
		this.displaydata = displaydata;
		this.itemmaterial = itemmaterial;
		this.itemdata = itemdata;
	}

	public static EnumKarts getKartfromString(String value){
		if(value == null)return null;
		for(EnumKarts kart : EnumKarts.values()){
			if(kart.getName().equalsIgnoreCase(ChatColor.stripColor(value)))
				return kart;
		}
		return null;
	}

	public static EnumKarts getKartfromEntity(Entity entity){
		if(entity == null)return null;
		if(entity.getCustomName() == null)return null;

		for(EnumKarts kart : EnumKarts.values()){
			if(kart.getName().equalsIgnoreCase(ChatColor.stripColor(entity.getCustomName())))
				return kart;
		}
		return null;
	}

	public static String getKartList(){
		String kartlist = null;
		for(EnumKarts kart : EnumKarts.values()){
			if(kartlist == null)
				kartlist = kart.getName();
			else
				kartlist += ", " + kart.getName();
		}
		return kartlist;
	}

	public static List<String> getKartArrayList(){
		List<String> kartlist = new ArrayList<String>();
		for(EnumKarts kart : EnumKarts.values()){
			kartlist.add(kart.getName());
		}
		return kartlist;
	}

	public static EnumKarts getRandomKart(){
		int ram = EnumKarts.values().length;
		ram = new Random().nextInt(ram);

		int count = 0;
		for(EnumKarts kart : EnumKarts.values()){
			if(count == ram)
				return kart;
			count++;
		}
		return null;
	}

	public String getParameter(){
		String text = "";
		text += "#DarkAqua " + "#Green重量 : #Yellow"							 + getWeight() + "\n";
		text += "#DarkAqua " + "#Green最高速度 : #Yellow"						 + getMaxSpeed() + "\n";
		text += "#DarkAqua " + "#Green加速 : #Yellow"							 + getAcceleration() + "\n";
		text += "#DarkAqua " + "#Greenカーブ性能 : #Yellow"					 + getDefaultCorneringPower() + "\n";
		text += "#DarkAqua " + "#Greenドリフト時のカーブ性能 : #Yellow"		 + getDriftCorneringPower() + "\n";
		text += "#DarkAqua " + "#Greenドリフト時の減速率 : #Yellow"				 + getDriftSpeedDecrease() + "\n";
		text += "#DarkAqua " + "#Greenダート走行時の減速率 : #Yellow"			 + getSpeedOnDirt() + "\n";
		text += "#DarkAqua " + "#Green乗り越えられるブロックの高さ : #Yellow"	 + getClimbableHeight() + "\n";
		return text;
	}

	public ItemStack getMenuItem(){
		ItemStack item = new ItemStack(getItemMaterial(), 1, (short)0, getItemData());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getName());
		meta.setLore(Util.replaceLine(Util.replaceChatColor(getParameter())));

		item.setItemMeta(meta);

		return item;
	}

	public Material getDisplayBlock(){
		return this.displayblock;
	}

	public byte getDisplayData(){
		return this.displaydata;
	}

	public String getName(){
		return ChatColor.stripColor(this.name);
	}

	public Material getItemMaterial(){
		return this.itemmaterial;
	}

	public byte getItemData(){
		return this.itemdata;
	}

	public void reload(String name, double[] classsetting){
		this.name = name;
		this.setting = classsetting;
	}

	public double getWeight(){
		return setting[0];
	}

	public int getMaxSpeed(){
		return (int) setting[1];
	}

	public double getAcceleration(){
		return setting[2];
	}

	public double getSpeedOnDirt(){
		return setting[3];
	}

	public float getClimbableHeight(){
		return (float) setting[4];
	}

	public float getDefaultCorneringPower(){
		return (float) setting[5];
	}

	public float getDriftCorneringPower(){
		return (float) setting[6];
	}

	public double getDriftSpeedDecrease(){
		return setting[7];
	}
}
