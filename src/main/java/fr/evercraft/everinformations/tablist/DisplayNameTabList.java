package fr.evercraft.everinformations.tablist;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.tab.TabListEntry;

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
				if(entry.isPresent() && other.sendTabList(ManagerTabList.IDENTIFIER, this.plugin.getTabList().getPriority())) {
					entry.get().setDisplayName(EChat.of(prefix + player.getName() + suffix));
				}
			}
		}
	}

	public void stop() {
		if(this.enable) {
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				if(player.hasTabList(ManagerTabList.IDENTIFIER)) {
					for(EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
						Optional<TabListEntry> entry = player.getTabList().getEntry(other.getUniqueId());
						if(entry.isPresent()) {
							entry.get().setDisplayName(null);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Ajoute le displayname de tous les joueurs à un joueur
	 * @param player
	 */
	public void addPlayer(EPlayer player) {
		if(this.enable && player.hasTabList(ManagerTabList.IDENTIFIER)) {
			for(EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
				// TabList du joueur
				Optional<TabListEntry> entry_player = player.getTabList().getEntry(other.getUniqueId());
				if(entry_player.isPresent()) {
					String prefix_other = this.plugin.getChat().replace(other.getOption(this.prefix).orElse(""));
					String suffix_other = this.plugin.getChat().replace(other.getOption(this.suffix).orElse(""));
					entry_player.get().setDisplayName(EChat.of(prefix_other + other.getName() + suffix_other));
				}
			}
		}
	}
	
	/**
	 * Ajoute le displayname d'un joueur à tous les autres
	 * @param player
	 */
	public void addOther(EPlayer player) {
		if(this.enable) {
			String prefix_player = this.plugin.getChat().replace(player.getOption(this.prefix).orElse(""));
			String suffix_player = this.plugin.getChat().replace(player.getOption(this.suffix).orElse(""));
			
			for(EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
				// TabList des autres joueurs
				if(other.hasTabList(ManagerTabList.IDENTIFIER)) {
					Optional<TabListEntry> entry_other = other.getTabList().getEntry(player.getUniqueId());
					if(entry_other.isPresent()) {
						entry_other.get().setDisplayName(EChat.of(prefix_player + player.getName() + suffix_player));
					}
				}
			}
		}
	}
	
	/**
	 * Supprime tous les displaynames d'un joueur
	 * @param player
	 */
	public void removePlayer(EPlayer player) {
		if(this.enable && player.hasTabList(ManagerTabList.IDENTIFIER)) {
			for(EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
				Optional<TabListEntry> entry = player.getTabList().getEntry(other.getUniqueId());
				if(entry.isPresent()) {
					entry.get().setDisplayName(null);
				}
			}
		}
	}
}
