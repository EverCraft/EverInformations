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
package fr.evercraft.everinformations.scoreboard.objective;

import java.util.UUID;

import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.scoreboard.objective.score.Score.TypeScore;

public abstract class EObjective  {
	
	protected final EPlugin plugin;
	
	// En secondes
	protected final double stay;
	protected final double update;
	
	public EObjective(EPlugin plugin, double stay, double update) {
		this.plugin = plugin;
		this.stay = stay;
		this.update = update;
	}
	
	/**
	 * En Millisecondes
	 * @return
	 */
	public long getNext() {
		return (long) (this.stay * 1000);
	}
	
	/**
	 * En Millisecondes
	 * @return
	 */
	public long getUpdate() {
		return (long) (this.update * 1000);
	}

	public abstract boolean isUpdate();
	
	public abstract boolean add(int priority, EPlayer player);
	public abstract boolean remove(EPlayer player);
	
	public abstract boolean start();
	public abstract boolean stop();

	public abstract void update();
	public abstract void update(TypeScore type);
	public abstract void update(UUID uniqueId, TypeScore type);
}
