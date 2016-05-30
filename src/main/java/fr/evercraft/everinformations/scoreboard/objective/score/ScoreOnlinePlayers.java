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
package fr.evercraft.everinformations.scoreboard.objective.score;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.event.VanishEvent;
import fr.evercraft.everinformations.scoreboard.objective.EObjective;

public class ScoreOnlinePlayers extends Score {
	
	@Override
	public int getValue(EPlayer player) {
		return player.getOnlinePlayers().size();
	}
	
	@Listener
    public void joinEvent(ClientConnectionEvent.Join event) {
		Sponge.getGame().getServer().getBroadcastChannel().send(Text.of(TextColors.RED, "Join"));
		for(EObjective objective : this.objectives) {
			objective.update(TypeScore.ONLINE_PLAYERS);
		}
	}
	
	@Listener(order=Order.POST)
    public void quitEvent(ClientConnectionEvent.Disconnect event) {
		Sponge.getGame().getServer().getBroadcastChannel().send(Text.of(TextColors.RED, "Disconnect"));
		for(EObjective objective : this.objectives) {
			objective.update(TypeScore.ONLINE_PLAYERS);
		}
	}
	
	@Listener
    public void vanishEvent(VanishEvent event) {
		Sponge.getGame().getServer().getBroadcastChannel().send(Text.of(TextColors.RED, "Vanish"));
		for(EObjective objective : this.objectives) {
			objective.update(TypeScore.ONLINE_PLAYERS);
		}
	}
	
	@Override
	public boolean isUpdate() {
		return true;
	}
}