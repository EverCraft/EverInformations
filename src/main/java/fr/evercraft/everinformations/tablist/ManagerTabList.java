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

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.tablist.config.ConfigTabList;

public class ManagerTabList {
	public final static String IDENTIFIER = "everinformations";
	
	private final HeaderFooterTabList header_footer;
	private final DisplayNameTabList displayname;
	 
	public ManagerTabList(final EverInformations plugin) {		
		ConfigTabList config = new ConfigTabList(plugin);
		
		this.header_footer = new HeaderFooterTabList(plugin, config);
		this.displayname = new DisplayNameTabList(plugin, config);
		
		reload();
	}

	public void reload(){		
		this.header_footer.reload();
		this.displayname.reload();
	}

	public void start() {
		this.header_footer.start();
		this.displayname.start();
	}

	public void stop() {
		this.header_footer.stop();
		this.displayname.stop();
	}
	
	public void addPlayer(EPlayer player) {
		this.header_footer.addPlayer(player);
		this.displayname.addPlayer(player);
	}
	
	public void removePlayer(EPlayer player) {
		this.header_footer.removePlayer(player);
		this.displayname.removePlayer(player);
	}

	public void changePermission(EPlayer player) {
		this.header_footer.addPlayer(player);
	}
}
