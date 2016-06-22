package fr.evercraft.everinformations.connection.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.connection.Connection;
import fr.evercraft.everinformations.connection.Connection.Connections;
import fr.evercraft.everinformations.message.ChatMessage;

public class ConfigChat extends EConfig implements IConfig<ChatMessage> {
	public ConfigChat(final EverInformations plugin) {
		super(plugin, "connection/connection_chat");
	}
	
	@Override
	protected void loadDefault() {		
		addDefault(Connection.PLAYER + ".enable", true);
		if(this.get(Connection.PLAYER + "." + Connection.DEFAULT + "." + Connections.JOIN.name() + ".messages").isVirtual()) {
			addDefault(Connection.PLAYER + "." + Connection.DEFAULT + "." + Connections.JOIN.name() + ".message", "&7&l[&2+&7&l] <DISPLAYNAME_FORMAT> &7joined the game");
		}
			
		addDefault(Connection.OTHERS + ".enable", true);
		if(this.get(Connection.OTHERS + "." + Connection.DEFAULT + "." + Connections.JOIN.name() + ".messages").isVirtual()) {
			addDefault(Connection.OTHERS + "." + Connection.DEFAULT + "." + Connections.JOIN.name() + ".message", "&7&l[&2+&7&l] <DISPLAYNAME_FORMAT> &7joined the game");
		}
		if(this.get(Connection.OTHERS + "." + Connection.DEFAULT + "." + Connections.QUIT.name() + ".messages").isVirtual()) {
			addDefault(Connection.OTHERS + "." + Connection.DEFAULT + "." + Connections.QUIT.name() + ".message", "&7&l[&4-&7&l] <DISPLAYNAME_FORMAT> &7left the game");
		}
		if(this.get(Connection.OTHERS + "." + Connection.DEFAULT + "." + Connections.KICK.name() + ".messages").isVirtual()) {
			addDefault(Connection.OTHERS + "." + Connection.DEFAULT + "." + Connections.KICK.name() + ".message", "&7&l[&4-&7&l] <DISPLAYNAME_FORMAT> &7has been kicked out of the game for <reason>");
		}
	}
	
	/*
	 * Accesseurs
	 */
	
	public boolean isPlayerEnable() {
		return this.isEnable(Connection.PLAYER);
	}
	
	public Map<String, List<ChatMessage>> getPlayerJoinMessages() {
		return this.getMessages(Connection.PLAYER, Connections.JOIN);
	}
	
	public boolean isOthersEnable() {
		return this.isEnable(Connection.OTHERS);
	}
	
	public Map<String, List<ChatMessage>> getOthersJoinMessages() {
		return this.getMessages(Connection.OTHERS, Connections.JOIN);
	}
	
	public Map<String, List<ChatMessage>> getOthersQuitMessages() {
		return this.getMessages(Connection.OTHERS, Connections.QUIT);
	}
	
	public Map<String, List<ChatMessage>> getOthersKickMessages() {
		return this.getMessages(Connection.OTHERS, Connections.KICK);
	}
	
	/*
	 * Fonctions
	 */
	
	private boolean isEnable(String prefix) {
		return this.get(prefix + ".enable").getBoolean(false);
	}
	
	private Map<String, List<ChatMessage>> getMessages(String prefix, Connections connections) {
		Map<String, List<ChatMessage>> groups = new HashMap<String, List<ChatMessage>>();
		
		// Default
		double interval_default = this.get("interval").getDouble(0);
		String prefix_default = this.get("prefix").getString("");
		
		for(Entry<Object, ? extends CommentedConfigurationNode> group : this.get(prefix).getChildrenMap().entrySet()) {
			if(group.getKey() instanceof String && !((String) group.getKey()).equals("enable")) {
				CommentedConfigurationNode config = group.getValue().getNode(connections.name());
				List<ChatMessage> messages = new ArrayList<ChatMessage>();
				
				// Default Player
				double interval_player = config.getNode("interval").getDouble(interval_default);
				String prefix_player = config.getNode("prefix").getString(prefix_default);
				
				// Message unique
				if(config.getNode("messages").isVirtual()) {
					String message = this.plugin.getChat().replace(config.getNode("message").getString(""));
					if(!message.isEmpty()) {
						messages.add(new ChatMessage(interval_player, TextSerializers.FORMATTING_CODE, prefix_player, message));
					}
				// Liste de messages
				} else {
					for(ConfigurationNode config_messages : config.getNode("messages").getChildrenList()) {
						// Message uniquement
						if(config_messages.getValue() instanceof String) {
							messages.add(new ChatMessage(interval_player, TextSerializers.FORMATTING_CODE, prefix_player, this.plugin.getChat().replace(config_messages.getString(""))));
						// Message avec config
						} else {							
							double interval = config_messages.getNode("next").getDouble(config_messages.getNode("interval").getDouble(interval_player));
							String prefix_message = this.plugin.getChat().replace(config_messages.getNode("prefix").getString(prefix_player));
							String message = this.plugin.getChat().replace(config_messages.getNode("message").getString(""));
							
							String type_string = config_messages.getNode("format").getString("");
							TextSerializer type = TextSerializers.FORMATTING_CODE;
							if(type_string.equalsIgnoreCase("JSON")) {
								type = TextSerializers.JSON;
							} else if(type_string.equalsIgnoreCase("TEXT_XML")) {
								type = TextSerializers.TEXT_XML;
							}
							if(!message.isEmpty()) {
								messages.add(new ChatMessage(interval, type, prefix_message, message));
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
