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
package fr.evercraft.everinformations.automessages.title;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;

public class TitleConfig extends EConfig {

	public TitleConfig(final EverInformations plugin) {
		super(plugin, "automessages_title");
	}
	
	public void reload() {
		super.reload();
	}
	
	@Override
	public void loadDefault() {
		addDefault("enable", true);
		addDefault("interval", 200, "Tick");
		addDefault("stay", 200, "Tick");
		addDefault("fadeIn", 1, "Tick");
		addDefault("fadeOut", 1, "Tick");
		
		if(this.get("messages").isVirtual()) {
			List<HashMap<String, String>> messages = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("title", "&cTitle 1");
			message.put("subTitle", "&cSubTitle 1");
			messages.add(message);
			
			message.put("title", "&aTitle 1");
			message.put("subTitle", "&aSubTitle 1");
			messages.add(message);
			
			message.put("title", "&1Title 1");
			message.put("subTitle", "&1SubTitle 1");
			messages.add(message);
			
			message.put("title", "&2Title 1");
			message.put("subTitle", "&2SubTitle 1");
			messages.add(message);
			
			this.get("messages").setValue(messages);
		}
	}
	
	public boolean isEnable() {
		return this.get("enable").getBoolean(false);
	}
	
	public int getInterval() {
		return this.get("interval").getInt(200);
	}
	
	public int getStay() {
		return this.get("stay").getInt(200);
	}
	
	public int getFadeIn() {
		return this.get("fadeIn").getInt(1);
	}
	
	public int getFadeOut() {
		return this.get("fadeOut").getInt(1);
	}
	
	public List<TitleMessage> getMessages() {
		List<TitleMessage> messages = new ArrayList<TitleMessage>();
		for(ConfigurationNode config : this.get("messages").getChildrenList()) {
			int stay = config.getNode("stay").getInt(this.getStay());
			int interval = config.getNode("next").getInt(this.getInterval());
			int fadeIn = config.getNode("fadeIn").getInt(this.getFadeIn());
			int fadeOut = config.getNode("fadeOut").getInt(this.getFadeOut());
			String title = this.plugin.getChat().replace(config.getNode("title").getString(""));
			String subTitle = this.plugin.getChat().replace(config.getNode("subTitle").getString(""));
			messages.add(new TitleMessage(stay, interval, fadeIn, fadeOut, title, subTitle));
		}
		return messages;
	}
}
