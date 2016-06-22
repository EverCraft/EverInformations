package fr.evercraft.everinformations.nametag;

import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.nametag.config.ConfigNameTag;

public class ManagerNameTag {
	private final static String IDENTIFIER = "everinformations";
	
	private final EverInformations plugin;
	
	private final ConfigNameTag config;

	private boolean enable;
	
	private String prefix;
	private String suffix;
	 
	public ManagerNameTag(final EverInformations plugin) {		
		this.plugin = plugin;
		
		this.config = new ConfigNameTag(this.plugin);
		this.enable = false;

		this.reload();
	}

	public void reload(){		
		this.stop();
		
		this.enable = this.config.isEnable();
		this.prefix = this.config.getPrefix(); 
		this.suffix = this.config.getSuffix();
		
		this.start();
	}

	public void start() {
		if(this.enable) {
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				Text prefix = EChat.of(player.getOption(this.prefix).orElse(""));
				Text suffix = EChat.of(player.getOption(this.suffix).orElse(""));
				Text teamRepresentation = player.getTeamRepresentation();
				
				for(EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
					other.sendNameTag(IDENTIFIER, teamRepresentation, prefix, suffix);
				}
			}
		}
	}

	public void stop() {
		if(this.enable) {
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				player.clearNameTag(IDENTIFIER);
			}
		}
	}
	
	public void addPlayer(EPlayer player) {
		if(this.enable) {
			Text prefix = EChat.of(player.getOption(this.prefix).orElse(""));
			Text suffix = EChat.of(player.getOption(this.suffix).orElse(""));
			Text teamRepresentation = player.getTeamRepresentation();
			
			for(EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
				if(!player.equals(other)) {
					other.sendNameTag(IDENTIFIER, teamRepresentation, prefix, suffix);
				}
				player.sendNameTag(IDENTIFIER, other.getTeamRepresentation(), EChat.of(other.getOption(this.prefix).orElse("")), EChat.of(other.getOption(this.suffix).orElse("")));
			}
		}
	}
	
	public void removePlayer(EPlayer player) {
		if(this.enable) {
			Text teamRepresentation = player.getTeamRepresentation();
			for(EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
				other.removeNameTag(IDENTIFIER, teamRepresentation);
			}
			
			player.clearNameTag(IDENTIFIER);
		}
	}

	public void updatePermission(EPlayer player) {
		this.removePlayer(player);
		this.addPlayer(player);
	}
}
