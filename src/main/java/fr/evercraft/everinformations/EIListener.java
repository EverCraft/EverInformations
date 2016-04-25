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
package fr.evercraft.everinformations;

import java.util.Optional;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.KickPlayerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import fr.evercraft.everapi.server.player.EPlayer;

public class EIListener {
	private EverInformations plugin;
	
	public EIListener(final EverInformations plugin) {
		this.plugin = plugin;
	}
	
	@Listener
	public void onPlayerJoin(final ClientConnectionEvent.Join event) {
		Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(event.getTargetEntity());
		
		if(optPlayer.isPresent()) {
			EPlayer player = optPlayer.get();
			if(player.getFirstDatePlayed() == player.getLastDatePlayed()) {
				this.plugin.getNewbie().addPlayer(player);
			}
			this.plugin.getConnection().joinPlayer(player, player.getGroup());
			event.setMessageCancelled(true);
		}
		
		if(this.plugin.getGame().getServer().getOnlinePlayers().size() == 1) {
			this.plugin.getAutoMessages().start();
		}
	}
	
	@Listener
	public void onPlayerQuit(final ClientConnectionEvent.Disconnect event) {		
		Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(event.getTargetEntity());
		
		if(optPlayer.isPresent()) {
			EPlayer player = optPlayer.get();
			if(player.getFirstDatePlayed() == player.getLastDatePlayed()) {
				this.plugin.getNewbie().removePlayer(player);
			}
			this.plugin.getConnection().quitPlayer(player, player.getGroup());
			event.setMessageCancelled(true);
		}
	}
	
	@Listener
	public void onPlayerQuit(final KickPlayerEvent event) {
		Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(event.getTargetEntity());
		
		if(optPlayer.isPresent()) {
			EPlayer player = optPlayer.get();
			this.plugin.getConnection().kickPlayer(player, player.getGroup(), event.getMessage());
			event.setMessageCancelled(true);
		}
		
		if(this.plugin.getGame().getServer().getOnlinePlayers().size() == 1) {
			this.plugin.getAutoMessages().stop();
		}
	}
}
