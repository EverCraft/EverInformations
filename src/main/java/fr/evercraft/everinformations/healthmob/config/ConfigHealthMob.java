/*
 * This file is part of EverInformations.
 *
 * EverInformations is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EverInformations is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EverInformations.  If not, see <http://www.gnu.org/licenses/>.
 */
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
		
		if (this.get("message").isVirtual()) {
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
		if (this.get("messages").isVirtual()) {
			String message = this.plugin.getChat().replace(this.get("message").getString(""));
			
			if (!message.isEmpty()) {
				messages.add(message);
			}
		// Liste de messages
		} else {
			for (ConfigurationNode config : this.get("messages").getChildrenList()) {
				String message = this.plugin.getChat().replace(config.getString(""));
				
				if (!message.isEmpty()) {
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
