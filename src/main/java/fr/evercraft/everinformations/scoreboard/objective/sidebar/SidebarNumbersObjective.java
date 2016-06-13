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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;

import com.google.common.collect.Sets;

import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.scoreboard.ScoreBoard;
import fr.evercraft.everinformations.scoreboard.objective.SidebarObjective;
import fr.evercraft.everinformations.scoreboard.objective.score.Score.TypeScore;

public class SidebarNumbersObjective extends SidebarObjective {
	
	private final ConcurrentMap<Integer, String> scores;
	private final ConcurrentMap<TypeScore, Set<Integer>> type_scores;

	public SidebarNumbersObjective(final EPlugin plugin, final double stay, final double update, final List<SidebarTitle> titles, final Map<Integer, String> scores) {
		super(plugin, stay, update, Type.NUMBERS, titles);
		
		this.scores = new ConcurrentHashMap<Integer, String>();
		this.type_scores = new ConcurrentHashMap<TypeScore, Set<Integer>>();
		this.scores.putAll(scores);
		
		for(TypeScore type : TypeScore.values()) {
			for(Entry<Integer, String> score : this.scores.entrySet()) {
				if(score.getValue().contains("<" + type.name() + ">")) {
					if(!this.type_scores.containsKey(type)) {
						this.type_scores.put(type, Sets.newConcurrentHashSet());
					}
					this.type_scores.get(type).add(score.getKey());
				}
			}
		}
		
		this.plugin.getLogger().warn("size : " + this.type_scores.size());
	}
	
	@Override
	public boolean add(int priority, EPlayer player) {
		Objective objective = Objective.builder()
							.name(ScoreBoard.SIDEBAR_IDENTIFIER)
							.displayName(this.getSidebarTitle().getTitle())
							.criterion(Criteria.DUMMY)
							.build();
		for(Entry<Integer, String> score : this.scores.entrySet()) {
			objective.getOrCreateScore(player.replaceVariable(score.getValue())).setScore(score.getKey());
		}
		return player.addObjective(priority, DisplaySlots.SIDEBAR, objective);
	}

	@Override
	public boolean remove(EPlayer player) {
		return player.removeObjective(DisplaySlots.SIDEBAR, ScoreBoard.SIDEBAR_IDENTIFIER);
	}

	@Override
	public boolean subStart() {
		for(TypeScore type : this.type_scores.keySet()) {
			type.addListener(this.plugin, this);
		}
		return true;
	}

	@Override
	public boolean subStop() {
		for(TypeScore type : this.type_scores.keySet()) {
			type.removeListener(this.plugin, this);
		}
		return true;
	}
	
	@Override
	public void update() {
		for(TypeScore score : this.type_scores.keySet()) {
			if(!score.isUpdate()) {
				this.update(score);
			}
		}
	}
	
	@Override
	public void update(TypeScore type) {
		Set<Integer> line = this.type_scores.get(type);
		for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			Optional<Objective> objective = player.getScoreboard().getObjective(ScoreBoard.SIDEBAR_IDENTIFIER);
			if(objective.isPresent()) {
				for(Entry<Text, Score> score : objective.get().getScores().entrySet()) {
					if(line.contains(score.getValue().getScore())) {
						Text text = player.replaceVariable(this.scores.get(score.getValue().getScore()));
						if(!score.getKey().equals(text)) {
							objective.get().getOrCreateScore(text).setScore(score.getValue().getScore());
							objective.get().removeScore(score.getKey());
						}
					}
				}
			}
		}
	}

	@Override
	public void update(UUID uuid, TypeScore type) {
		Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(uuid);
		if(player.isPresent()) {
			Optional<Objective> objective = player.get().getScoreboard().getObjective(ScoreBoard.SIDEBAR_IDENTIFIER);
			if(objective.isPresent()) {
				Set<Integer> line = this.type_scores.get(type);
				for(Entry<Text, Score> score : objective.get().getScores().entrySet()) {
					if(line.contains(score.getValue().getScore())) {
						Text text = player.get().replaceVariable(this.scores.get(score.getValue().getScore()));
						if(!score.getKey().equals(text)) {
							objective.get().getOrCreateScore(text).setScore(score.getValue().getScore());
							objective.get().removeScore(score.getKey());
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isUpdate() {
		boolean update = true;
		for(TypeScore score : this.type_scores.keySet()) {
			if(!score.isUpdate()) {
				update = false;
			}
		}
		return update;
	}
	
	@Override
	protected void updateTitle() {		
		SidebarTitle title = this.getSidebarTitle();
		this.plugin.getLogger().debug("SidebarTitle : View (title='" + title.getTitle().toPlain() + "';next='" + title.getNext() + "')");
		
		for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			Optional<Objective> objective = player.getScoreboard().getObjective(ScoreBoard.SIDEBAR_IDENTIFIER);
			if(objective.isPresent()) {
				objective.get().setDisplayName(title.getTitle());
			}
		}
	}
}