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
import fr.evercraft.everinformations.message.TitleMessage;

public class ConfigTitle extends EConfig implements IConfig<TitleMessage> {

	public ConfigTitle(final EverInformations plugin) {
		super(plugin, "connection_title");
	}
	
	@Override
	protected void loadDefault() {		
		addDefault(Connection.PLAYER + ".enable", true);
		addDefault(Connection.PLAYER + "." + Connection.DEFAULT + "." + Connections.JOIN.name() + ".subTitle", "&7&l[&2+&7&l] <DISPLAYNAME_FORMAT> &7joined the game");
		
		addDefault(Connection.OTHERS + ".enable", true);
		addDefault(Connection.OTHERS + "." + Connection.DEFAULT + "." + Connections.JOIN.name() + ".subTitle", "&7&l[&2+&7&l] <DISPLAYNAME_FORMAT> &7joined the game");
		addDefault(Connection.OTHERS + "." + Connection.DEFAULT + "." + Connections.QUIT.name() + ".subTitle", "&7&l[&4-&7&l] <DISPLAYNAME_FORMAT> &7left the game");
		addDefault(Connection.OTHERS + "." + Connection.DEFAULT + "." + Connections.KICK.name() + ".subTitle", "&7&l[&4-&7&l] <DISPLAYNAME_FORMAT> &7has been kicked out of the game for <reason>");
	}
	
	/*
	 * Accesseurs
	 */
	
	public boolean isPlayerEnable() {
		return this.isEnable(Connection.PLAYER);
	}
	
	public Map<String, List<TitleMessage>> getPlayerJoinMessages() {
		return this.getMessages(Connection.PLAYER, Connections.JOIN);
	}

	public boolean isOthersEnable() {
		return this.isEnable(Connection.OTHERS);
	}
	
	public Map<String, List<TitleMessage>> getOthersJoinMessages() {
		return this.getMessages(Connection.OTHERS, Connections.JOIN);
	}
	
	public Map<String, List<TitleMessage>> getOthersQuitMessages() {
		return this.getMessages(Connection.OTHERS, Connections.QUIT);
	}
	
	public Map<String, List<TitleMessage>> getOthersKickMessages() {
		return this.getMessages(Connection.OTHERS, Connections.KICK);
	}
	
	/*
	 * Fonctions
	 */
	
	private boolean isEnable(String prefix) {
		return this.get(prefix + ".enable").getBoolean(false);
	}
	
	private Map<String, List<TitleMessage>> getMessages(String prefix, Connections connections) {
		Map<String, List<TitleMessage>> groups = new HashMap<String, List<TitleMessage>>();
		for(Entry<Object, ? extends CommentedConfigurationNode> group : this.get(prefix).getChildrenMap().entrySet()) {
			if(group.getKey() instanceof String && !((String) group.getKey()).equals("enable")) {  
				CommentedConfigurationNode config = group.getValue().getNode(connections.name());
				List<TitleMessage> messages = new ArrayList<TitleMessage>();
				
				double stay_default = config.getNode("stay").getDouble(10);
				double interval_default = config.getNode("inverval").getDouble(0);
				double fadeIn_default = config.getNode("fadeIn").getDouble(1);
				double fadeOut_default = config.getNode("fadeOut").getDouble(1);
				
				if(!config.getNode("title").isVirtual() || !config.getNode("subTitle").isVirtual()) {
					String title = this.plugin.getChat().replace(config.getNode("title").getString(""));
					String subtitle = this.plugin.getChat().replace(config.getNode("subTitle").getString(""));
					messages.add(new TitleMessage(stay_default, interval_default, fadeIn_default, fadeOut_default, title, subtitle));
				} else {
					for(ConfigurationNode config_messages : config.getNode("messages").getChildrenList()) {
						double stay = config_messages.getNode("stay").getDouble(stay_default);
						double interval = config_messages.getNode("next").getDouble(interval_default);
						double fadeIn = config_messages.getNode("fadeIn").getDouble(fadeIn_default);
						double fadeOut = config_messages.getNode("fadeOut").getDouble(fadeOut_default);
								
						String title = this.plugin.getChat().replace(config_messages.getNode("title").getString(""));
						String subTitle = this.plugin.getChat().replace(config_messages.getNode("subTitle").getString(""));
						
						messages.add(new TitleMessage(stay, interval, fadeIn, fadeOut, title, subTitle));
					}
				}
				groups.put((String) group.getKey(), messages);
			}
		}
		return groups;
	}
}
