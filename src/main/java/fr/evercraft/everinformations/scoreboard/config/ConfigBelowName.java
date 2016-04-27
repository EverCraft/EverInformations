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
import java.util.List;

import org.spongepowered.api.text.Text;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.objective.BelowNameObjective;
import fr.evercraft.everinformations.scoreboard.objective.score.ObjectiveType;

public class ConfigBelowName extends EConfig implements IConfig<BelowNameObjective> {
	public ConfigBelowName(final EverInformations plugin) {
		super(plugin, "scoreboard/scoreboard_belowname");
	}
	
	@Override
	protected void loadDefault() {
		addDefault("enable", true);
		addDefault("type", "HEALTH", "Type : HEALTH|DEATHS|PLAYER_KILLS|TOTAL_KILLS");
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
		
		if(this.get("objectives").isVirtual()) {
			try {
				ObjectiveType type = ObjectiveType.valueOf(this.get("type").getString(""));
				Text message = EChat.of(this.plugin.getChat().replace(this.get("name").getString("")));
				objectives.add(new BelowNameObjective(this.plugin, stay_default, update_default, type, message));
			} catch (IllegalArgumentException e) {}
		} else {
			for(ConfigurationNode config : this.get("objectives").getChildrenList()) {				
				try {
					ObjectiveType type = ObjectiveType.valueOf(config.getNode("type").getString(""));
					double stay = config.getNode("stay").getDouble(stay_default);
					double update = config.getNode("update").getDouble(update_default);
					Text message = EChat.of(this.plugin.getChat().replace(config.getNode("name").getString("")));
					
					objectives.add(new BelowNameObjective(this.plugin, stay, update, type, message));
				} catch (IllegalArgumentException e) {}
			}
		}
		return objectives;
	}
}
