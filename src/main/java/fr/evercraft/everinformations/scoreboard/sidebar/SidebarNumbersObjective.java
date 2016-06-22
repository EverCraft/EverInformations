package fr.evercraft.everinformations.scoreboard.sidebar;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.scoreboard.TypeScores;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.ScoreBoard;
import fr.evercraft.everinformations.scoreboard.objective.SidebarObjective;

public class SidebarNumbersObjective extends SidebarObjective {
	
	private final ConcurrentMap<Text, TypeScores> scores;

	public SidebarNumbersObjective(final EverInformations plugin, final double stay, final double update, final List<SidebarTitle> titles, final Map<Text, TypeScores> scores) {
		super(plugin, stay, update, Type.NUMBERS, titles);
		
		this.scores = new ConcurrentHashMap<Text, TypeScores>();
		this.scores.putAll(scores);
	}
	
	@Override
	public boolean add(int priority, EPlayer player) {
		Objective objective = Objective.builder()
							.name(ScoreBoard.SIDEBAR_IDENTIFIER)
							.displayName(this.getSidebarTitle().getTitle())
							.criterion(Criteria.DUMMY)
							.build();
		for(Entry<Text, TypeScores> score : this.scores.entrySet()) {
			objective.getOrCreateScore(score.getKey()).setScore(score.getValue().getValue(player));
		}
		return player.addObjective(priority, DisplaySlots.SIDEBAR, objective);
	}

	@Override
	public boolean remove(EPlayer player) {
		return player.removeObjective(DisplaySlots.SIDEBAR, ScoreBoard.SIDEBAR_IDENTIFIER);
	}

	@Override
	public boolean subStart() {
		for(TypeScores type : new HashSet<TypeScores>(this.scores.values())) {
			type.addListener(this.plugin, this);
		}
		return true;
	}

	@Override
	public boolean subStop() {
		for(TypeScores type : new HashSet<TypeScores>(this.scores.values())) {
			type.removeListener(this.plugin, this);
		}
		return true;
	}
	
	@Override
	public void update() {
		for(Entry<Text, TypeScores> score : this.scores.entrySet()) {
			if(!score.getValue().isUpdate()) {
				for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
					Optional<Objective> objective = player.getScoreboard().getObjective(ScoreBoard.SIDEBAR_IDENTIFIER);
					if(objective.isPresent()) {
						objective.get().getOrCreateScore(score.getKey()).setScore(score.getValue().getValue(player));
					}
				}
			}
		}
	}
	
	@Override
	public void update(TypeScores type) {
		for(Entry<Text, TypeScores> score : this.scores.entrySet()) {
			if(score.getValue().equals(type)) {
				for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
					Optional<Objective> objective = player.getScoreboard().getObjective(ScoreBoard.SIDEBAR_IDENTIFIER);
					if(objective.isPresent()) {
						objective.get().getOrCreateScore(score.getKey()).setScore(score.getValue().getValue(player));
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
				for(Entry<Text, TypeScores> score : this.scores.entrySet()) {
					if(score.getValue().equals(type)) {
						objective.get().getOrCreateScore(score.getKey()).setScore(type.getValue(player.get()));
					}
				}
			}
		}
	}

	@Override
	public boolean isUpdate() {
		boolean update = true;
		for(Entry<Text, TypeScores> score : this.scores.entrySet()) {
			if(!score.getValue().isUpdate()) {
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
