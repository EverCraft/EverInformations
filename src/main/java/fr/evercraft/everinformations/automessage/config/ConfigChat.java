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

import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.ChatMessage;

public class ConfigChat extends EConfig<EverInformations> implements IConfig<ChatMessage> {

	public ConfigChat(final EverInformations plugin) {
		super(plugin, "automessage/automessage_chat");
	}
	
	@Override
	protected void loadDefault() {
		addDefault("enable", true);
		addDefault("format", 300, "Seconds");
		addDefault("prefix", "&f[&4Ever&6&lNews&f] ");
		addDefault("messages", Arrays.asList("&1Message 1 ......", "&bMessage 2 ......", "&cMessage 3 ......", "&aMessage 4 ......"));
	}

	/*
	 * Accesseurs
	 */
	
	public boolean isEnable() {
		return this.get("enable").getBoolean(false);
	}
	
	public List<ChatMessage> getMessages() {
		List<ChatMessage> messages = new ArrayList<ChatMessage>();
		
		// Default
		double interval_default = this.get("interval").getDouble(300);
		String prefix_default = this.plugin.getChat().replace(this.get("prefix").getString("&f[&4Ever&6&lNews&f] "));
		
		for (ConfigurationNode config : this.get("messages").getChildrenList()) {
			// Message uniquement
			if (config.getValue() instanceof String) {
				String prefix_message = this.plugin.getChat().replace(prefix_default);
				String message = this.plugin.getChat().replace(config.getString(""));
				
				if (!message.isEmpty()) {
					messages.add(new ChatMessage(interval_default, TextSerializers.FORMATTING_CODE, prefix_message, message));
				}
			// Message avec config
			} else {
				double interval = config.getNode("next").getDouble(config.getNode("interval").getDouble(interval_default));
				String prefix = this.plugin.getChat().replace(config.getNode("prefix").getString(prefix_default));
				String message = this.plugin.getChat().replace(config.getNode("message").getString(""));
				
				String format_string = config.getNode("format").getString("");
				TextSerializer format = TextSerializers.FORMATTING_CODE;
				if (format_string.equalsIgnoreCase("JSON")) {
					format = TextSerializers.JSON;
				} else if (format_string.equalsIgnoreCase("TEXT_XML")) {
					format = TextSerializers.TEXT_XML;
				}
				
				if (!message.isEmpty()) {
					messages.add(new ChatMessage(interval, format, prefix, message));
				}
			}
		}
		return messages;
	}
}
