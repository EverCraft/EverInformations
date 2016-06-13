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
package fr.evercraft.everinformations.tablist;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.tablist.config.ConfigTabList;

public class DisplayNameTabList {
	private final EverInformations plugin;
	
	private boolean enable;
	
	private final ConfigTabList config;

	private String prefix;
	private String suffix;
	 
	public DisplayNameTabList(final EverInformations plugin, ConfigTabList config) {		
		this.plugin = plugin;
		this.config = config;
		
		this.enable = false;
		
		reload();
	}

	public void reload() {
		if(this.enable) {
			stop();
		}
		
		this.enable = this.config.isEnable();
		
		this.prefix = this.config.getPrefix(); 
		this.suffix = this.config.getSuffix();
		
		if (this.prefix.isEmpty() && this.suffix.isEmpty() && this.enable) {
			this.plugin.getLogger().warn("TabList DisplayName : There is empty");
			this.enable = false;
		} else if (this.enable) {
			this.start();
		}
	}

	public void start() {
		for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			String prefix = this.plugin.getChat().replace(player.getOption(this.prefix).orElse(""));
			String suffix = this.plugin.getChat().replace(player.getOption(this.suffix).orElse(""));
			
			for(EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
				Optional<TabListEntry> entry = other.getTabList().getEntry(player.getUniqueId());
				if(entry.isPresent()) {
					entry.get().setDisplayName(EChat.of(prefix + player.getName() + suffix));
				}
			}
		}
	}

	public void stop() {
		if(this.enable) {
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				for(EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
					Optional<TabListEntry> entry = player.getTabList().getEntry(other.getUniqueId());
					if(entry.isPresent()) {
						entry.get().setDisplayName(Text.EMPTY);
					}
				}
			}
		}
	}
	
	public void addPlayer(EPlayer player) {
		if(this.enable) {
			String prefix_player = this.plugin.getChat().replace(player.getOption(this.prefix).orElse(""));
			String suffix_player = this.plugin.getChat().replace(player.getOption(this.suffix).orElse(""));
			
			for(EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
				// TabList du joueur
				Optional<TabListEntry> entry_player = player.getTabList().getEntry(other.getUniqueId());
				if(entry_player.isPresent()) {
					String prefix_other = this.plugin.getChat().replace(other.getOption(this.prefix).orElse(""));
					String suffix_other = this.plugin.getChat().replace(other.getOption(this.suffix).orElse(""));
					entry_player.get().setDisplayName(EChat.of(prefix_other + other.getName() + suffix_other));
				}
				
				// TabList des autres joueurs
				if(!other.equals(player)) {
					Optional<TabListEntry> entry_other = other.getTabList().getEntry(player.getUniqueId());
					if(entry_other.isPresent()) {
						entry_other.get().setDisplayName(EChat.of(prefix_player + player.getName() + suffix_player));
					}
				}
			}
		}
	}
	
	public void removePlayer(EPlayer player) {
		if(this.enable) {
			for(EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
				Optional<TabListEntry> entry = other.getTabList().getEntry(player.getUniqueId());
				if(entry.isPresent()) {
					entry.get().setDisplayName(Text.EMPTY);
				}
			}
		}
	}

	public void updatePlayer(EPlayer player) {
		if(this.enable) {
			String prefix_player = this.plugin.getChat().replace(player.getOption(this.prefix).orElse(""));
			String suffix_player = this.plugin.getChat().replace(player.getOption(this.suffix).orElse(""));
			
			for(EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
				Optional<TabListEntry> entry_other = other.getTabList().getEntry(player.getUniqueId());
				if(entry_other.isPresent()) {
					entry_other.get().setDisplayName(EChat.of(prefix_player + player.getName() + suffix_player));
				}
			}
		}
	}
}
