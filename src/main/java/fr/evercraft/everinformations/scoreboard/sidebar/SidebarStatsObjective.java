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
package fr.evercraft.everinformations.scoreboard.sidebar;

import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
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
import fr.evercraft.everinformations.scoreboard.score.TypeScores;

public class SidebarStatsObjective extends SidebarObjective {
	private final static int TOP_COUNT = 16;
	
	public static enum TypeTop {
		DEATHS,
		KILLS,
		RATIO;
    }
	
	public static enum TypeTimes {
		ALL,
		MONTH,
		WEEK,
		DAY;
    }

	private final EPlugin plugin;
	private Objective objective;
	private final String message;
	
	private final TypeTop score_type;
	private final TypeTimes time_type;
	
	
	public SidebarStatsObjective(final EPlugin plugin, final double stay, final List<SidebarTitle> titles, final String message, TypeTop top_type, TypeTimes time_type) {
		super(plugin, stay,  0.0, Type.STATS, titles);
		
		this.plugin = plugin;
		this.message = message;
		
		this.score_type = top_type;
		this.time_type = time_type;
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
	public boolean subStart() {
		this.update();
		return true;
	}

	@Override
	public boolean subStop() {
		return false;
	}

	@Override
	public void update() {
		Objective objective = Objective.builder()
				.name(ScoreBoard.SIDEBAR_IDENTIFIER)
				.displayName(this.getSidebarTitle().getTitle())
				.criterion(Criteria.DUMMY)
				.build();
		
		if(this.plugin.getEverAPI().getManagerService().getTopEconomy().isPresent()) {
			for(Entry<UUID, Double> player : this.getTop().entrySet()) {
				Optional<User> user = this.plugin.getEServer().getUser(player.getKey());
				// Si le User existe bien
				if(user.isPresent()) {
					objective.getOrCreateScore(EChat.of(this.message.replaceAll("<player>",user.get().getName()))).setScore(player.getValue().intValue());
				}
			}
		} else {
			this.plugin.getLogger().warn("No EverStats");
		}
		
		if(objective.getScores().isEmpty()) {
			objective.getOrCreateScore(EIMessages.SCOREBOARD_EMPTY.getText()).setScore(0);
		}
		this.objective = objective;
	}
	
	private LinkedHashMap<UUID, Double> getTop() {
		if(this.score_type == TypeTop.DEATHS) {
			return this.plugin.getEverAPI().getManagerService().getStats().get().getTopDeaths(TOP_COUNT, this.getTime());
		} else if(this.score_type == TypeTop.KILLS) {
			return this.plugin.getEverAPI().getManagerService().getStats().get().getTopKills(TOP_COUNT, this.getTime());
		} else if(this.score_type == TypeTop.RATIO) {
			return this.plugin.getEverAPI().getManagerService().getStats().get().getTopRatio(TOP_COUNT, this.getTime());
		}
		return new LinkedHashMap<UUID, Double>();
	}
	
	private Long getTime() {
		if(this.time_type == TypeTimes.ALL) {
			return (long) 0;
		} else if(this.time_type == TypeTimes.MONTH) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(GregorianCalendar.MONTH, 1);
			calendar.set(GregorianCalendar.HOUR, 0);
			calendar.set(GregorianCalendar.MINUTE, 0);
			calendar.set(GregorianCalendar.SECOND, 0);
			return calendar.getTimeInMillis();
		} else if(this.time_type == TypeTimes.WEEK) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(System.currentTimeMillis());
			while (calendar.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.MONDAY) {
				calendar.add(GregorianCalendar.DAY_OF_WEEK,-1);
		    }
			calendar.set(GregorianCalendar.HOUR, 0);
			calendar.set(GregorianCalendar.MINUTE, 0);
			calendar.set(GregorianCalendar.SECOND, 0);
			return calendar.getTimeInMillis();
		} else if(this.time_type == TypeTimes.DAY) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(GregorianCalendar.HOUR, 0);
			calendar.set(GregorianCalendar.MINUTE, 0);
			calendar.set(GregorianCalendar.SECOND, 0);
			return calendar.getTimeInMillis();
		}
		return (long) 0;
	}
	
	@Override
	public void update(TypeScores type) {
		this.update();
	}

	@Override
	public void update(UUID uniqueId, TypeScores type) {
		this.update();
	}

	@Override
	public boolean isUpdate() {
		return true;
	}
	
	@Override
	protected void updateTitle() {		
		SidebarTitle title = this.getSidebarTitle();
		this.plugin.getLogger().debug("SidebarTitle : View (title='" + title.getTitle().toPlain() + "';next='" + title.getNext() + "')");
		
		this.objective.setDisplayName(title.getTitle());
	}
}
