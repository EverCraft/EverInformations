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

import fr.evercraft.everapi.event.TabListEvent;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.PriorityService;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.tablist.config.ConfigTabList;

public class ManagerTabList {
	public final static String IDENTIFIER = "everinformations";
	
	private final EverInformations plugin;
	
	private final HeaderFooterTabList header_footer;
	private final DisplayNameTabList displayname;
	
	private int priority;
	 
	public ManagerTabList(final EverInformations plugin) {
		this.plugin = plugin;
		
		ConfigTabList config = new ConfigTabList(this.plugin);
		
		this.header_footer = new HeaderFooterTabList(this.plugin, config);
		this.displayname = new DisplayNameTabList(this.plugin, config);
		
		load();
	}

	public void load() {
		this.priority = PriorityService.DEFAULT;
		if(this.plugin.getEverAPI().getManagerService().getPriority().isPresent()) {
			this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get().getTabList(IDENTIFIER);
		}
	}
	
	public void reload() {
		this.load();
		
		this.displayname.reload();
		this.header_footer.reload();
	}

	public void start() {
		this.displayname.start();
		this.header_footer.start();
	}

	public void stop() {
		this.displayname.stop();
		this.header_footer.stop();
	}
	
	public void addPlayer(EPlayer player) {
		this.displayname.addPlayer(player);
		this.displayname.addOther(player);
		this.header_footer.addPlayer(player);
	}
	
	public void removePlayer(EPlayer player) {
		this.displayname.removePlayer(player);
		this.header_footer.removePlayer(player);
	}

	public void updatePlayer(EPlayer player) {
		this.displayname.addOther(player);
	}

	public void event(TabListEvent event) {
		if(!event.getIdentifier().equalsIgnoreCase(IDENTIFIER)) {
			this.displayname.addPlayer(event.getPlayer());
			this.header_footer.addPlayer(event.getPlayer());
		}
	}
	
	public int getPriority() {
		return this.priority;
	}

	public void eventTabList(EPlayer player, String before_identifier) {
		this.displayname.addPlayer(player);
		this.header_footer.addPlayer(player);
	}
}
