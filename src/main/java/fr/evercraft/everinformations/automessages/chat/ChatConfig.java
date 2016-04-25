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
package fr.evercraft.everinformations.automessages.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.ChatMessage;

public class ChatConfig extends EConfig {

	public ChatConfig(final EverInformations plugin) {
		super(plugin, "automessages_chat");
	}
	
	public void reload() {
		super.reload();
	}
	
	@Override
	public void loadDefault() {
		addDefault("enable", true);
		addDefault("interval", 300);
		addDefault("prefix", "&f[&4Ever&6&lNews&f] ");
		addDefault("messages", Arrays.asList("&1[ARROW] Message 1 ......", "&bMessage 2 ......", "&cMessage 3 ......", "&aMessage 4 ......"));
		addDefault("test", TextSerializers.JSON.serialize(Text.builder("Salut tout le monde").onHover(TextActions.showText(Text.of("Clique ici"))).build()));
	}
	
	public boolean isEnable() {
		return this.get("enable").getBoolean(false);
	}
	
	public int getInterval() {
		return this.get("interval").getInt(300);
	}
	
	public String getPrefix() {
		return this.plugin.getChat().replace(this.get("prefix").getString("&f[&4Ever&6&lNews&f] "));
	}
	
	public List<ChatMessage> getMessages() {
		List<ChatMessage> messages = new ArrayList<ChatMessage>();
		for(ConfigurationNode config : this.get("messages").getChildrenList()) {
			if(config.getValue() instanceof String) {
				String prefix_message = this.plugin.getChat().replace(this.getPrefix());
				String message = this.plugin.getChat().replace(config.getString(""));
				
				messages.add(new ChatMessage(this.getInterval(), TextSerializers.FORMATTING_CODE, prefix_message, message));
			} else {
				long interval = config.getNode("next").getLong(this.getInterval());
				String prefix = this.plugin.getChat().replace(config.getNode("prefix").getString(this.getPrefix()));
				String message = this.plugin.getChat().replace(config.getNode("message").getString(""));
				
				String type_string = config.getNode("type").getString("");
				TextSerializer type = TextSerializers.FORMATTING_CODE;
				if(type_string.equalsIgnoreCase("JSON")) {
					type = TextSerializers.JSON;
				} else if(type_string.equalsIgnoreCase("TEXT_XML")) {
					type = TextSerializers.TEXT_XML;
				}
				messages.add(new ChatMessage(interval, type, prefix, message));
			}
		}
		return messages;
	}
}
