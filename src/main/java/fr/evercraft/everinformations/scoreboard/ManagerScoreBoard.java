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
package fr.evercraft.everinformations.scoreboard;

import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.config.ConfigBelowName;
import fr.evercraft.everinformations.scoreboard.config.ConfigList;
import fr.evercraft.everinformations.scoreboard.config.ConfigSidebar;
import fr.evercraft.everinformations.scoreboard.objective.BelowNameObjective;
import fr.evercraft.everinformations.scoreboard.objective.ListObjective;
import fr.evercraft.everinformations.scoreboard.objective.SidebarObjective;

public class ManagerScoreBoard {
	private final EverInformations plugin;
	
	private final ScoreBoard<BelowNameObjective> below_name;
	private final ScoreBoard<ListObjective> list;
	private final ScoreBoard<SidebarObjective> title;
	
	public ManagerScoreBoard(final EverInformations plugin) {
		this.plugin = plugin;
		
		this.below_name = new ScoreBoard<BelowNameObjective>(this.plugin, new ConfigBelowName(this.plugin), DisplaySlots.BELOW_NAME);
		this.list = new ScoreBoard<ListObjective>(this.plugin, new ConfigList(this.plugin), DisplaySlots.LIST);
		this.title = new ScoreBoard<SidebarObjective>(this.plugin, new ConfigSidebar(this.plugin), DisplaySlots.SIDEBAR);
	}
	
	public void reload() {
		this.below_name.reload();
		this.list.reload();
		this.title.reload();
	}
	
	public void start() {
		this.below_name.start();
		this.list.start();
		this.title.start();
	}

	public void stop() {
		this.below_name.stop();
		this.list.stop();
		this.title.stop();
	}

	public void addPlayer(EPlayer player) {
		this.below_name.addPlayer(player);
		this.list.addPlayer(player);
		this.title.addPlayer(player);
	}

	public void removePlayer(EPlayer player) {
		this.below_name.removePlayer(player);
		this.list.removePlayer(player);
		this.title.removePlayer(player);
	}
}
