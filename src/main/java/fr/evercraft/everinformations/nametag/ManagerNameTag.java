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
package fr.evercraft.everinformations.nametag;

import java.util.concurrent.TimeUnit;

import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.message.format.EFormatString;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.InformationService.Priorities;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.nametag.config.ConfigNameTag;

public class ManagerNameTag {
	
	private final EverInformations plugin;
	
	private final ConfigNameTag config;

	private boolean enable;
	
	private long update;
	private Task task;
	
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
		this.update = this.config.getUpdate();
		
		this.start();
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
					this.clearAll();
					this.sendAll();
				})
				.delay(this.update, TimeUnit.SECONDS)
				.name("NameTag")
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
			Text prefix = EFormatString.of(player.getOption(this.prefix).orElse("")).toText(player.getReplaces());
			Text suffix = EFormatString.of(player.getOption(this.suffix).orElse("")).toText(player.getReplaces());
			Text teamRepresentation = player.getTeamRepresentation();
			
			for (EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
				other.sendNameTag(Priorities.NAMETAG, teamRepresentation, prefix, suffix);
			}
		}
	}
	
	public void clearAll() {
		for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			player.clearNameTag(Priorities.NAMETAG);
		}
	}
	
	public void sendPlayer(EPlayer player) {
		if (this.enable) {
			for (EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
				System.out.println(player.getOption(this.prefix).orElse(""));
				player.sendNameTag(Priorities.NAMETAG, 
						other.getTeamRepresentation(), 
						EFormatString.of(other.getOption(this.prefix).orElse("")).toText(player.getReplaces()), 
						EFormatString.of(other.getOption(this.suffix).orElse("")).toText(player.getReplaces()));
			}
		}
	}
	
	public void clearPlayer(EPlayer player) {
		if (this.enable) {
			player.clearNameTag(Priorities.NAMETAG);
		}
	}
	
	public void sendAll(EPlayer player) {
		if (this.enable) {
			Text prefix = EFormatString.of(player.getOption(this.prefix).orElse("")).toText(player.getReplaces());
			Text suffix = EFormatString.of(player.getOption(this.suffix).orElse("")).toText(player.getReplaces());
			Text teamRepresentation = player.getTeamRepresentation();
			
			for (EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
				if (!player.getUniqueId().equals(other.getUniqueId())) {
					other.sendNameTag(Priorities.NAMETAG, teamRepresentation, prefix, suffix);
				}
				
				player.sendNameTag(Priorities.NAMETAG, 
						other.getTeamRepresentation(), 
						EFormatString.of(other.getOption(this.prefix).orElse("")).toText(player.getReplaces()), 
						EFormatString.of(other.getOption(this.suffix).orElse("")).toText(player.getReplaces()));
			}
		}
	}
	
	public void clearAll(EPlayer player) {
		if (this.enable) {
			Text teamRepresentation = player.getTeamRepresentation();
			for (EPlayer other : this.plugin.getEServer().getOnlineEPlayers()) {
				other.removeNameTag(Priorities.NAMETAG, teamRepresentation);
			}
			
			player.clearNameTag(Priorities.NAMETAG);
		}
	}

	public void updatePermission(EPlayer player) {
		this.clearAll(player);
		this.sendAll(player);
	}

	public void eventNameTag(EPlayer player) {
		this.sendPlayer(player);
	}
}
