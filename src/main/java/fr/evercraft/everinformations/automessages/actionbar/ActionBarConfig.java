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
package fr.evercraft.everinformations.automessages.actionbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;

import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;

public class ActionBarConfig extends EConfig {

	public ActionBarConfig(final EverInformations plugin) {
		super(plugin, "automessages_actionbar");
	}
	
	public void reload() {
		super.reload();
	}
	
	@Override
	public void loadDefault() {
		addDefault("enable", true);
		addDefault("interval", 300000, "Millisecondes");
		addDefault("stay", 20000, "Millisecondes");
		addDefault("messages", Arrays.asList("&1[ARROW] Message 1 ......", "&bMessage 2 ......", "&cMessage 3 ......", "&aMessage 4 ......"));
	}
	
	public boolean isEnable() {
		return this.get("enable").getBoolean(false);
	}
	
	public long getInterval() {
		return this.get("interval").getLong(300000);
	}
	
	public long getStay() {
		return this.get("stay").getInt(20000);
	}
	
	public List<ActionBarMessage> getMessages() {
		List<ActionBarMessage> messages = new ArrayList<ActionBarMessage>();
		for(ConfigurationNode config : this.get("messages").getChildrenList()) {
			if(config.getValue() instanceof String) {
				messages.add(new ActionBarMessage(this.getStay(), this.getInterval(), config.getString("")));
			} else {
				long stay = config.getNode("stay").getLong(this.getStay());
				long interval = config.getNode("next").getLong(this.getInterval());
				String message = this.plugin.getChat().replace(config.getNode("message").getString(""));
				messages.add(new ActionBarMessage(stay, interval, message));
			}
		}
		return messages;
	}
}
