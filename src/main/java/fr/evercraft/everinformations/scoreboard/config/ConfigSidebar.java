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
package fr.evercraft.everinformations.scoreboard.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import org.spongepowered.api.text.Text;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.message.format.EFormatString;
import fr.evercraft.everapi.message.replace.EReplacesPlayer;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everapi.registers.ScoreType;
import fr.evercraft.everapi.registers.ScoreType.ScoreTypes;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.objective.SidebarObjective;
import fr.evercraft.everinformations.scoreboard.objective.SidebarObjective.Type;
import fr.evercraft.everinformations.scoreboard.sidebar.SidebarEconomyObjective;
import fr.evercraft.everinformations.scoreboard.sidebar.SidebarInformationsObjective;
import fr.evercraft.everinformations.scoreboard.sidebar.SidebarNumbersObjective;
import fr.evercraft.everinformations.scoreboard.sidebar.SidebarStatsObjective;
import fr.evercraft.everinformations.scoreboard.sidebar.SidebarStatsObjective.TypeTop;
import fr.evercraft.everinformations.scoreboard.sidebar.SidebarTitle;
import fr.evercraft.everinformations.scoreboard.sidebar.SidebarStatsObjective.TypeTimes;

public class ConfigSidebar extends EConfig<EverInformations> implements IConfig<SidebarObjective> {
	public ConfigSidebar(final EverInformations plugin) {
		super(plugin, "scoreboard/scoreboard_sidebar");
	}
	
	@Override
	protected void loadDefault() {
		addDefault("enable", true);

		if (this.get("objectives").isVirtual()) {
			List<HashMap<String, Object>> messages = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> message = new HashMap<String, Object>();
			
			// Numbers
			message.put("title", "&6✖ Infos ✖");
			message.put("stay", 60);
			message.put("update", 20);
			message.put("type", Type.NUMBERS.name());
			
			HashMap<String, String> scores = new HashMap<String, String>();
			scores.put("&aOnline :", ScoreTypes.ONLINE_PLAYERS_CANSEE.getName());
			scores.put("&aBalance :", ScoreTypes.BALANCE.getName());
			scores.put("&aPing :", ScoreTypes.PING.getName());
			scores.put("&aFood :", ScoreTypes.FEED.getName());
			scores.put("&aHealth :", ScoreTypes.HEALTH_HEARTS.getName());
			scores.put("&aLevel :", ScoreTypes.LEVEL.getName());
			scores.put("&aXP :", ScoreTypes.XP.getName());

			message.put("scores", scores);
			messages.add(message);
			
			// Informations
			message = new HashMap<String, Object>();
			HashMap<String, String> scores_int = new HashMap<String, String>();
			message.put("title", "&6✖  EverCraft ✖");
			message.put("stay", 60);
			message.put("update", 20);
			message.put("type", Type.INFORMATIONS.name());
			
			scores_int.put("9", "&1");
			scores_int.put("8", "&aJoueur");
			scores_int.put("7", "&4  " + EReplacesPlayer.ONLINE_PLAYERS_CANSEE.getName());
			scores_int.put("6", "&2");
			scores_int.put("5", "&aTeamSpeak :");
			scores_int.put("4", "&4  ts.evercraft.fr");
			scores_int.put("3", "&3");
			scores_int.put("2", "&aSite Web :");
			scores_int.put("1", "&4  evercraft.fr");
			scores_int.put("0", "&4");
			
			
			message.put("scores", scores_int);
			messages.add(message);
			
			// Economy
			message = new HashMap<String, Object>();
			message.put("title", "&6✖  Top eco ✖");
			message.put("stay", 60);
			message.put("type", Type.ECONOMY.name());
			message.put("format", "&a{player}");
			messages.add(message);
			
			// Stats
			message = new HashMap<String, Object>();
			message.put("title", "&6✖  Top Kill ✖");
			message.put("stay", 60);
			message.put("type", Type.STATS.name());
			message.put("top", TypeTop.KILLS.name());
			message.put("time", TypeTimes.MONTH.name());
			message.put("format", "&a{player}");
			messages.add(message);
			
			addDefault("objectives", messages, "Type : INFORMATIONS|NUMBERS|ECONOMY|STATS");
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
		double stay_title_default = this.get("title_stay").getDouble(stay_default);
		
		// Liste des objectives
		for (ConfigurationNode config : this.get("objectives").getChildrenList()) {
			
			// Chargement du title
			double stay_title_default_2 = config.getNode("title_stay").getDouble(stay_title_default);
			List<SidebarTitle> titles = new ArrayList<SidebarTitle>();
			
			// Titre unique
			if (config.getNode("titles").isVirtual()) {
				EFormatString title = EFormatString.of(config.getNode("title").getString(""));
				
				titles.add(new SidebarTitle(stay_title_default_2, title));
			// Liste de titres
			} else {
				for (ConfigurationNode config_title : config.getNode("titles").getChildrenList()) {
					// Titre uniquement
					if (config_title.getValue() instanceof String) {
						EFormatString title = EFormatString.of(config_title.getString(""));
						
						titles.add(new SidebarTitle(stay_title_default_2, title));
					// Titre avec config
					} else {
						double stay_title = config_title.getNode("stay").getDouble(stay_title_default_2);
						EFormatString title = EFormatString.of(config_title.getNode("title").getString(""));
						
						titles.add(new SidebarTitle(stay_title, title));
					}
				}
			}
			
			// Scores
			try {
				if (!titles.isEmpty()) {
					double stay = config.getNode("stay").getDouble(stay_default);
					double update = config.getNode("update").getDouble(update_default);
					Type type = Type.valueOf(config.getNode("type").getString("").toUpperCase());
					
					// Numbers
					if (type.equals(Type.NUMBERS)) {
						Map<Text, ScoreType> scores = new HashMap<Text, ScoreType>();
						for (Entry<Object, ? extends ConfigurationNode> config_score : config.getNode("scores").getChildrenMap().entrySet()) {
							if (config_score.getKey() instanceof String) {
								Optional<ScoreType> score_type = this.plugin.getGame().getRegistry().getType(ScoreType.class, config_score.getValue().getString("").toUpperCase());
								if (score_type.isPresent()) {
									Text score_value = EChat.of((String) config_score.getKey());
									scores.put(score_value, score_type.get());
								} else {
									this.plugin.getELogger().warn("Error during the change of the scoreboard (type='NUMBERS') : score='" + config_score.getValue().getString("") + "'");
								}
							}
						}
						
						if (!scores.isEmpty()) {
							objectives.add(new SidebarNumbersObjective(this.plugin, stay, update, titles, scores));
						} else {
							this.plugin.getELogger().warn("Error during the change of the scoreboard (type='NUMBERS') : Score Empty");
						}
					// Informations	
					} else if (type.equals(Type.INFORMATIONS)) {
						Map<Integer, String> scores = new HashMap<Integer, String>();
						for (Entry<Object, ? extends ConfigurationNode> config_score : config.getNode("scores").getChildrenMap().entrySet()) {
							if (config_score.getKey() instanceof String) {
								try {
									Integer score_int = Integer.parseInt((String) config_score.getKey());
									String score_text = config_score.getValue().getString("");
									scores.put(score_int, score_text);
								} catch (NumberFormatException e) {
									this.plugin.getELogger().warn("Error during the change of the scoreboard (type='INFORMATIONS') : number='" + config_score.getValue().getString("") + "'");
								}
							}
						}
						
						if (!scores.isEmpty()) {
							objectives.add(new SidebarInformationsObjective(this.plugin, stay, update, titles, scores));
						} else {
							this.plugin.getELogger().warn("Error during the change of the scoreboard (type='INFORMATIONS') : Score Empty");
						}
					// Economy
					} else if (type.equals(Type.ECONOMY)) {
						if (this.plugin.getEverAPI().getManagerService().getTopEconomy().isPresent()) {
							String format = config.getNode("format").getString("{player}");
							objectives.add(new SidebarEconomyObjective(this.plugin, stay, update, titles, EFormatString.of(format)));
						} else {
							this.plugin.getELogger().warn("Error during the change of the scoreboard (type='ECONOMY') : There is no EverEconomy");
						}
					// Stats
					} else if (type.equals(Type.STATS)) {
						if (this.plugin.getEverAPI().getManagerService().getStats().isPresent()) {
							try {
								TypeTop top_type = TypeTop.valueOf(config.getNode("top").getString("").toUpperCase());
								try {
									TypeTimes time_type = TypeTimes.valueOf(config.getNode("time").getString("").toUpperCase());
									String format = config.getNode("format").getString("{player}");
									
									objectives.add(new SidebarStatsObjective(this.plugin, stay, titles, EFormatString.of(format), top_type, time_type));
								} catch (IllegalArgumentException e) {
									this.plugin.getELogger().warn("Error during the change of the scoreboard (type='STATS') : time='" + config.getNode("time").getString("") + "'");
								}
							} catch (IllegalArgumentException e) {
								this.plugin.getELogger().warn("Error during the change of the scoreboard (type='STATS') : top='" + config.getNode("top").getString("") + "'");
							}
						} else {
							this.plugin.getELogger().warn("Error during the change of the scoreboard (type='STATS') : There is no EverStats");
						}
					}
				} else {
					this.plugin.getELogger().warn("Error during the change of the scoreboard (type='" + config.getNode("type").getString("") + "') : Title empty ");
				}
			} catch(IllegalArgumentException e) {
				this.plugin.getELogger().warn("Error during the change of the scoreboard : type='" + config.getNode("type").getString("") + "'");
			}
		}
		
		return objectives;
	}
}
