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
package fr.evercraft.everinformations.scoreboard.score;

import java.util.Optional;

import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.critieria.Criterion;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayModes;

import fr.evercraft.everinformations.scoreboard.score.Score.TypeScore;

public enum ObjectiveType {
	HEALTH(Criteria.HEALTH, ObjectiveDisplayModes.HEARTS),
	HEALTH_INTEGER(Criteria.HEALTH),
	PLAYER_KILLS(Criteria.PLAYER_KILLS),
	TOTAL_KILLS(Criteria.TOTAL_KILLS),
	ONLINE_PLAYERS(TypeScore.ONLINE_PLAYERS),
	BALANCE(TypeScore.BALANCE),
	PING(TypeScore.PING),
	FOOD(TypeScore.FOOD),
	LEVEL(TypeScore.LEVEL),
	XP(TypeScore.XP),
	DEATHS(TypeScore.DEATHS),
	KILLS(TypeScore.KILLS),
	RATIO(TypeScore.RATIO),
	DEATHS_MONTHLY(TypeScore.DEATHS_MONTHLY),
	KILSL_MONTHLY(TypeScore.KILLS_MONTHLY),
	RATIO_MONTHLY(TypeScore.RATIO_MONTHLY);
	
	private final Criterion criterion;
	private final ObjectiveDisplayMode display;
	private final Optional<TypeScore> score;
	
	private ObjectiveType(final Criterion criterion) {
		this(criterion, ObjectiveDisplayModes.INTEGER, null);
	}
	
	private ObjectiveType(final Criterion criterion, final ObjectiveDisplayMode display) {
		this(criterion, display, null);
	}
	
	private ObjectiveType(final TypeScore score) {
		this(Criteria.DUMMY, ObjectiveDisplayModes.INTEGER, score);
	}
	
	private ObjectiveType(final Criterion criterion, final ObjectiveDisplayMode display, final TypeScore score) {
		this.criterion = criterion;
		this.display = display;
		this.score = Optional.ofNullable(score);
	}
	
	public Criterion getCriterion() {
		return this.criterion;
	}
	
	public ObjectiveDisplayMode getObjectiveDisplayMode() {
		return this.display;
	}
	
	public Optional<TypeScore> getTypeScore() {
		return this.score;
	}
	
	public Optional<TypeScore> getVariables() {
		return this.score;
	}
	
	public boolean isUpdate() {
		if(this.score.isPresent()){
			return this.score.get().isUpdate();
		}
		return true;
	}
}
