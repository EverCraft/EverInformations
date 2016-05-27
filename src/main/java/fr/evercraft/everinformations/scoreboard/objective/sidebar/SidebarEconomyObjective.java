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
package fr.evercraft.everinformations.scoreboard.objective.sidebar;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;

import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EIMessage.EIMessages;
import fr.evercraft.everinformations.scoreboard.ScoreBoard;
import fr.evercraft.everinformations.scoreboard.objective.SidebarObjective;
import fr.evercraft.everinformations.scoreboard.objective.score.Score.TypeScore;

public class SidebarEconomyObjective extends SidebarObjective {
	private final static int TOP_COUNT = 16;

	private final EPlugin plugin;
	private Objective objective;
	private final String message;
	
	
	public SidebarEconomyObjective(final EPlugin plugin, final double stay, final double update, final List<SidebarTitle> titles, final String message) {
		super(plugin, stay,  update, Type.ECONOMY, titles);
		
		this.plugin = plugin;
		this.message = message;
	}
	
	@Override
	public boolean add(int priority, EPlayer player) {
		return player.addObjective(priority, DisplaySlots.SIDEBAR, this.objective);
	}
	
	@Override
	public boolean remove(EPlayer player) {
		return player.removeObjective(DisplaySlots.LIST, ScoreBoard.SIDEBAR_IDENTIFIER);
	}
	
	@Override
	public boolean start() {
		this.update();
		return true;
	}

	@Override
	public boolean stop() {
		return false;
	}

	@Override
	public void update() {
		Objective objective = Objective.builder()
				.name(ScoreBoard.SIDEBAR_IDENTIFIER)
				.displayName(this.titles.get(0).getTitle())
				.criterion(Criteria.DUMMY)
				.build();
		
		if(this.plugin.getEverAPI().getManagerService().getTopEconomy().isPresent()) {
			for(Entry<UUID, BigDecimal> player : this.plugin.getEverAPI().getManagerService().getTopEconomy().get().topUniqueAccount(TOP_COUNT).entrySet()) {
				Optional<User> user = this.plugin.getEServer().getUser(player.getKey());
				// Si le User existe bien
				if(user.isPresent()){
					this.plugin.getLogger().warn("update : " + user.get().getName());
					objective.getOrCreateScore(EChat.of(message.replaceAll("<player>",user.get().getName()))).setScore(player.getValue().intValue());
				} else {
					this.plugin.getLogger().warn("no name : " + player.getKey());
				}
			}
		} else {
			this.plugin.getLogger().warn("no EverEconomy");
		}
		
		if(objective.getScores().isEmpty()) {
			objective.getOrCreateScore(EIMessages.SCOREBOARD_EMPTY.getText()).setScore(0);
		}
		this.objective = objective;
	}
	
	@Override
	public void update(TypeScore type) {
		this.update();
	}

	@Override
	public void update(UUID uniqueId, TypeScore type) {
		this.update();
	}

	@Override
	public boolean isUpdate() {
		return true;
	}
}
