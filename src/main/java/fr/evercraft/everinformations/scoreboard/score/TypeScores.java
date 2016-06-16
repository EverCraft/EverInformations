package fr.evercraft.everinformations.scoreboard.score;

import java.util.Optional;

import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.critieria.Criterion;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayModes;

import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.scoreboard.objective.EObjective;

public enum TypeScores {
	HEALTH(new ScoreHealth(), Criteria.HEALTH, ObjectiveDisplayModes.HEARTS),
	HEALTH_INTEGER(new ScoreHealth(), Criteria.HEALTH),
	ONLINE_PLAYERS(new ScoreOnlinePlayers()),
	BALANCE(new ScoreBalance()),
	PING(new ScorePing()),
	FOOD(new ScoreFood()),
	LEVEL(new ScoreLevel()),
	XP(new ScoreXp()),
	DEATHS(new ScoreDeath()),
	KILLS(new ScoreKill()),
	RATIO(new ScoreRatio()),
	DEATHS_MONTHLY(new ScoreDeathMonthly()),
	KILLS_MONTHLY(new ScoreKillMonthly()),
	RATIO_MONTHLY(new ScoreRatioMonthly());
	
	private final Score score;
	private final Optional<Criterion> criterion;
	private final ObjectiveDisplayMode display;
	
	private TypeScores(Score score) {
		this.score = score;
		this.criterion = Optional.empty();
		this.display = ObjectiveDisplayModes.INTEGER;
	}
	
	private TypeScores(Score score, Criterion criterion) {
		this.score = score;
		this.criterion = Optional.ofNullable(criterion);
		this.display = ObjectiveDisplayModes.INTEGER;
	}
	
	private TypeScores(Score score, Criterion criterion, ObjectiveDisplayMode display) {
		this.score = score;
		this.criterion = Optional.ofNullable(criterion);
		this.display = display;
	}
	
	public int getValue(EPlayer player) {
		return this.score.getValue(player);
	}
	
	public boolean isUpdate() {
		return this.score.isUpdate();
	}
	
	public void addListener(EPlugin plugin, EObjective objective) {
		this.score.addListener(plugin, objective);
	}
	
	public void removeListener(EPlugin plugin, EObjective objective) {
		this.score.removeListener(plugin, objective);
	}
	
	public Optional<Criterion> getCriterion() {
		return this.criterion;
	}
	
	public ObjectiveDisplayMode getObjectiveDisplayMode() {
		return this.display;
	}
}