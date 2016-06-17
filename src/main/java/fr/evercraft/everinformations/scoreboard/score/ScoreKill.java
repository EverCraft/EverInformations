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

import org.spongepowered.api.event.Listener;

import fr.evercraft.everapi.event.StatsUserEvent;
import fr.evercraft.everapi.server.player.EPlayer;

public class ScoreKill extends Score {
	
	@Override
	public int getValue(EPlayer player) {
		return player.getKill();
	}
	
	@Listener
    public void event(StatsUserEvent.Kill event) {
		this.update(event.getKiller().getUniqueId(), TypeScores.KILLS);
	}
	
	@Override
	public boolean isUpdate() {
		return true;
	}
}