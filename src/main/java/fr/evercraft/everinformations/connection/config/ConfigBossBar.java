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

import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlay;
import org.spongepowered.api.boss.BossBarOverlays;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everapi.sponge.UtilsBossBar;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.connection.Connection;
import fr.evercraft.everinformations.connection.Connection.Connections;
import fr.evercraft.everinformations.message.BossBarMessage;

public class ConfigBossBar extends EConfig implements IConfig<BossBarMessage> {

	public ConfigBossBar(final EverInformations plugin) {
		super(plugin, "connection/connection_bossbar");
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
	
	public Map<String, List<BossBarMessage>> getPlayerJoinMessages() {
		return this.getMessages(Connection.PLAYER, Connections.JOIN);
	}

	public boolean isOthersEnable() {
		return this.isEnable(Connection.OTHERS);
	}
	
	public Map<String, List<BossBarMessage>> getOthersJoinMessages() {
		return this.getMessages(Connection.OTHERS, Connections.JOIN);
	}
	
	public Map<String, List<BossBarMessage>> getOthersQuitMessages() {
		return this.getMessages(Connection.OTHERS, Connections.QUIT);
	}
	
	public Map<String, List<BossBarMessage>> getOthersKickMessages() {
		return this.getMessages(Connection.OTHERS, Connections.KICK);
	}
	
	/*
	 * Fonctions
	 */
	
	private boolean isEnable(String prefix) {
		return this.get(prefix + ".enable").getBoolean(false);
	}
	
	private Map<String, List<BossBarMessage>> getMessages(String prefix, Connections connections) {
		Map<String, List<BossBarMessage>> groups = new HashMap<String, List<BossBarMessage>>();
		
		// Default
		double stay_default = this.get("stay").getDouble(30);
		double next_default = this.get("interval").getDouble(0);
		float percent_default = this.get("percent").getFloat(100);
		BossBarColor color_default = UtilsBossBar.getColor(this.get("color").getString("")).orElse(BossBarColors.WHITE);
		BossBarOverlay overlay_default = UtilsBossBar.getOverlay(this.get("overlay").getString("")).orElse(BossBarOverlays.PROGRESS);
		boolean darkenSky_default = this.get("darkenSky").getBoolean(false);
		boolean playEndBossMusic_default = this.get("playEndBossMusic").getBoolean(false);
		boolean createFog_default = this.get("createFog").getBoolean(false);
		
		for(Entry<Object, ? extends CommentedConfigurationNode> group : this.get(prefix).getChildrenMap().entrySet()) {
			if(group.getKey() instanceof String && !((String) group.getKey()).equals("enable")) {  
				CommentedConfigurationNode config = group.getValue().getNode(connections.name());
				List<BossBarMessage> messages = new ArrayList<BossBarMessage>();
				
				double stay_player = config.getNode("stay").getDouble(stay_default);
				double next_player = config.getNode("interval").getDouble(next_default);
				float percent_player = config.getNode("percent").getFloat(percent_default);
				BossBarColor color_player = UtilsBossBar.getColor(config.getNode("color").getString("")).orElse(color_default);
				BossBarOverlay overlay_player = UtilsBossBar.getOverlay(config.getNode("overlay").getString("")).orElse(overlay_default);
				boolean darkenSky_player = config.getNode("darkenSky").getBoolean(darkenSky_default);
				boolean playEndBossMusic_player = config.getNode("playEndBossMusic").getBoolean(playEndBossMusic_default);
				boolean createFog_player = config.getNode("createFog").getBoolean(createFog_default);
				
				// Message unique
				if(config.getNode("messages").isVirtual()) {					
					String message = this.plugin.getChat().replace(config.getNode("message").getString(""));
					
					if(!message.isEmpty()) {
						messages.add(new BossBarMessage(stay_player, next_player, message, percent_player, color_player, overlay_player,
								darkenSky_player, playEndBossMusic_player, createFog_player));
					}
				// Liste de messages
				} else {
					for(ConfigurationNode config_messages : config.getNode("messages").getChildrenList()) {
						double stay = config_messages.getNode("stay").getDouble(stay_player);
						double next = config_messages.getNode("next").getDouble(config_messages.getNode("interval").getDouble(next_player));
						float percent = config_messages.getNode("percent").getFloat(percent_player);
						BossBarColor color = UtilsBossBar.getColor(config_messages.getNode("color").getString("")).orElse(color_player);
						BossBarOverlay overlay = UtilsBossBar.getOverlay(config_messages.getNode("overlay").getString("")).orElse(overlay_player);
						boolean darkenSky = config_messages.getNode("darkenSky").getBoolean(darkenSky_player);
						boolean playEndBossMusic = config_messages.getNode("playEndBossMusic").getBoolean(playEndBossMusic_player);
						boolean createFog = config_messages.getNode("createFog").getBoolean(createFog_player);
						
						String message = this.plugin.getChat().replace(config_messages.getNode("message").getString(""));
						
						if(!message.isEmpty()) {
							messages.add(new BossBarMessage(stay, next, message, percent, color, overlay,
									darkenSky, playEndBossMusic, createFog));
						}
					}
				}
				groups.put((String) group.getKey(), messages);
			}
		}
		return groups;
	}
}
