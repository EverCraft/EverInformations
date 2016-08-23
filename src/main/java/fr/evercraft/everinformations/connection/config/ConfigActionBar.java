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
package fr.evercraft.everinformations.connection.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.connection.Connection;
import fr.evercraft.everinformations.connection.Connection.Connections;
import fr.evercraft.everinformations.message.ActionBarMessage;

public class ConfigActionBar extends EConfig implements IConfig<ActionBarMessage> {

	public ConfigActionBar(final EverInformations plugin) {
		super(plugin, "connection/connection_actionbar");
	}
	
	@Override
	protected void loadDefault() {		
		addDefault(Connection.PLAYER + ".enable", true);
		if (this.get(Connection.PLAYER + "." + Connection.DEFAULT + "." + Connections.JOIN.name() + ".messages").isVirtual()) {
			addDefault(Connection.PLAYER + "." + Connection.DEFAULT + "." + Connections.JOIN.name() + ".message", "&7&l[&2+&7&l] <DISPLAYNAME_FORMAT> &7joined the game");
		}
		
		addDefault(Connection.OTHERS + ".enable", true);
		if (this.get(Connection.OTHERS + "." + Connection.DEFAULT + "." + Connections.JOIN.name() + ".messages").isVirtual()) {
			addDefault(Connection.OTHERS + "." + Connection.DEFAULT + "." + Connections.JOIN.name() + ".message", "&7&l[&2+&7&l] <DISPLAYNAME_FORMAT> &7joined the game");
		}
		if (this.get(Connection.OTHERS + "." + Connection.DEFAULT + "." + Connections.QUIT.name() + ".messages").isVirtual()) {
			addDefault(Connection.OTHERS + "." + Connection.DEFAULT + "." + Connections.QUIT.name() + ".message", "&7&l[&4-&7&l] <DISPLAYNAME_FORMAT> &7left the game");
		}
		if (this.get(Connection.OTHERS + "." + Connection.DEFAULT + "." + Connections.KICK.name() + ".messages").isVirtual()) {
			addDefault(Connection.OTHERS + "." + Connection.DEFAULT + "." + Connections.KICK.name() + ".message", "&7&l[&4-&7&l] <DISPLAYNAME_FORMAT> &7has been kicked out of the game for <reason>");
		}
	}
	
	/*
	 * Accesseurs
	 */
	
	public boolean isPlayerEnable() {
		return this.isEnable(Connection.PLAYER);
	}
	
	public Map<String, List<ActionBarMessage>> getPlayerJoinMessages() {
		return this.getMessages(Connection.PLAYER, Connections.JOIN);
	}
	
	public boolean isOthersEnable() {
		return this.isEnable(Connection.OTHERS);
	}
	
	public Map<String, List<ActionBarMessage>> getOthersJoinMessages() {
		return this.getMessages(Connection.OTHERS, Connections.JOIN);
	}
	
	public Map<String, List<ActionBarMessage>> getOthersQuitMessages() {
		return this.getMessages(Connection.OTHERS, Connections.QUIT);
	}
	
	public Map<String, List<ActionBarMessage>> getOthersKickMessages() {
		return this.getMessages(Connection.OTHERS, Connections.KICK);
	}
	
	/*
	 * Fonctions
	 */
	
	private boolean isEnable(String prefix) {
		return this.get(prefix + ".enable").getBoolean(false);
	}
	
	private Map<String, List<ActionBarMessage>> getMessages(String prefix, Connections connections) {
		Map<String, List<ActionBarMessage>> groups = new HashMap<String, List<ActionBarMessage>>();
		
		// Default
		double stay_default = this.get("stay").getDouble(10);
		double interval_default = this.get("interval").getDouble(0);
		
		for (Entry<Object, ? extends CommentedConfigurationNode> group : this.get(prefix).getChildrenMap().entrySet()) {
			if (group.getKey() instanceof String && !((String) group.getKey()).equals("enable")) {
				CommentedConfigurationNode config = group.getValue().getNode(connections.name());
				List<ActionBarMessage> messages = new ArrayList<ActionBarMessage>();
				
				// Default
				double stay_player = config.getNode("stay").getDouble(stay_default);
				double interval_player = config.getNode("interval").getDouble(interval_default);
				
				// Message unique
				if (config.getNode("messages").isVirtual()) {
					String message = this.plugin.getChat().replace(config.getNode("message").getString(""));
					
					if (!message.isEmpty()) {
						messages.add(new ActionBarMessage(stay_player, interval_player, message));
					}
				// Liste de messages
				} else {
					for (ConfigurationNode config_messages : config.getNode("messages").getChildrenList()) {
						// Message uniquement
						if (config_messages.getValue() instanceof String) {
							messages.add(new ActionBarMessage(stay_player, interval_player, this.plugin.getChat().replace(config_messages.getString(""))));
						// Message avec config
						} else {
							double stay = config_messages.getNode("stay").getDouble(stay_player);
							double interval = config_messages.getNode("next").getDouble(config_messages.getNode("interval").getDouble(interval_player));
							String message = this.plugin.getChat().replace(config_messages.getNode("message").getString(""));
							
							if (!message.isEmpty()) {
								messages.add(new ActionBarMessage(stay, interval, message));
							}
						}
					}
				}
				groups.put((String) group.getKey(), messages);
			}
		}
		return groups;
	}
}
