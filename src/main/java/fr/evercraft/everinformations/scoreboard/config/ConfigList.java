package fr.evercraft.everinformations.scoreboard.config;

import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everapi.scoreboard.TypeScores;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.objective.ListObjective;

public class ConfigList extends EConfig implements IConfig<ListObjective> {
	public ConfigList(final EverInformations plugin) {
		super(plugin, "scoreboard/scoreboard_list");
	}
	
	@Override
	protected void loadDefault() {
		addDefault("enable", true);
		addDefault("type", "HEALTH", "Type : HEALTH|DEATHS|PLAYER_KILLS|TOTAL_KILLS|ONLINE_PLAYERS|BALANCE|PING|FOOD|LEVEL|XP");
	}
	
	/*
	 * Accesseurs
	 */
	
	public boolean isEnable() {
		return this.get("enable").getBoolean(false);
	}
	
	public List<ListObjective> getObjectives() {
		List<ListObjective> objectives = new ArrayList<ListObjective>();
		
		double stay_default = this.get("stay").getDouble(60);
		double update_default = this.get("update").getDouble(20);
		
		// Objectif unique
		if(this.get("objectives").isVirtual()) {
			try {
				TypeScores type = TypeScores.valueOf(this.get("type").getString("").toUpperCase());
				objectives.add(new ListObjective((EverInformations) this.plugin, stay_default, update_default, type));
			} catch (IllegalArgumentException e) {}
		// Liste d'objectives
		} else {
			for(ConfigurationNode config : this.get("objectives").getChildrenList()) {
				try {
					TypeScores type = TypeScores.valueOf(config.getNode("type").getString("").toUpperCase());
					double stay = config.getNode("stay").getDouble(stay_default);
					double update = config.getNode("update").getDouble(update_default);
					
					objectives.add(new ListObjective((EverInformations) this.plugin, stay, update, type));
				} catch (IllegalArgumentException e) {}
			}
		}
		return objectives;
	}
}
