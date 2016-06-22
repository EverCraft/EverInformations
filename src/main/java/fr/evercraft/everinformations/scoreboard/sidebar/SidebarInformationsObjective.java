package fr.evercraft.everinformations.scoreboard.sidebar;

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

import fr.evercraft.everapi.scoreboard.TypeScores;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.ScoreBoard;
import fr.evercraft.everinformations.scoreboard.objective.SidebarObjective;

public class SidebarInformationsObjective extends SidebarObjective {
	
	private final ConcurrentMap<Integer, String> scores;
	private final ConcurrentMap<TypeScores, Set<Integer>> type_scores;

	public SidebarInformationsObjective(final EverInformations plugin, final double stay, final double update, final List<SidebarTitle> titles, final Map<Integer, String> scores) {
		super(plugin, stay, update, Type.INFORMATIONS, titles);
		
		this.scores = new ConcurrentHashMap<Integer, String>();
		this.type_scores = new ConcurrentHashMap<TypeScores, Set<Integer>>();
		this.scores.putAll(scores);
		
		for(TypeScores type : TypeScores.values()) {
			for(Entry<Integer, String> score : this.scores.entrySet()) {
				if(score.getValue().contains("<" + type.name() + ">")) {
					if(!this.type_scores.containsKey(type)) {
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
		for(TypeScores type : this.type_scores.keySet()) {
			type.addListener(this.plugin, this);
		}
		return true;
	}

	@Override
	public boolean subStop() {
		for(TypeScores type : this.type_scores.keySet()) {
			type.removeListener(this.plugin, this);
		}
		return true;
	}
	
	@Override
	public void update() {
		for(TypeScores score : this.type_scores.keySet()) {
			if(!score.isUpdate()) {
				this.update(score);
			}
		}
	}
	
	@Override
	public void update(TypeScores type) {
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
	public void update(UUID uuid, TypeScores type) {
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
		for(TypeScores score : this.type_scores.keySet()) {
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
