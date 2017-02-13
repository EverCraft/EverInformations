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

import java.util.Set;
import org.spongepowered.api.entity.living.player.Player;

import com.google.common.collect.Sets;

import fr.evercraft.everapi.message.format.EFormatString;
import fr.evercraft.everapi.scoreboard.TypeScores;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.PriorityService;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.objective.EObjective;

public class TabListMessage extends EObjective {
	
	private final Set<TypeScores> scores;
	
	private final String header;
	private final String footer;
	
	public TabListMessage(EverInformations plugin, double stay, double update, String header, String footer) {
		super(plugin, stay, update);

		this.header = header;
		this.footer = footer;
		
		this.scores = Sets.newConcurrentHashSet();
		this.update = true;
		
		for (TypeScores score : TypeScores.values()) {
			if (this.header.contains("<" + score.name() + ">") || this.footer.contains("<" + score.name() + ">")) {
				this.scores.add(score);
				if (!score.isUpdate()) {
					this.update = false;
				}
			}
		}
	}
	
	/*
	 * Activation
	 */

	@Override
	public boolean start() {		
		for (TypeScores score : this.scores) {
			score.addListener(this.plugin, this);
		}
		return false;
	}

	@Override
	public boolean stop() {		
		for (TypeScores score : this.scores) {
			score.removeListener(this.plugin, this);
		}
		return true;
	}
	
	/*
	 * Player
	 */
	
	@Override
	public boolean add(int priority, EPlayer player) {
		if (player.sendTabList(ManagerTabList.IDENTIFIER, this.plugin.getTabList().getPriority())) {
			player.getTabList().setHeaderAndFooter(
					EFormatString.of(this.header).toText(player.getReplacesPlayer()),
					EFormatString.of(this.footer).toText(player.getReplacesPlayer()));
		}
		return true;
	}
	
	@Override
	public boolean remove(EPlayer player) {
		player.removeTabList(ManagerTabList.IDENTIFIER);
		return true;
	}
	
	/*
	 * Update 
	 */
	
	@Override
	public void update() {
		for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			if (player.sendTabList(ManagerTabList.IDENTIFIER, this.plugin.getTabList().getPriority())) {
				this.add(PriorityService.DEFAULT, player);
			}
		}
	}

	@Override
	public void update(TypeScores score) {
		this.update();
	}

	@Override
	public void update(Player player_sponge, TypeScores score) {
		EPlayer player = this.plugin.getEServer().getEPlayer(player_sponge);
		if (player.sendTabList(ManagerTabList.IDENTIFIER, this.plugin.getTabList().getPriority())) {
			this.add(PriorityService.DEFAULT, player);
		}
	}

	@Override
	public String toString() {
		return "TabListMessage [scores=" + scores + ", header=" + header
				+ ", footer=" + footer + ", stay=" + stay + ", update_time="
				+ update_time + ", update=" + update + "]";
	}
	
}
