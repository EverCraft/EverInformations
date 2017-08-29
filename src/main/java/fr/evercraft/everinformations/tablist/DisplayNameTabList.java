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
package fr.evercraft.everinformations.tablist;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.InformationService.Priorities;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.tablist.config.ConfigTabList;

public class DisplayNameTabList {
	private final EverInformations plugin;
	
	private boolean enable;
	
	private final ConfigTabList config;
	
	private long update;
	private Task task;

	private String prefix;
	private String suffix;
	 
	public DisplayNameTabList(final EverInformations plugin, ConfigTabList config) {		
		this.plugin = plugin;
		this.config = config;
		
		this.enable = false;
		
		this.reload();
	}

	public void reload() {
		this.stop();
		
		this.enable = this.config.isEnable();
		
		this.prefix = this.config.getDisplayNamePrefix(); 
		this.suffix = this.config.getDisplayNameSuffix();
		this.update = this.config.getDisplayNameUpdate();
		
		if (this.prefix.isEmpty() && this.suffix.isEmpty() && this.enable) {
			this.plugin.getELogger().warn("TabList DisplayName : There is empty");
			this.enable = false;
		} else if (this.enable) {
			this.start();
		}
	}
	
	public void start() {
		if (this.enable) {
			this.sendAll();
			this.startScheduler();
		}
	}
	
	public void stop() {
		if (this.enable) {
			this.stopScheduler();
			this.clearAll();
		}
	}
	
	public void startScheduler() {
		this.stopScheduler();
		
		if (this.enable && this.update > 0) {
			this.plugin.getGame().getScheduler().createTaskBuilder()
				.execute(() -> {
					this.sendAll();
				})
				.delay(this.update, TimeUnit.SECONDS)
				.name("TabList : DisplayName")
				.submit(this.plugin);
		}
	}
	
	public void stopScheduler() {
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
	}

	public void sendAll() {
		for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			this.sendAll(player);
		}
	}

	public void clearAll() {
		if (this.enable) {
			for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				this.clearPlayer(player);
			}
		}
	}
	
	/**
	 * Ajoute le displayname de tous les joueurs à un joueur
	 * @param player
	 */
	public void sendTo(EPlayer player) {
		if (this.enable && player.sendTabList(Priorities.TABLIST)) {
			for (EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
				// TabList du joueur
				Optional<TabListEntry> optEntry = player.getTabList().getEntry(other.getUniqueId());
				String prefix_other = this.plugin.getChat().replace(other.getOption(this.prefix).orElse(""));
				String suffix_other = this.plugin.getChat().replace(other.getOption(this.suffix).orElse(""));
				Text displayName = EChat.of(prefix_other + other.getName() + suffix_other);
				
				if (!other.isVanish() || player.equals(other)) {
					if (optEntry.isPresent()) {
						optEntry.get()
							.setDisplayName(displayName)
							.setGameMode(other.getGameMode());
					} else {
						player.getTabList().addEntry(TabListEntry.builder()
							.profile(other.getProfile())
							.gameMode(other.getGameMode())
							.list(player.getTabList())
							.displayName(displayName)
							.build());
					}
				} else if (player.canSeePlayer(other)) {
					if (optEntry.isPresent()) {
						optEntry.get()
							.setGameMode(GameModes.SPECTATOR)
							.setDisplayName(displayName);
					} else {
						player.getTabList().addEntry(TabListEntry.builder()
							.profile(other.getProfile())
							.gameMode(GameModes.SPECTATOR)
							.list(player.getTabList())
							.displayName(displayName)
							.build());
					}
				} else {
					player.getTabList().removeEntry(other.getUniqueId());
				}
			}
		}
	}
	
	/**
	 * Ajoute le displayname d'un joueur à tous les autres
	 * @param player
	 */
	public void sendAll(EPlayer player) {
		if (this.enable) {
			String prefix_player = this.plugin.getChat().replace(player.getOption(this.prefix).orElse(""));
			String suffix_player = this.plugin.getChat().replace(player.getOption(this.suffix).orElse(""));
			Text displayName = EChat.of(prefix_player + player.getName() + suffix_player);
			
			boolean vanish = player.isVanish();
			Stream<EPlayer> players = this.plugin.getEServer().getOnlineEPlayers().stream()
				.filter(other -> other.hasTabList(Priorities.TABLIST));
			
			// Le joueur n'est pas en vanish
			if (!vanish) {
				players.forEach(other -> {
					Optional<TabListEntry> optEntry = other.getTabList().getEntry(player.getUniqueId());
					
					if (optEntry.isPresent()) {
						optEntry.get()
							.setDisplayName(displayName)
							.setGameMode(player.getGameMode());
					} else {
						other.getTabList().addEntry(TabListEntry.builder()
							.profile(player.getProfile())
							.gameMode(player.getGameMode())
							.list(other.getTabList())
							.displayName(displayName)
							.build());
					}
				});
				
			// Le joueur est en vanish
			} else {
				players.filter(other -> !other.equals(player))
					.forEach(other -> {
						if (other.canSeePlayer(player)) {
							Optional<TabListEntry> optEntry = other.getTabList().getEntry(player.getUniqueId());
							if (optEntry.isPresent()) {
								optEntry.get()
									.setGameMode(GameModes.SPECTATOR)
									.setDisplayName(displayName);
							} else {
								other.getTabList().addEntry(TabListEntry.builder()
									.profile(player.getProfile())
									.gameMode(GameModes.SPECTATOR)
									.list(other.getTabList())
									.displayName(displayName)
									.build());
							}
						} else {
							other.getTabList().removeEntry(player.getUniqueId());
						}
					});
			}
			
			// Le joueur
			Optional<TabListEntry> optEntry = player.getTabList().getEntry(player.getUniqueId());
			if (optEntry.isPresent()) {
				optEntry.get()
					.setDisplayName(displayName)
					.setGameMode(player.getGameMode());
			} else {
				player.getTabList().addEntry(TabListEntry.builder()
					.profile(player.getProfile())
					.gameMode(player.getGameMode())
					.list(player.getTabList())
					.displayName(displayName)
					.build());
			}
		}
	}
	
	/**
	 * Supprime tous les displaynames d'un joueur
	 * @param player
	 */	
	public void clearPlayer(EPlayer player) {
		player.removeTabList(Priorities.TABLIST); 
	}
}
