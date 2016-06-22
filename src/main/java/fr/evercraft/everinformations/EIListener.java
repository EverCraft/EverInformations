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
package fr.evercraft.everinformations;

import java.util.Optional;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.KickPlayerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import fr.evercraft.everapi.event.ActionBarEvent;
import fr.evercraft.everapi.event.BossBarEvent;
import fr.evercraft.everapi.event.PermGroupEvent;
import fr.evercraft.everapi.event.PermSystemEvent;
import fr.evercraft.everapi.event.PermUserEvent;
import fr.evercraft.everapi.event.ScoreBoardEvent;
import fr.evercraft.everapi.event.TabListEvent;
import fr.evercraft.everapi.event.TitleEvent;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.event.VanishEvent;
import fr.evercraft.everapi.services.essentials.event.VanishEvent.Action;

public class EIListener {
	private EverInformations plugin;
	
	public EIListener(final EverInformations plugin) {
		this.plugin = plugin;
	}
	
	@Listener
	public void onPlayerJoin(final ClientConnectionEvent.Join event) {
		Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(event.getTargetEntity());
		
		// Chargement du EPlayer
		if(optPlayer.isPresent()) {
			EPlayer player = optPlayer.get();
			
			// Newbie
			if(player.getFirstDatePlayed() == player.getLastDatePlayed()) {
				this.plugin.getNewbie().addPlayer(player);
				
				if(this.plugin.getConfigs().isNewbieAndConnection()) {
					// Connection
					this.plugin.getConnection().joinPlayer(player, player.getGroup());
				}
			} else {
				// Connection
				this.plugin.getConnection().joinPlayer(player, player.getGroup());
			}
			
			// Supprime le message par défaut
			if(this.plugin.getConnection().isEnableChat()) {
				event.setMessageCancelled(true);
			}
			
			// ScoreBoard
			this.plugin.getScoreBoard().addPlayer(player);
			
			// NameTag
			this.plugin.getNameTag().addPlayer(player);
			
			// TabList
			this.plugin.getTabList().addPlayer(player);
		}
		
		// Active l'AutoMessage
		if(this.plugin.getGame().getServer().getOnlinePlayers().size() == 1) {
			this.plugin.getAutoMessages().start();
		}
	}
	
	@Listener
	public void onPlayerQuit(final ClientConnectionEvent.Disconnect event) {		
		Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(event.getTargetEntity());
		
		// Chargement du EPlayer
		if(optPlayer.isPresent()) {
			EPlayer player = optPlayer.get();
			
			// Newbie
			if(player.getFirstDatePlayed() == player.getLastDatePlayed()) {
				this.plugin.getNewbie().removePlayer(player);
			}
			
			// Connection
			this.plugin.getConnection().quitPlayer(player, player.getGroup());
			
			// Supprime le message par défaut
			if(this.plugin.getConnection().isEnableChat()) {
				event.setMessageCancelled(true);
			}
			
			// AutoMessage
			this.plugin.getAutoMessages().removePlayer(player);
			
			// ScoreBoard
			this.plugin.getScoreBoard().removePlayer(player);
			
			// NameTag
			this.plugin.getNameTag().removePlayer(player);
			
			// TabList
			this.plugin.getTabList().removePlayer(player);
		}
		
		// Désactive l'AutoMessage
		if(this.plugin.getGame().getServer().getOnlinePlayers().size() == 1) {
			this.plugin.getAutoMessages().stop();
		}
	}
	
	/*
	 * Pas encore implementer
	 */
	@Listener
	public void onPlayerKick(final KickPlayerEvent event) {
		Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(event.getTargetEntity());
		
		// Chargement du EPlayer
		if(optPlayer.isPresent()) {
			EPlayer player = optPlayer.get();
			this.plugin.getConnection().kickPlayer(player, player.getGroup(), event.getMessage());
			
			if(this.plugin.getConnection().isEnableChat()) {
				event.setMessageCancelled(true);
			}
		}
	}
	
	@Listener
    public void vanishEvent(VanishEvent event) {
		if(this.plugin.getConfigs().isVanishFake()) {
			if(event.getAction().equals(Action.ADD)) {
				this.plugin.getConnection().quitPlayer(event.getEPlayer(), event.getEPlayer().getGroup());
			} else {
				this.plugin.getConnection().joinPlayer(event.getEPlayer(), event.getEPlayer().getGroup());
				this.plugin.getTabList().updatePlayer(event.getEPlayer());
			}
		}
	}
	
	@Listener
    public void permUserEvent(PermUserEvent event) {
		if(event.getPlayer().isPresent() && (event.getAction().equals(PermUserEvent.Action.USER_OPTION_CHANGED) ||
			event.getAction().equals(PermUserEvent.Action.USER_GROUP_CHANGED) ||
			event.getAction().equals(PermUserEvent.Action.USER_SUBGROUP_CHANGED))) {
			// NameTag
			this.plugin.getNameTag().updatePermission(event.getPlayer().get());
			
			// TabList
			this.plugin.getTabList().updatePlayer(event.getPlayer().get());
		}
	}
	
	@Listener
    public void permGroupEvent(PermGroupEvent event) {
		for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			if(player.isChildOf(event.getSubject())) {
				// NameTag
				this.plugin.getNameTag().updatePermission(player);
				
				// TabList
				this.plugin.getTabList().updatePlayer(player);
			}
		}
	}
	
	@Listener
    public void permSystemEvent(PermSystemEvent event) {
		for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			// NameTag
			this.plugin.getNameTag().updatePermission(player);
			
			// TabList
			this.plugin.getTabList().updatePlayer(player);
		}
	}
	
	@Listener
    public void scoreBoardEvent(ScoreBoardEvent.Remove event) {
		this.plugin.getScoreBoard().eventScoreBoard(event.getPlayer(), event.getDisplaySlot(), event.getIdentifier());
	}
	
	@Listener
    public void tabListEvent(TabListEvent.Remove event) {
		this.plugin.getTabList().eventTabList(event.getPlayer(), event.getIdentifier());
	}
	
	@Listener
    public void bossBarEvent(BossBarEvent.Remove event) {
		this.plugin.getAutoMessages().eventBossBar(event.getPlayer(), event.getIdentifier());
	}
	
	@Listener
    public void titleEvent(TitleEvent.Remove event) {
		this.plugin.getAutoMessages().eventTitle(event.getPlayer(), event.getIdentifier());
	}
	
	@Listener
    public void actionBarEvent(ActionBarEvent.Remove event) {
		this.plugin.getAutoMessages().eventActionBar(event.getPlayer(), event.getIdentifier());
	}
}
