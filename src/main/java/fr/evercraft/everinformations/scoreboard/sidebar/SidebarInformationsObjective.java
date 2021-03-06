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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;

import com.google.common.collect.Sets;

import fr.evercraft.everapi.message.format.EFormatString;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.registers.ScoreType;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.InformationService.Priorities;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.objective.SidebarObjective;

public class SidebarInformationsObjective extends SidebarObjective {
	
	private final ConcurrentMap<Integer, String> scores;
	private final ConcurrentMap<ScoreType, Set<Integer>> type_scores;

	public SidebarInformationsObjective(final EverInformations plugin, final double stay, final double update, final List<SidebarTitle> titles, final Map<Integer, String> scores) {
		super(plugin, stay, update, Type.INFORMATIONS, titles);
		
		this.scores = new ConcurrentHashMap<Integer, String>();
		this.type_scores = new ConcurrentHashMap<ScoreType, Set<Integer>>();
		this.scores.putAll(scores);
		
		for (ScoreType type : this.plugin.getGame().getRegistry().getAllOf(ScoreType.class)) {
			for (Entry<Integer, String> score : this.scores.entrySet()) {
				if (score.getValue().contains("{" + type.getName() + "}")) {
					if (!this.type_scores.containsKey(type)) {
						this.type_scores.put(type, Sets.newConcurrentHashSet());
					}
					this.type_scores.get(type).add(score.getKey());
				}
			}
		}
	}
	
	@Override
	public boolean add(int priority, EPlayer player) {
		Objective objective = Objective.builder()
							.name(Priorities.SCOREBOARD_SIDEBAR)
							.displayName(EChat.fixLength(this.getSidebarTitle().getTitle().toText(player.getReplaces()), 32))
							.criterion(Criteria.DUMMY)
							.build();
		for (Entry<Integer, String> score : this.scores.entrySet()) {
			objective.getOrCreateScore(EFormatString.of(score.getValue()).toText(player.getReplaces())).setScore(score.getKey());
		}
		return player.addObjective(priority, DisplaySlots.SIDEBAR, objective);
	}

	@Override
	public boolean remove(EPlayer player) {
		return player.removeObjective(DisplaySlots.SIDEBAR, Priorities.SCOREBOARD_SIDEBAR);
	}

	@Override
	public boolean subStart() {
		for (ScoreType type : this.type_scores.keySet()) {
			type.addListener(this);
		}
		return true;
	}

	@Override
	public boolean subStop() {
		for (ScoreType type : this.type_scores.keySet()) {
			type.removeListener(this);
		}
		return true;
	}
	
	@Override
	public void update() {
		for (ScoreType score : this.type_scores.keySet()) {
			if (!score.isUpdate()) {
				this.update(score);
			}
		}
	}
	
	@Override
	public void update(ScoreType type) {
		Set<Integer> line = this.type_scores.get(type);
		for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			Optional<Objective> objective = player.getScoreboard().getObjective(Priorities.SCOREBOARD_SIDEBAR);
			if (objective.isPresent()) {
				for (Entry<Text, Score> score : objective.get().getScores().entrySet()) {
					if (line.contains(score.getValue().getScore())) {
						Text text = EFormatString.of(this.scores.get(score.getValue().getScore())).toText(player.getReplaces());
						if (!score.getKey().equals(text)) {
							objective.get().getOrCreateScore(text).setScore(score.getValue().getScore());
							objective.get().removeScore(score.getKey());
						}
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
			Set<Integer> line = this.type_scores.get(type);
			for (Entry<Text, Score> score : objective.get().getScores().entrySet()) {
				if (line.contains(score.getValue().getScore())) {
					Text text = EFormatString.of(this.scores.get(score.getValue().getScore())).toText(player.getReplaces());
					if (!score.getKey().equals(text)) {
						objective.get().getOrCreateScore(text).setScore(score.getValue().getScore());
						objective.get().removeScore(score.getKey());
					}
				}
			}
		}
	}

	@Override
	public boolean isUpdate() {
		boolean update = true;
		for (ScoreType score : this.type_scores.keySet()) {
			if (!score.isUpdate()) {
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
