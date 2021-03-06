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
package fr.evercraft.everinformations.scoreboard.sidebar;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.registers.ScoreType;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.InformationService.Priorities;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.objective.SidebarObjective;

public class SidebarNumbersObjective extends SidebarObjective {
	
	private final ConcurrentMap<Text, ScoreType> scores;

	public SidebarNumbersObjective(final EverInformations plugin, final double stay, final double update, final List<SidebarTitle> titles, final Map<Text, ScoreType> scores) {
		super(plugin, stay, update, Type.NUMBERS, titles);
		
		this.scores = new ConcurrentHashMap<Text, ScoreType>();
		this.scores.putAll(scores);
	}
	
	@Override
	public boolean add(int priority, EPlayer player) {
		Objective objective = Objective.builder()
							.name(Priorities.SCOREBOARD_SIDEBAR)
							.displayName(EChat.fixLength(this.getSidebarTitle().getTitle().toText(player.getReplaces()), 32))
							.criterion(Criteria.DUMMY)
							.build();
		for (Entry<Text, ScoreType> score : this.scores.entrySet()) {
			objective.getOrCreateScore(score.getKey()).setScore(score.getValue().getValue(player));
		}
		return player.addObjective(priority, DisplaySlots.SIDEBAR, objective);
	}

	@Override
	public boolean remove(EPlayer player) {
		return player.removeObjective(DisplaySlots.SIDEBAR, Priorities.SCOREBOARD_SIDEBAR);
	}

	@Override
	public boolean subStart() {
		for (ScoreType type : new HashSet<ScoreType>(this.scores.values())) {
			type.addListener(this);
		}
		return true;
	}

	@Override
	public boolean subStop() {
		for (ScoreType type : new HashSet<ScoreType>(this.scores.values())) {
			type.removeListener(this);
		}
		return true;
	}
	
	@Override
	public void update() {
		for (Entry<Text, ScoreType> score : this.scores.entrySet()) {
			if (!score.getValue().isUpdate()) {
				for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
					Optional<Objective> objective = player.getScoreboard().getObjective(Priorities.SCOREBOARD_SIDEBAR);
					if (objective.isPresent()) {
						objective.get().getOrCreateScore(score.getKey()).setScore(score.getValue().getValue(player));
					}
				}
			}
		}
	}
	
	@Override
	public void update(ScoreType type) {
		for (Entry<Text, ScoreType> score : this.scores.entrySet()) {
			if (score.getValue().equals(type)) {
				for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
					Optional<Objective> objective = player.getScoreboard().getObjective(Priorities.SCOREBOARD_SIDEBAR);
					if (objective.isPresent()) {
						objective.get().getOrCreateScore(score.getKey()).setScore(score.getValue().getValue(player));
					}
				}
			}
		}
	}

	@Override
	public void update(Player player_sponge, ScoreType type) {
		EPlayer player = this.plugin.getEServer().getEPlayer(player_sponge);
		Optional<Objective> objective = player.getScoreboard().getObjective(Priorities.SCOREBOARD_SIDEBAR);
		if (objective.isPresent()) {
			for (Entry<Text, ScoreType> score : this.scores.entrySet()) {
				if (score.getValue().equals(type)) {
					objective.get().getOrCreateScore(score.getKey()).setScore(type.getValue(player));
				}
			}
		}
	}

	@Override
	public boolean isUpdate() {
		boolean update = true;
		for (Entry<Text, ScoreType> score : this.scores.entrySet()) {
			if (!score.getValue().isUpdate()) {
				update = false;
			}
		}
		return update;
	}
	
	@Override
	protected void updateTitle() {		
		SidebarTitle title = this.getSidebarTitle();
		this.plugin.getELogger().debug("SidebarTitle : View (title='" + title.getTitle().toString() + "';next='" + title.getNext() + "')");
		
		for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			Optional<Objective> objective = player.getScoreboard().getObjective(Priorities.SCOREBOARD_SIDEBAR);
			if (objective.isPresent()) {
				objective.get().setDisplayName(EChat.fixLength(title.getTitle().toText(player.getReplaces()), 32));
			}
		}
	}
}
