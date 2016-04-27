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
package fr.evercraft.everinformations.scoreboard.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.api.text.Text;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.objective.SidebarObjective;
import fr.evercraft.everinformations.scoreboard.objective.SidebarObjective.Type;
import fr.evercraft.everinformations.scoreboard.objective.score.Score.TypeScore;
import fr.evercraft.everinformations.scoreboard.objective.sidebar.SidebarEconomyObjective;
import fr.evercraft.everinformations.scoreboard.objective.sidebar.SidebarInformationsObjective;
import fr.evercraft.everinformations.scoreboard.objective.sidebar.SidebarTitle;

public class ConfigSidebar extends EConfig implements IConfig<SidebarObjective> {
	public ConfigSidebar(final EverInformations plugin) {
		super(plugin, "scoreboard/scoreboard_sidebar");
	}
	
	@Override
	protected void loadDefault() {
		addDefault("enable", true);

		if(this.get("objectives").isVirtual()) {
			List<HashMap<String, Object>> messages = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> message = new HashMap<String, Object>();
			message.put("title", "&6✖ Infos ✖");
			message.put("stay", 300);
			message.put("update", 20);
			message.put("type", Type.INFORMATIONS.name());
			
			HashMap<String, String> scores = new HashMap<String, String>();
			scores.put("&aOnlines :", TypeScore.ONLINE_PLAYERS.name());
			scores.put("&aEmerauds :", TypeScore.BALANCE.name());
			scores.put("&aPing :", TypeScore.PING.name());
			scores.put("&aFood :", TypeScore.FOOD.name());
			scores.put("&aHealth :", TypeScore.HEALTH.name());
			scores.put("&aLevel :", TypeScore.LEVEL.name());
			scores.put("&aXP :", TypeScore.XP.name());

			message.put("scores", scores);
			messages.add(message);
			
			message = new HashMap<String, Object>();
			message.put("title", "&6✖ Top eco ✖");
			message.put("stay", 300);
			message.put("type", Type.ECONOMY.name());
			message.put("message", "&a<player>");
			messages.add(message);
			
			addDefault("objectives", messages, "Type : INFORMATIONS|ECONOMY");
		}
	}
	
	/*
	 * Accesseurs
	 */
	
	public boolean isEnable() {
		return this.get("enable").getBoolean(false);
	}
	
	public List<SidebarObjective> getObjectives() {
		List<SidebarObjective> objectives = new ArrayList<SidebarObjective>();
		
		double stay_default = this.get("stay").getDouble(300);
		double update_default = this.get("update").getDouble(20);
		
		for(ConfigurationNode config : this.get("objectives").getChildrenList()) {
			
			// Chargement du title
			double stay_title_default = config.getNode("title_stay").getDouble(stay_default);
			List<SidebarTitle> titles = new ArrayList<SidebarTitle>();
			
			if(config.getNode("titles").isVirtual()) {
				Text title = EChat.of(this.plugin.getChat().replace(config.getNode("title").getString("")));
				if(!title.isEmpty()) {
					titles.add(new SidebarTitle(stay_title_default, title));
				}
			} else {
				for(ConfigurationNode config_title : config.getNode("titles").getChildrenList()) {
					double stay_title = config_title.getNode("stay").getDouble(stay_title_default);
					Text title = EChat.of(this.plugin.getChat().replace(config_title.getNode("title").getString("")));
					
					if(!title.isEmpty()) {
						titles.add(new SidebarTitle(stay_title, title));
					}
				}
			}
			
			if(!titles.isEmpty()) {
				double stay = config.getNode("stay").getDouble(stay_default);
				double update = config.getNode("update").getDouble(update_default);
				Type type = Type.valueOf(config.getNode("type").getString(""));
				
				if(type != null) {
					if(type.equals(Type.INFORMATIONS)) {
						Map<Text, TypeScore> scores = new HashMap<Text, TypeScore>();
						for(Entry<Object, ? extends ConfigurationNode> config_score : config.getNode("scores").getChildrenMap().entrySet()) {
							if(config_score.getKey() instanceof String) {
								try {
									Text score_value = EChat.of((String) config_score.getKey());
									TypeScore score_type = TypeScore.valueOf(config_score.getValue().getString(""));
									scores.put(score_value, score_type);
								} catch (IllegalArgumentException e) {}
							}
						}
						
						if(!scores.isEmpty()) {
							objectives.add(new SidebarInformationsObjective(this.plugin, stay, update, titles, scores));
						}
						
					} else if(type.equals(Type.ECONOMY)) {
						if(this.plugin.getEverAPI().getManagerService().getTopEconomy().isPresent()) {
							String message = this.plugin.getChat().replace(config.getNode("message").getString("<player>"));
							objectives.add(new SidebarEconomyObjective(this.plugin, stay, update, titles, message));
						}
					}
				}
			}
		}
		
		return objectives;
	}
}
