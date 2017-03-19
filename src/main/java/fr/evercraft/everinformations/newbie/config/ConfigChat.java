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

import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import fr.evercraft.everapi.message.replace.EReplacesPlayer;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.ChatMessage;
import fr.evercraft.everinformations.newbie.Newbie;

public class ConfigChat extends EConfig<EverInformations> implements IConfig<ChatMessage> {
	public ConfigChat(final EverInformations plugin) {
		super(plugin, "newbie/newbie_chat");
	}
	
	@Override
	protected void loadDefault() {
		addDefault(Newbie.PLAYER + ".enable", true);
		addDefault(Newbie.PLAYER + ".prefix", "");
		if (this.get(Newbie.PLAYER + ".messages").isVirtual()) {
			addDefault(Newbie.PLAYER + ".message", "&4Welcome &a" + EReplacesPlayer.DISPLAYNAME.getName() + " &4to the server!");
		}
		
		addDefault(Newbie.OTHERS + ".enable", true);
		addDefault(Newbie.OTHERS + ".prefix", "");
		if (this.get(Newbie.OTHERS + ".messages").isVirtual()) {
			addDefault(Newbie.OTHERS + ".message", "&a" + EReplacesPlayer.DISPLAYNAME.getName() + " &4is a new player.");
		}
	}
	
	/*
	 * Accesseurs
	 */
	
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

	private List<ChatMessage> getMessages(String prefix) {
		List<ChatMessage> messages = new ArrayList<ChatMessage>();
		
		double interval_default = this.get("inverval").getDouble(2);
		String prefix_default = this.get("prefix").getString("");
		
		double interval_player = this.get(prefix + ".inverval").getDouble(interval_default);
		String prefix_player = this.get(prefix + ".prefix").getString(prefix_default);
		
		// Message unique
		if (this.get(prefix + ".messages").isVirtual()) {
			String message = this.plugin.getChat().replace(this.get(prefix + ".message").getString(""));
			if (!message.isEmpty()) {
				messages.add(new ChatMessage(interval_player, TextSerializers.FORMATTING_CODE, prefix_player, message));
			}
		// Liste de messages
		} else {
			for (ConfigurationNode config : this.get(prefix + ".messages").getChildrenList()) {
				// Message uniquement
				if (config.getValue() instanceof String) {
					String prefix_message = this.plugin.getChat().replace(prefix_player);
					String message = this.plugin.getChat().replace(config.getString(""));
					
					if (!message.isEmpty()) {
						messages.add(new ChatMessage(interval_player, TextSerializers.FORMATTING_CODE, prefix_message, message));
					}
				// Message avec config
				} else {
					double interval = config.getNode("next").getDouble(config.getNode("inverval").getDouble(interval_player));
					String prefix_message = this.plugin.getChat().replace(config.getNode("prefix").getString(prefix_player));
					String message = this.plugin.getChat().replace(config.getNode("message").getString(""));
					
					String type_string = config.getNode("format").getString("");
					TextSerializer type = TextSerializers.FORMATTING_CODE;
					if (type_string.equalsIgnoreCase("JSON")) {
						type = TextSerializers.JSON;
					}
					
					if (!message.isEmpty()) {
						messages.add(new ChatMessage(interval, type, prefix_message, message));
					}
				}
			}
		}
		return messages;
	}
}
