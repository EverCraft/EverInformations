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
package fr.evercraft.everinformations.newbie.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.TitleMessage;
import fr.evercraft.everinformations.newbie.Newbie;

public class ConfigTitle extends EConfig implements IConfig<TitleMessage> {

	public ConfigTitle(final EverInformations plugin) {
		super(plugin, "newbie/newbie_title");
	}
	
	@Override
	protected void loadDefault() {
		addDefault(Newbie.PLAYER + ".enable", true);
		addDefault(Newbie.PLAYER + "interval", 0, "Seconds");
		addDefault(Newbie.PLAYER + "stay", 10, "Seconds");
		addDefault(Newbie.PLAYER + "fadeIn", 1, "Seconds");
		addDefault(Newbie.PLAYER + "fadeOut", 1, "Seconds");
		
		if(this.get(Newbie.PLAYER + ".messages").isVirtual()) {
			List<HashMap<String, String>> messages = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("title", "&4Welcome");
			message.put("subTitle", "&4Welcome &a<DISPLAYNAME_FORMAT> &4to the server!");
			messages.add(message);
			
			this.get(Newbie.PLAYER + ".messages").setValue(messages);
		}
		
		addDefault(Newbie.OTHERS + ".enable", true);
		addDefault(Newbie.OTHERS + ".interval", 0, "Seconds");
		addDefault(Newbie.OTHERS + ".stay", 10, "Seconds");
		addDefault(Newbie.OTHERS + ".fadeIn", 1, "Seconds");
		addDefault(Newbie.OTHERS + ".fadeOut", 1, "Seconds");
		
		if(this.get(Newbie.OTHERS + ".messages").isVirtual()) {
			List<HashMap<String, String>> messages = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("title", "&4Title");
			message.put("subTitle", "&a<DISPLAYNAME_FORMAT> &4is a new player.");
			messages.add(message);
			
			this.get(Newbie.OTHERS + ".messages").setValue(messages);
		}
	}
	
	/*
	 * Accesseurs
	 */
	
	public boolean isPlayerEnable() {
		return this.isEnable(Newbie.PLAYER);
	}
	
	public List<TitleMessage> getPlayerMessages() {
		return this.getMessages(Newbie.PLAYER);
	}
	
	public boolean isOthersEnable() {
		return this.isEnable(Newbie.OTHERS);
	}
	
	public List<TitleMessage> getOthersMessages() {
		return this.getMessages(Newbie.OTHERS);
	}
	
	/*
	 * Fonctions
	 */
	
	private boolean isEnable(String prefix) {
		return this.get(prefix + ".enable").getBoolean(false);
	}
	
	private List<TitleMessage> getMessages(String prefix) {
		List<TitleMessage> messages = new ArrayList<TitleMessage>();
		
		double stay_default = this.get(prefix + ".stay").getDouble(10);
		double interval_default = this.get(prefix + ".inverval").getDouble(0);
		double fadeIn_default = this.get(prefix + ".fadeIn").getDouble(1);
		double fadeOut_default = this.get(prefix + ".fadeOut").getDouble(1);
		
		for(ConfigurationNode config : this.get(prefix + ".messages").getChildrenList()) {
			double stay = config.getNode("stay").getDouble(stay_default);
			double interval = config.getNode("next").getDouble(interval_default);
			double fadeIn = config.getNode("fadeIn").getDouble(fadeIn_default);
			double fadeOut = config.getNode("fadeOut").getDouble(fadeOut_default);
			
			String title = this.plugin.getChat().replace(config.getNode("title").getString(""));
			String subTitle = this.plugin.getChat().replace(config.getNode("subTitle").getString(""));
			
			messages.add(new TitleMessage(stay, interval, fadeIn, fadeOut, title, subTitle));
		}
		
		return messages;
	}
}
