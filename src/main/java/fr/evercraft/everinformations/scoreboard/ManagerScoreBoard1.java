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
package fr.evercraft.everinformations.scoreboard;

import java.util.Optional;

import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.Visibilities;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;

public class ManagerScoreBoard1 {
	

	private final EverInformations plugin;
	
	private final Objective objective_name;
	private final Objective objective_list;

	public ManagerScoreBoard1(final EverInformations plugin) {
		this.plugin = plugin;
		
		this.objective_name = Objective.builder()
				.name("everinfo.name")
				.displayName(Text.of(TextColors.RED, "靹"))
				.criterion(Criteria.HEALTH)
				.build();
		this.objective_list = Objective.builder()
				.name("everinfo.list")
				.displayName(Text.of(TextColors.RED))
				.objectiveDisplayMode(ObjectiveDisplayModes.HEARTS)
				.criterion(Criteria.HEALTH)
				.build();
	}
	
	public boolean addPlayer(EPlayer player) {
		Text prefix = EChat.of(player.getPrefix().orElse(""));
		Text suffix = EChat.of(player.getSuffix().orElse(""));
		Text representation = player.getTeamRepresentation();
		
		player.setScoreboard(Scoreboard.builder().build());
		player.sendMessage("size = " + this.plugin.getEServer().getOnlineEPlayers().size());
		for(EPlayer others : this.plugin.getEServer().getOnlineEPlayers()) {
			if(!others.equals(player)) {
				Team team = Team.builder()
						.prefix(prefix)
						.suffix(suffix)
						.name(player.getName())
						.nameTagVisibility(Visibilities.ALL)
						.displayName(Text.of(TextColors.WHITE, player.getName()))
						.color(TextColors.WHITE)
						.build();
				team.addMember(representation);
				others.getScoreboard().registerTeam(team);
				player.sendMessage("add others = " + team.getName());
			}
			Team team = Team.builder()
					.prefix(EChat.of(others.getPrefix().orElse("")))
					.suffix(EChat.of(others.getSuffix().orElse("")))
					.name(others.getName())
					.nameTagVisibility(Visibilities.ALL)
					.displayName(Text.of(TextColors.WHITE, others.getName()))
					.color(TextColors.WHITE)
					.build();
			team.addMember(others.getTeamRepresentation());
			player.getScoreboard().registerTeam(team);
			player.sendMessage("others = " + team.getName());
		}
		
		Objective sidebar = Objective.builder()
				.name("everinfo.side")
				.displayName(Text.of(TextColors.RED, "Informations"))
				.criterion(Criteria.DUMMY)
				.build();
		
		sidebar.getOrCreateScore(EChat.of("&6Emeraude")).setScore(player.getBalance().intValue());
		sidebar.getOrCreateScore(EChat.of("&61234567")).setScore(100);
		
		player.getScoreboard().addObjective(sidebar);
		player.getScoreboard().updateDisplaySlot(sidebar, DisplaySlots.SIDEBAR);
		
		player.getScoreboard().addObjective(this.objective_name);
		player.getScoreboard().updateDisplaySlot(this.objective_name, DisplaySlots.BELOW_NAME);
		
		player.getScoreboard().addObjective(this.objective_list);
		player.getScoreboard().updateDisplaySlot(this.objective_list, DisplaySlots.LIST);
		
		return true;
	}
	
	public boolean removePlayer(EPlayer player) {
		Text representation = player.getTeamRepresentation();
		
		player.sendMessage("size = " + this.plugin.getEServer().getOnlineEPlayers().size());
		
		for(EPlayer others : this.plugin.getEServer().getOnlineEPlayers()) {
			Optional<Team> team = others.getScoreboard().getMemberTeam(representation);
			if(team.isPresent()) {
				team.get().unregister();
				player.sendMessage("remove = " + team.get().getName());
			}
		}
		return true;
	}
	
	public boolean updatePlayer(EPlayer player) {
		Text prefix = EChat.of(player.getPrefix().orElse(""));
		Text suffix = EChat.of(player.getSuffix().orElse(""));
		Text representation = player.getTeamRepresentation();
		
		for(EPlayer others : this.plugin.getEServer().getOnlineEPlayers()) {
			Optional<Team> team = others.getScoreboard().getMemberTeam(representation);
			if(team.isPresent()) {
				team.get().setPrefix(prefix);
				team.get().setSuffix(suffix);
			}
		}
		return true;
	}
	
	public boolean healthPlayer(EPlayer player) {
		Text prefix = EChat.of(player.getPrefix().orElse(""));
		Text suffix = EChat.of(player.getSuffix().orElse(""));
		Text representation = player.getTeamRepresentation();
		
		for(EPlayer others : this.plugin.getEServer().getOnlineEPlayers()) {
			Optional<Team> team = others.getScoreboard().getMemberTeam(representation);
			if(team.isPresent()) {
				team.get().setPrefix(prefix);
				team.get().setSuffix(suffix);
			}
		}
		return true;
	}
}
