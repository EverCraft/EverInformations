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
package fr.evercraft.everinformations.scoreboard.objective;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.scoreboard.TypeScores;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.ScoreBoard;

public class ListObjective extends EObjective {

	private final Objective objective;
	private final TypeScores type;
	
	public ListObjective(final EverInformations plugin, final double stay, final double update, TypeScores type) {
		super(plugin, stay, update);
		this.type = type;
		
		this.objective = Objective.builder()
				.name(ScoreBoard.LIST_IDENTIFIER)
				.displayName(Text.of())
				.criterion(type.getCriterion().orElse(Criteria.DUMMY))
				.objectiveDisplayMode(type.getObjectiveDisplayMode())
				.build();
	}

	@Override
	public boolean add(int priority, EPlayer player) {
		this.objective.getOrCreateScore(player.getTeamRepresentation()).setScore(this.type.getValue(player));
		return player.addObjective(priority, DisplaySlots.LIST, this.objective);
	}
	
	@Override
	public boolean remove(EPlayer player) {
		return player.removeObjective(DisplaySlots.LIST, ScoreBoard.LIST_IDENTIFIER);
	}

	@Override
	public boolean start() {
		this.type.addListener(this.plugin, this);
		return true;
	}

	@Override
	public boolean stop() {
		this.type.removeListener(this);
		return true;
	}
	
	@Override
	public void update() {
		this.update(this.type);
	}

	@Override
	public void update(TypeScores score) {
		for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			this.objective.getOrCreateScore(player.getTeamRepresentation()).setScore(score.getValue(player));
		}
	}

	@Override
	public void update(Player player_sponge, TypeScores score) {
		EPlayer player = this.plugin.getEServer().getEPlayer(player_sponge);
		this.objective.getOrCreateScore(player.getTeamRepresentation()).setScore(score.getValue(player));
	}

	@Override
	public boolean isUpdate() {
		return this.type.isUpdate();
	}

	@Override
	public String toString() {
		return "ListObjective [stay=" + stay + ", type=" + this.type.name()
				+ ", objective=" + objective.getName() + "]";
	}
	
	
}
