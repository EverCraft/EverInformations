package fr.evercraft.everinformations.scoreboard.config;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.text.Text;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everapi.scoreboard.TypeScores;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.objective.BelowNameObjective;

public class ConfigBelowName extends EConfig implements IConfig<BelowNameObjective> {
	public ConfigBelowName(final EverInformations plugin) {
		super(plugin, "scoreboard/scoreboard_belowname");
	}
	
	@Override
	protected void loadDefault() {
		addDefault("enable", true);
		addDefault("type", "HEALTH", "Type : HEALTH|DEATHS|KILLS|RATIO|TOTAL_KILLS|ONLINE_PLAYERS|BALANCE|PING|FOOD|LEVEL|XP");
		addDefault("name", "&4❤");
	}
	
	/*
	 * Accesseurs
	 */
	
	public boolean isEnable() {
		return this.get("enable").getBoolean(false);
	}
	
	public List<BelowNameObjective> getObjectives() {
		List<BelowNameObjective> objectives = new ArrayList<BelowNameObjective>();
		
		double stay_default = this.get("stay").getDouble(60);
		double update_default = this.get("update").getDouble(20);
		
		// Objectif unique
		if(this.get("objectives").isVirtual()) {
			try {
				TypeScores type = TypeScores.valueOf(this.get("type").getString("").toUpperCase());
				Text message = EChat.of(this.plugin.getChat().replace(this.get("name").getString("")));
				
				objectives.add(new BelowNameObjective((EverInformations) this.plugin, stay_default, update_default, type, message));
			} catch (IllegalArgumentException e) {}
		// Liste d'objectives
		} else {
			for(ConfigurationNode config : this.get("objectives").getChildrenList()) {				
				try {
					TypeScores type = TypeScores.valueOf(config.getNode("type").getString("").toUpperCase());
					double stay = config.getNode("stay").getDouble(stay_default);
					double update = config.getNode("update").getDouble(update_default);
					Text message = EChat.of(this.plugin.getChat().replace(config.getNode("name").getString("")));
					
					objectives.add(new BelowNameObjective((EverInformations) this.plugin, stay, update, type, message));
				} catch (IllegalArgumentException e) {}
			}
		}
		return objectives;
	}
}
