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
package fr.evercraft.everinformations.scoreboard.score;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.scoreboard.objective.EObjective;

public abstract class Score {
	
	protected final CopyOnWriteArrayList<EObjective> objectives;
	
	
	public Score() {
		this.objectives = new CopyOnWriteArrayList<EObjective>();
	}
	
	public void addListener(EPlugin plugin, EObjective objective) {
		if(this.objectives.isEmpty()) {
			plugin.getGame().getEventManager().registerListeners(plugin, this);
		}
		this.objectives.add(objective);
	}
	
	public void removeListener(EPlugin plugin, EObjective objective) {
		this.objectives.remove(objective);
		if(this.objectives.isEmpty()) {
			plugin.getGame().getEventManager().unregisterListeners(this);
		}
	}
	
	protected void update(TypeScores type) {
		for(EObjective objective : this.objectives) {
			objective.update(type);
		}
	}
	
	protected void update(UUID uniqueId, TypeScores type) {
		for(EObjective objective : this.objectives) {
			objective.update(type);
		}
	}
	
	public abstract int getValue(EPlayer player);
	
	public abstract boolean isUpdate();
}
