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

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.ChangeGameModeEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.KickPlayerEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import fr.evercraft.everapi.event.ActionBarEvent;
import fr.evercraft.everapi.event.BossBarEvent;
import fr.evercraft.everapi.event.NameTagEvent;
import fr.evercraft.everapi.event.PermGroupEvent;
import fr.evercraft.everapi.event.PermSystemEvent;
import fr.evercraft.everapi.event.PermUserEvent;
import fr.evercraft.everapi.event.ScoreBoardEvent;
import fr.evercraft.everapi.event.TabListEvent;
import fr.evercraft.everapi.event.TitleEvent;
import fr.evercraft.everapi.event.VanishEvent;
import fr.evercraft.everapi.server.player.EPlayer;

public class EIListener {
	private EverInformations plugin;
	
	public EIListener(final EverInformations plugin) {
		this.plugin = plugin;
	}
	
	@Listener
	public void onPlayerJoin(final ClientConnectionEvent.Join event) {
		EPlayer player = this.plugin.getEServer().getEPlayer(event.getTargetEntity());

		// Newbie
		if (player.getFirstDatePlayed() == player.getLastDatePlayed()) {
			this.plugin.getNewbie().addPlayer(player);
			
			if (this.plugin.getConfigs().isNewbieAndConnection()) {
				// Connection
				this.plugin.getConnection().joinPlayer(player, player.getGroup());
			}
		} else if (!player.isVanish()) {
			// Connection
			this.plugin.getConnection().joinPlayer(player, player.getGroup());
		}
		
		// Supprime le message par défaut
		if (this.plugin.getConnection().isEnableChat()) {
			event.setMessageCancelled(true);
		}
		
		// ScoreBoard
		this.plugin.getScoreBoard().addPlayer(player);
		
		// NameTag
		this.plugin.getNameTag().addPlayer(player);
		
		// TabList
		this.plugin.getTabList().addPlayer(player);

		// Active l'AutoMessage
		if (this.plugin.getGame().getServer().getOnlinePlayers().size() == 1) {
			this.plugin.getAutoMessages().start();
			this.plugin.getNameTag().startScheduler();
			this.plugin.getTabList().getDisplayName().startScheduler();
		}
	}
	
	@Listener
	public void onPlayerQuit(final ClientConnectionEvent.Disconnect event) {		
		EPlayer player = this.plugin.getEServer().getEPlayer(event.getTargetEntity());
		
		// Newbie
		if (player.getFirstDatePlayed() == player.getLastDatePlayed()) {
			this.plugin.getNewbie().removePlayer(player);
		}
		
		// Connection
		this.plugin.getConnection().quitPlayer(player, player.getGroup());
		
		// Supprime le message par défaut
		if (this.plugin.getConnection().isEnableChat()) {
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
		
		// Désactive l'AutoMessage
		if (this.plugin.getGame().getServer().getOnlinePlayers().size() == 1) {
			this.plugin.getAutoMessages().stop();
			this.plugin.getNameTag().stopScheduler();
			this.plugin.getTabList().getDisplayName().stopScheduler();
		}
	}
	
	/*
	 * Pas encore implementer
	 */
	@Listener
	public void onPlayerKick(final KickPlayerEvent event) {
		EPlayer player = this.plugin.getEServer().getEPlayer(event.getTargetEntity());
		
		this.plugin.getConnection().kickPlayer(player, player.getGroup(), event.getMessage());
		
		if (this.plugin.getConnection().isEnableChat()) {
			event.setMessageCancelled(true);
		}
	}	
	
	@Listener
	public void onMoveEntity(MoveEntityEvent.Teleport event) {
		if (!(event.getTargetEntity() instanceof Player)) return;
		if (event.getFromTransform().getExtent().equals(event.getToTransform().getExtent())) return;
		
		final EPlayer player = this.plugin.getEServer().getEPlayer((Player) event.getTargetEntity());
		
		this.plugin.getGame().getScheduler().createTaskBuilder().execute(() -> {
			// NameTag
			this.plugin.getNameTag().updatePermission(player);
			
			// TabList
			this.plugin.getTabList().updatePlayer(player);
		}).submit(this.plugin);
	}
	
	@Listener
	public void onRespawnPlayer(RespawnPlayerEvent event) {
		EPlayer player = this.plugin.getEServer().getEPlayer(event.getTargetEntity());
		
		if (player.isVanish()) {
			// TabList : Permet de le remettre en Spectator
			this.plugin.getTabList().updatePlayer(player);
		}
	}
	
	@Listener
	public void onChangeGameMode(ChangeGameModeEvent.TargetPlayer event) {
		if (!(event.getTargetEntity() instanceof Player)) return;		
		EPlayer player = this.plugin.getEServer().getEPlayer((Player) event.getTargetEntity());
		
		if (player.isVanish()) {
			this.plugin.getGame().getScheduler().createTaskBuilder().execute(() -> {
				// TabList : Permet de le remettre en Spectator
				this.plugin.getTabList().updatePlayer(player);
			}).submit(this.plugin);
		}
	}
	
	@Listener
    public void vanishEvent(VanishEvent event) {
		if (event.getPlayer().isDisconnected()) return;
		
		if (this.plugin.getConfigs().isVanishFake()) {
			if (event.getValue()) {
				this.plugin.getConnection().quitPlayerFake(event.getPlayer(), event.getPlayer().getGroup());
			} else {
				this.plugin.getConnection().joinPlayerFake(event.getPlayer(), event.getPlayer().getGroup());
			}
		}
		
		this.plugin.getGame().getScheduler().createTaskBuilder().execute(() -> {
			this.plugin.getTabList().updatePlayer(event.getPlayer());
		}).submit(this.plugin);
	}
	
	@Listener
    public void permUserEvent(PermUserEvent event) {
		if (event.getPlayer().isPresent() && (event.getAction().equals(PermUserEvent.Action.USER_OPTION_CHANGED) ||
			event.getAction().equals(PermUserEvent.Action.USER_GROUP_CHANGED) ||
			event.getAction().equals(PermUserEvent.Action.USER_SUBGROUP_CHANGED))) {
			
			this.plugin.getGame().getScheduler().createTaskBuilder().execute(() -> {
				// NameTag
				this.plugin.getNameTag().updatePermission(event.getPlayer().get());
				
				// TabList
				this.plugin.getTabList().updatePlayer(event.getPlayer().get());
			}).submit(this.plugin);
		}
	}
	
	@Listener
    public void permGroupEvent(PermGroupEvent event) {
		this.plugin.getGame().getScheduler().createTaskBuilder().execute(() -> {
			for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				if (player.isChildOf(event.getSubject())) {
					// NameTag
					this.plugin.getNameTag().updatePermission(player);
					
					// TabList
					this.plugin.getTabList().updatePlayer(player);
				}
			}
		}).submit(this.plugin);
	}
	
	@Listener
    public void permSystemEvent(PermSystemEvent event) {
		this.plugin.getGame().getScheduler().createTaskBuilder().execute(() -> {
			for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				// NameTag
				this.plugin.getNameTag().updatePermission(player);
				
				// TabList
				this.plugin.getTabList().updatePlayer(player);
			}
		}).submit(this.plugin);
	}
	
	@Listener
    public void scoreBoardEvent(ScoreBoardEvent.Remove event) {
		if (!event.getPlayer().isDisconnected()) {
			this.plugin.getScoreBoard().eventScoreBoard(event.getPlayer(), event.getDisplaySlot(), event.getIdentifier());
		}
	}
	
	@Listener
    public void tabListEvent(TabListEvent.Remove event) {
		if (!event.getPlayer().isDisconnected()) {
			this.plugin.getTabList().eventTabList(event.getPlayer(), event.getIdentifier());
		}
	}
	
	@Listener
    public void bossBarEvent(BossBarEvent.Remove event) {
		if (!event.getPlayer().isDisconnected()) {
			this.plugin.getAutoMessages().eventBossBar(event.getPlayer(), event.getIdentifier());
		}
	}
	
	@Listener
    public void titleEvent(TitleEvent.Remove event) {
		if (!event.getPlayer().isDisconnected()) {
			this.plugin.getAutoMessages().eventTitle(event.getPlayer(), event.getIdentifier());
		}
	}
	
	@Listener
    public void actionBarEvent(ActionBarEvent.Remove event) {
		if (!event.getPlayer().isDisconnected()) {
			this.plugin.getAutoMessages().eventActionBar(event.getPlayer(), event.getIdentifier());
		}
	}
	
	@Listener
    public void nameTagEvent(NameTagEvent.Remove event) {
		if (!event.getPlayer().isDisconnected()) {
			this.plugin.getNameTag().eventNameTag(event.getPlayer());
		}
	}
}
