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
package fr.evercraft.everinformations.automessage.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.ActionBarMessage;

public class ConfigActionBar extends EConfig implements IConfig<ActionBarMessage>{

	public ConfigActionBar(final EverInformations plugin) {
		super(plugin, "automessage/automessage_actionbar");
	}
	
	@Override
	protected void loadDefault() {
		addDefault("enable", true);
		addDefault("interval", 300, "Seconds");
		addDefault("stay", 20, "Seconds");
		addDefault("messages", Arrays.asList("&1Message 1 ......", "&bMessage 2 ......", "&cMessage 3 ......", "&aMessage 4 ......"));
	}
	
	/*
	 * Accesseurs
	 */
	
	public boolean isEnable() {
		return this.get("enable").getBoolean(false);
	}
	
	public List<ActionBarMessage> getMessages() {
		List<ActionBarMessage> messages = new ArrayList<ActionBarMessage>();
		
		// Default
		double stay_default = this.get("stay").getDouble(300);
		double interval_default = this.get("interval").getDouble(20);

		for (ConfigurationNode config : this.get("messages").getChildrenList()) {
			// Message uniquement
			if (config.getValue() instanceof String) {
				String message = this.plugin.getChat().replace(config.getString(""));
				
				if (!message.isEmpty()) {
					messages.add(new ActionBarMessage(stay_default, interval_default, message));
				}
			// Message avec config
			} else {
				double stay = config.getNode("stay").getDouble(stay_default);
				double interval = config.getNode("next").getDouble(config.getNode("interval").getDouble(interval_default));
				String message = this.plugin.getChat().replace(config.getNode("message").getString(""));
				
				if (!message.isEmpty()) {
					messages.add(new ActionBarMessage(stay, interval, message));
				}
			}
		}
		return messages;
	}
}
