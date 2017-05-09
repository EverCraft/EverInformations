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
package fr.evercraft.everinformations.newbie.config;

import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.message.replace.EReplacesPlayer;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.TitleMessage;
import fr.evercraft.everinformations.newbie.Newbie;

public class ConfigTitle extends EConfig<EverInformations> implements IConfig<TitleMessage> {

	public ConfigTitle(final EverInformations plugin) {
		super(plugin, "newbie/newbie_title");
	}
	
	@Override
	protected void loadDefault() {
		addDefault(Newbie.PLAYER + ".enable", true);
		addDefault(Newbie.PLAYER + ".interval", 0, "Seconds");
		addDefault(Newbie.PLAYER + ".stay", 10, "Seconds");
		addDefault(Newbie.PLAYER + ".fadeIn", 1, "Seconds");
		addDefault(Newbie.PLAYER + ".fadeOut", 1, "Seconds");
		if (this.get(Newbie.PLAYER + ".messages").isVirtual()) {
			addDefault(Newbie.PLAYER + ".title", "&4Welcome");
			addDefault(Newbie.PLAYER + ".subTitle", "&4Welcome &a" + EReplacesPlayer.DISPLAYNAME.getName() + " &4to the server!");
		}
		
		addDefault(Newbie.OTHERS + ".enable", true);
		addDefault(Newbie.OTHERS + ".interval", 0, "Seconds");
		addDefault(Newbie.OTHERS + ".stay", 10, "Seconds");
		addDefault(Newbie.OTHERS + ".fadeIn", 1, "Seconds");
		addDefault(Newbie.OTHERS + ".fadeOut", 1, "Seconds");
		
		if (this.get(Newbie.OTHERS + ".messages").isVirtual()) {
			addDefault(Newbie.OTHERS + ".title", "");
			addDefault(Newbie.OTHERS + ".subTitle", "&4" + EReplacesPlayer.DISPLAYNAME.getName() + " &4is a new player.");
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
		
		double stay_player = this.get(prefix + ".stay").getDouble(stay_default);
		double interval_player = this.get(prefix + ".inverval").getDouble(interval_default);
		double fadeIn_player = this.get(prefix + ".fadeIn").getDouble(fadeIn_default);
		double fadeOut_player = this.get(prefix + ".fadeOut").getDouble(fadeOut_default);
		
		// Message unique
		if (this.get(prefix + ".messages").isVirtual()) {
			String title = this.plugin.getChat().replace(this.get(prefix + ".title").getString(""));
			String subTitle = this.plugin.getChat().replace(this.get(prefix + ".subTitle").getString(""));
			
			if (!title.isEmpty() || !subTitle.isEmpty()) {
				messages.add(new TitleMessage(stay_player, interval_player, fadeIn_player, fadeOut_player, title, subTitle));
			}
		// Liste de messages
		} else {
			for (ConfigurationNode config : this.get(prefix + ".messages").getChildrenList()) {
				double stay = config.getNode("stay").getDouble(stay_player);
				double interval = config.getNode("next").getDouble(config.getNode("inverval").getDouble(interval_player));
				double fadeIn = config.getNode("fadeIn").getDouble(fadeIn_player);
				double fadeOut = config.getNode("fadeOut").getDouble(fadeOut_player);
				
				String title = this.plugin.getChat().replace(config.getNode("title").getString(""));
				String subTitle = this.plugin.getChat().replace(config.getNode("subTitle").getString(""));
				
				if (!title.isEmpty() || !subTitle.isEmpty()) {
					messages.add(new TitleMessage(stay, interval, fadeIn, fadeOut, title, subTitle));
				}
			}
		}
		return messages;
	}
}
