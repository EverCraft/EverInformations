package fr.evercraft.everinformations.healthmob.config;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.entity.EntityTypes;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.java.UtilsList;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;

public class ConfigHealthMob extends EConfig {
	public ConfigHealthMob(final EverInformations plugin) {
		super(plugin, "health_mob");
	}
	
	@Override
	protected void loadDefault() {
		addDefault("enable", true);
		addDefault("stay", 30, "Seconds");
		
		addDefault("disable-worlds", Arrays.asList(
				"world1", 
				"world2"));
		addDefault("disable-entities", Arrays.asList(
				EntityTypes.ENDER_DRAGON.getName().toUpperCase(), 
				EntityTypes.WITHER.getName().toUpperCase()));
		
		if(this.get("message").isVirtual()) {
			addDefault("messages", Arrays.asList(
				"&c▌                   ",
				"&c▌                   ",
				"&c█                  ",
				"&c█▌                 ",
				"&c██                ",
				"&e██▌               ",
				"&e███              ",
				"&e███▌             ",
				"&e████            ",
				"&e████▌           ",
				"&e█████          ",
				"&a█████▌         ",
				"&a██████        ",
				"&a██████▌       ",
				"&a███████      ",
				"&a███████▌     ",
				"&a████████    ",
				"&a████████▌   ",
				"&a█████████  ",
				"&a█████████▌ ",
				"&a██████████"));
		}
	}
	
	/*
	 * Accesseurs
	 */
	
	public boolean isEnable() {
		return this.get("enable").getBoolean(false);
	}
	
	/**
	 * Temps avant de réinitialisé le nom original
	 * @return En Seconde
	 */
	public int getStay() {
		return this.get("stay").getInt(30);
	}

	public List<String> getMessages() {
		List<String> messages = new ArrayList<String>();
		
		// Message unique
		if(this.get("messages").isVirtual()) {
			String message = this.plugin.getChat().replace(this.get("message").getString(""));
			
			if(!message.isEmpty()) {
				messages.add(message);
			}
		// Liste de messages
		} else {
			for(ConfigurationNode config : this.get("messages").getChildrenList()) {
				String message = this.plugin.getChat().replace(config.getString(""));
				
				if(!message.isEmpty()) {
					messages.add(message);
				}
			}
		}
		return messages;
	}
	
	public List<String> getDisableWorlds() {
		return this.getListString("disable-worlds");
	}

	public List<String> getDisableEntities() {
		return UtilsList.toUpperCase(this.getListString("disable-entities"));
	}
	
}
