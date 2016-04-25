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
package fr.evercraft.everinformations.newbie.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;

import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.ChatMessage;
import fr.evercraft.everinformations.newbie.Newbie;

public class ChatConfig extends EConfig {
	public ChatConfig(final EverInformations plugin) {
		super(plugin, "newbie_chat");
	}
	
	public void reload() {
		super.reload();
	}
	
	@Override
	public void loadDefault() {
		addDefault(Newbie.PLAYER + ".enable", true);
		addDefault(Newbie.PLAYER + ".prefix", "");
		addDefault(Newbie.PLAYER + ".interval", 10);
		addDefault(Newbie.PLAYER + ".messages", Arrays.asList("&4Welcome &a<DISPLAYNAME_FORMAT> &4to the server!"));
		
		addDefault(Newbie.OTHERS + ".enable", true);
		addDefault(Newbie.OTHERS + ".prefix", "");
		addDefault(Newbie.OTHERS + ".interval", 10);
		addDefault(Newbie.OTHERS + ".messages", Arrays.asList("&a<DISPLAYNAME_FORMAT> &4is a new player.", "&4Want to welcome &a<DISPLAYNAME_FORMAT>"));
	}
	
	public boolean isPlayerEnable() {
		return this.isEnable(Newbie.PLAYER);
	}

	public List<ChatMessage> getPlayerMessages() {
		return this.getMessages(Newbie.PLAYER);
	}
	
	public boolean isOthersEnable() {
		return this.isEnable(Newbie.OTHERS);
	}
	
	public List<ChatMessage> getOthersMessages() {
		return this.getMessages(Newbie.OTHERS);
	}
	
	/*
	 * Fonctions
	 */
	private boolean isEnable(String prefix) {
		return this.get(prefix + ".enable").getBoolean(false);
	}
	
	private int getInterval(String prefix) {
		return this.get(prefix + ".interval").getInt(300);
	}
	
	private String getPrefix(String prefix) {
		return this.get("prefix").getString("&f[&4Ever&6&lNews&f] ");
	}
	
	private List<ChatMessage> getMessages(String prefix) {
		List<ChatMessage> messages = new ArrayList<ChatMessage>();
		for(ConfigurationNode config : this.get(prefix + ".messages").getChildrenList()) {
			if(config.getValue() instanceof String) {
				String prefix_message = this.plugin.getChat().replace(this.getPrefix(prefix));
				String message = this.plugin.getChat().replace(config.getString(""));
				
				messages.add(new ChatMessage(this.getInterval(prefix), TextSerializers.FORMATTING_CODE, prefix_message, message));
			} else {
				long interval = config.getNode("next").getLong(this.getInterval(prefix));
				String prefix_message = this.plugin.getChat().replace(config.getNode("prefix").getString(this.getPrefix(prefix)));
				String message = this.plugin.getChat().replace(config.getNode("message").getString(""));
				
				String type_string = config.getNode("type").getString("");
				TextSerializer type = TextSerializers.FORMATTING_CODE;
				if(type_string.equalsIgnoreCase("JSON")) {
					type = TextSerializers.JSON;
				} else if(type_string.equalsIgnoreCase("TEXT_XML")) {
					type = TextSerializers.TEXT_XML;
				}
				messages.add(new ChatMessage(interval, type, prefix_message, message));
			}
		}
		return messages;
	}
}
