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
package fr.evercraft.everinformations.newbie.actionbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.ActionBarMessage;
import fr.evercraft.everinformations.newbie.Newbie;

public class ActionBarConfig extends EConfig {

	public ActionBarConfig(final EverInformations plugin) {
		super(plugin, "newbie_actionbar");
	}
	
	public void reload() {
		super.reload();
	}
	
	@Override
	protected void loadDefault() {		
		addDefault(Newbie.PLAYER + ".enable", true);
		addDefault(Newbie.PLAYER + ".interval", 0, "Millisecondes");
		addDefault(Newbie.PLAYER + ".stay", 200, "Millisecondes");
		addDefault(Newbie.PLAYER + ".messages", Arrays.asList("&aWelcome &a<DISPLAYNAME_FORMAT> &4to the server!", "&4Welcome &a<DISPLAYNAME> &4to the server!"));
		
		addDefault(Newbie.OTHERS + ".enable", true);
		addDefault(Newbie.OTHERS + ".interval", 0, "Millisecondes");
		addDefault(Newbie.OTHERS + ".stay", 200, "Millisecondes");
		addDefault(Newbie.OTHERS + ".messages", Arrays.asList("&a<DISPLAYNAME_FORMAT> &4is a new player."));
	}
	
	public boolean isPlayerEnable() {
		return this.isEnable(Newbie.PLAYER);
	}
	
	public List<ActionBarMessage> getPlayerMessages() {
		return this.getMessages(Newbie.PLAYER);
	}
	
	public boolean isOthersEnable() {
		return this.isEnable(Newbie.OTHERS);
	}
	
	public List<ActionBarMessage> getOthersMessages() {
		return this.getMessages(Newbie.OTHERS);
	}
	
	/*
	 * Fonctions
	 */
	
	private boolean isEnable(String prefix) {
		return this.get(prefix + ".enable").getBoolean(false);
	}
	
	private int getInterval(String prefix) {
		return this.get(prefix + ".interval").getInt(200);
	}
	
	private int getStay(String prefix) {
		return this.get(prefix + ".stay").getInt(200);
	}
	
	public List<ActionBarMessage> getMessages(String prefix) {
		List<ActionBarMessage> messages = new ArrayList<ActionBarMessage>();
		for(ConfigurationNode config : this.get(prefix + ".messages").getChildrenList()) {
			if(config.getValue() instanceof String) {
				messages.add(new ActionBarMessage(this.getStay(prefix), this.getInterval(prefix), this.plugin.getChat().replace(config.getString(""))));
			} else {
				long stay = config.getNode("stay").getLong(this.getStay(prefix));
				long interval = config.getNode("next").getLong(this.getInterval(prefix));
				String message = this.plugin.getChat().replace(config.getNode("message").getString(""));
				messages.add(new ActionBarMessage(stay, interval, message));
			}
		}
		return messages;
	}
}
