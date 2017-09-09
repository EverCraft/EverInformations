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
import java.util.List;
import java.util.Optional;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everapi.registers.ScoreType;
import fr.evercraft.everapi.registers.ScoreType.ScoreTypes;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.objective.ListObjective;

public class ConfigList extends EConfig<EverInformations> implements IConfig<ListObjective> {
	public ConfigList(final EverInformations plugin) {
		super(plugin, "scoreboard/scoreboard_list");
	}
	
	@Override
	protected void loadDefault() {
		addDefault("enable", true);
		addDefault("type", ScoreTypes.HEALTH.getName().toUpperCase() , "Type : HEALTH|HEALTH_INTEGER|DEATHS|PLAYER_KILLS|TOTAL_KILLS|BALANCE|PING|FOOD|LEVEL|XP");
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
		if (this.get("objectives").isVirtual()) {
			Optional<ScoreType> type = this.plugin.getGame().getRegistry().getType(ScoreType.class, this.get("type").getString("").toUpperCase());
			if (type.isPresent()) {
				objectives.add(new ListObjective(this.plugin, stay_default, update_default, type.get()));
			} else {
				this.plugin.getELogger().warn("Error during the change of the ConfigList : score='" + this.get("type").getString("") + "'");
			}
		// Liste d'objectives
		} else {
			for (ConfigurationNode config : this.get("objectives").getChildrenList()) {
				Optional<ScoreType> type = this.plugin.getGame().getRegistry().getType(ScoreType.class, config.getNode("type").getString("").toUpperCase());
				if (type.isPresent()) {
					double stay = config.getNode("stay").getDouble(stay_default);
					double update = config.getNode("update").getDouble(update_default);
					
					objectives.add(new ListObjective(this.plugin, stay, update, type.get()));
				} else {
					this.plugin.getELogger().warn("Error during the change of the ConfigList : score='" + this.get("type").getString("") + "'");
				}
			}
		}
		return objectives;
	}
}
