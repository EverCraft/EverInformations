/**
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
package fr.evercraft.everinformations.automessages.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.TitleMessage;

public class ConfigTitle extends EConfig implements IConfig<TitleMessage> {

	public ConfigTitle(final EverInformations plugin) {
		super(plugin, "automessages_title");
	}
	
	@Override
	protected void loadDefault() {
		addDefault("enable", true);
		addDefault("interval", 360, "Seconds");
		addDefault("stay", 20, "Seconds");
		addDefault("fadeIn", 1, "Seconds");
		addDefault("fadeOut", 1, "Seconds");
		
		if(this.get("messages").isVirtual()) {
			List<HashMap<String, String>> messages = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("title", "&cTitle 1");
			message.put("subTitle", "&cSubTitle 1");
			messages.add(message);
			
			message = new HashMap<String, String>();
			message.put("title", "&aTitle 2");
			message.put("subTitle", "&aSubTitle 2");
			messages.add(message);
			
			message = new HashMap<String, String>();
			message.put("title", "&1Title 3");
			message.put("subTitle", "&1SubTitle 3");
			messages.add(message);
			
			message = new HashMap<String, String>();
			message.put("title", "&2Title 4");
			message.put("subTitle", "&2SubTitle 4");
			messages.add(message);
			
			this.get("messages").setValue(messages);
		}
	}

	/*
	 * Fonctions
	 */
	
	private double getInterval() {
		return this.get("interval").getDouble(200);
	}
	
	private double getStay() {
		return this.get("stay").getDouble(200);
	}
	
	private double getFadeIn() {
		return this.get("fadeIn").getDouble(1);
	}
	
	private double getFadeOut() {
		return this.get("fadeOut").getDouble(1);
	}
	
	/*
	 * Accesseurs
	 */
	
	public boolean isEnable() {
		return this.get("enable").getBoolean(false);
	}
	
	public List<TitleMessage> getMessages() {
		List<TitleMessage> messages = new ArrayList<TitleMessage>();
		for(ConfigurationNode config : this.get("messages").getChildrenList()) {
			double stay = config.getNode("stay").getDouble(this.getStay());
			double interval = config.getNode("next").getDouble(this.getInterval());
			double fadeIn = config.getNode("fadeIn").getDouble(this.getFadeIn());
			double fadeOut = config.getNode("fadeOut").getDouble(this.getFadeOut());
			String title = this.plugin.getChat().replace(config.getNode("title").getString(""));
			String subTitle = this.plugin.getChat().replace(config.getNode("subTitle").getString(""));
			messages.add(new TitleMessage(stay, interval, fadeIn, fadeOut, title, subTitle));
		}
		return messages;
	}
}
