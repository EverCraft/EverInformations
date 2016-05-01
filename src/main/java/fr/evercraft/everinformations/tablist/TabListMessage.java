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
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;

import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.priority.PriorityService;
import fr.evercraft.everinformations.scoreboard.objective.EObjective;
import fr.evercraft.everinformations.scoreboard.objective.score.Score.TypeScore;

public class TabListMessage extends EObjective {
	
	private final Set<TypeScore> scores;
	
	private final String header;
	private final String footer;
	
	public TabListMessage(EPlugin plugin, double stay, double update, String header, String footer) {
		super(plugin, stay, update);

		this.header = header;
		this.footer = footer;
		
		this.scores = Sets.newConcurrentHashSet();
		this.update = true;
		
		for(TypeScore score : TypeScore.values()) {
			if(this.header.contains("<" + score.name() + ">") || this.footer.contains("<" + score.name() + ">")) {
				this.scores.add(score);
				if(!score.isUpdate()) {
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
		for(TypeScore type : this.scores) {
			type.addListener(this.plugin, this);
		}
		return false;
	}

	@Override
	public boolean stop() {		
		for(TypeScore type : this.scores) {
			type.removeListener(this.plugin, this);
		}
		
		
		return true;
	}
	
	/*
	 * Player
	 */
	
	@Override
	public boolean add(int priority, EPlayer player) {
		return this.add(player);
	}
	
	public boolean add(EPlayer player) {
		if(player.sendTabList(ManagerTabList.IDENTIFIER)) {
			if(this.header.isEmpty()) {
				player.getTabList().setHeader(null);
			} else {
				player.getTabList().setHeader(player.replaceVariable(this.header));
			}
			
			if(this.footer.isEmpty()) {
				player.getTabList().setFooter(null);
			} else {
				player.getTabList().setFooter(player.replaceVariable(this.footer));
			}
		}
		return true;
	}
	
	@Override
	public boolean remove(EPlayer player) {
		player.getTabList().setHeaderAndFooter(null, null);
		return true;
	}
	
	/*
	 * Update 
	 */
	
	@Override
	public void update() {
		for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			this.add(PriorityService.DEFAULT, player);
		}
	}

	@Override
	public void update(TypeScore score) {
		this.update();
	}

	@Override
	public void update(UUID uuid, TypeScore score) {
		Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(uuid);
		if(player.isPresent()) {
			this.add(PriorityService.DEFAULT, player.get());
		}
	}

	@Override
	public String toString() {
		return "TabListMessage [scores=" + scores + ", header=" + header
				+ ", footer=" + footer + ", stay=" + stay + ", update_time="
				+ update_time + ", update=" + update + "]";
	}
	
}
