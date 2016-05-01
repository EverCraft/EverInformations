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
import java.util.UUID;

import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.priority.PriorityService;
import fr.evercraft.everinformations.scoreboard.objective.EObjective;
import fr.evercraft.everinformations.scoreboard.objective.score.Score.TypeScore;

public class TabListMessage  extends EObjective {
	
	private final String header;
	private final String footer;
	
	public TabListMessage(EPlugin plugin, double stay, double update, String header, String footer) {
		super(plugin, stay, update);

		this.header = header;
		this.footer = footer;
	}
	
	/**
	 * En Millisecondes
	 * @return
	 */
	public long getNext() {
		return (long) (this.stay * 1000);
	}
	
	/**
	 * En Millisecondes
	 * @return
	 */
	public long getUpdate() {
		return (long) (this.update * 1000);
	}

	public boolean add(int priority, EPlayer player) {
		if(!this.header.isEmpty()) {
			player.getTabList().setHeader(player.replaceVariable(this.header));
		}
		if(!this.footer.isEmpty()) {
			player.getTabList().setFooter(player.replaceVariable(this.footer));
		}
		return true;
	}
	
	public boolean remove(EPlayer player) {
		player.getTabList().setHeaderAndFooter(null, null);
		return true;
	}

	public boolean start() {
		for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			this.add(PriorityService.DEFAULT, player);
		}
		return false;
	}

	public boolean stop() {
		for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			this.remove(player);
		}
		return true;
	}
	
	public void update() {
		start();
	}

	public void update(TypeScore score) {
		start();
	}

	public void update(UUID uuid, TypeScore score) {
		Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(uuid);
		if(player.isPresent()) {
			this.add(PriorityService.DEFAULT, player.get());
		}
	}

	public boolean isUpdate() {
		return false;
	}

	@Override
	public String toString() {
		return "TabListMessage [stay=" + stay + ", update=" + update 
				+ ", header=" + header + ", footer=" + footer + "]";
	}
	
}
