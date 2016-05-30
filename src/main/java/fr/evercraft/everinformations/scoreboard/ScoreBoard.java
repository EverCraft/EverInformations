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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.priority.PriorityService;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.config.IConfig;
import fr.evercraft.everinformations.scoreboard.objective.EObjective;

public class ScoreBoard<T extends EObjective> {
	public final static String BELOW_NAME_IDENTIFIER = "everinfo.below";
	public final static String LIST_IDENTIFIER = "everinfo.list";
	public final static String SIDEBAR_IDENTIFIER = "everinfo.side";

	private final EverInformations plugin;
	
	private final DisplaySlot display;
	
	private boolean enable;
	private int numero;
	
	private final IConfig<T> config;
	private final CopyOnWriteArrayList<T> objectives;
	
	private int priority;
	private Task task;
	
	private Task task_update;
	 
	public ScoreBoard(final EverInformations plugin, IConfig<T> config, DisplaySlot display) {		
		this.plugin = plugin;
		
		this.enable = false;
		this.numero = 0;
		
		this.display = display;
		
		this.config = config;
		this.objectives = new CopyOnWriteArrayList<T>();
		
		this.reload();
	}

	public void reload(){		
		this.stop();

		this.priority = PriorityService.DEFAULT;
		if(this.plugin.getEverAPI().getManagerService().getPriority().isPresent()) {
			if(this.display.equals(DisplaySlots.BELOW_NAME)) {
				this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get().getScoreBoard(this.display, BELOW_NAME_IDENTIFIER);
			} else if(this.display.equals(DisplaySlots.LIST)) {
				this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get().getScoreBoard(this.display, LIST_IDENTIFIER);
			} else if(this.display.equals(DisplaySlots.SIDEBAR)) {
				this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get().getScoreBoard(this.display, SIDEBAR_IDENTIFIER);
			}
		}
		
		this.numero = 0;
		this.enable = this.config.isEnable();
		this.objectives.clear();
		this.objectives.addAll(this.config.getObjectives());
		
		if (this.objectives.size() == 0 && this.enable) {
			this.plugin.getLogger().warn("ScoreBoard (type='" + this.display.getName() + "') : There is no objective");
			this.enable = false;
		} else if (this.enable) {
			this.start();
		}
	}

	public boolean start() {		
		if(this.task == null) {
			this.view();
			
			if(this.objectives.size() > 1) {
				this.task();
			}
			return true;
		}
		return false;
	}

	public void stop() {		
		this.stopUpdate();
		
		if(!this.objectives.isEmpty()) {
			this.getObjective().stop();
		}
		
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
		
		for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			Optional<Objective> objective = player.getScoreboard().getObjective(getIgentifier());
			if(objective.isPresent()) {
				player.getScoreboard().removeObjective(objective.get());
			}
		}
	}
	
	public void stopUpdate() {		
		if (this.task_update != null) {
			this.task_update.cancel();
			this.task_update = null;
		}
	}
	
	public void task() {		
		T objective = this.getObjective();
		
		if(objective.getNext() == 0) {
			this.next();
		} else {
			this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
							.execute(() -> {
								this.plugin.getLogger().debug("ScoreBoard Task (type='" + this.display.getName() + "';priority='" + this.priority + "')");
								this.next();
							})
							.async()
							.delay(objective.getNext(), TimeUnit.MILLISECONDS)
							.name("ScoreBoard (type='" + this.display.getName() + "') " + System.currentTimeMillis())
							.submit(this.plugin);
		}
	}
	
	public void addPlayer(EPlayer player) {
		if(this.enable) {
			T objective = this.getObjective();
			this.plugin.getLogger().debug("ScoreBoard add (player='" + player.getIdentifier() + "';"
														+ "type='" + this.display.getName() + "';"
														+ "priority='" + this.priority + "';"
														+ "objective='" + objective + "')");
			objective.add(this.priority, player);
		}
	}
	
	public void removePlayer(EPlayer player) {
		if(this.enable) {
			T objective = this.getObjective();
			this.plugin.getLogger().debug("ScoreBoard remove (player='" + player.getIdentifier() + "';"
														+ "type='" + this.display.getName() + "';"
														+ "priority='" + this.priority + "';"
														+ "objective='" + objective + "')");
			objective.remove(player);
		}
	}
	
	public void next() {		
		this.stopUpdate();
		this.getObjective().stop();
		
		this.numero++;
		if(this.numero >= this.objectives.size()){
			this.numero = 0;
		}
		this.view();
		this.task();
	}

	protected void view() {
		if(this.enable) {			
			T objective = this.getObjective();
			objective.start();
			this.plugin.getLogger().debug("ScoreBoard (type='" + this.display.getName() + "';priority='" + this.priority + "';objective='" + objective + "')");
			
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				objective.add(this.priority, player);
			}
			
			this.stopUpdate();
			// Si l'Objective ne s'actualise pas tout seul
			if(!objective.isUpdate()) {
				this.task_update = this.plugin.getGame().getScheduler().createTaskBuilder()
						.execute(() -> {
							this.plugin.getLogger().debug("ScoreBoard Update (type='" + this.display.getName() + "';priority='" + this.priority + "';objective='" + this.getObjective() + "')");
							this.getObjective().update();
						})
						.async()
						.delay(objective.getUpdate(), TimeUnit.MILLISECONDS)
						.interval(objective.getUpdate(), TimeUnit.MILLISECONDS)
						.name("ScoreBoard Update (type='" + this.display.getName() + "') " + System.currentTimeMillis())
						.submit(this.plugin);
			}
		}
	}
	
	public T getObjective() {
		return this.objectives.get(this.numero);
	}
	
	public String getIgentifier() {
		if(this.display.equals(DisplaySlots.BELOW_NAME)) {
			return BELOW_NAME_IDENTIFIER;
		} else if(this.display.equals(DisplaySlots.LIST)) {
			return LIST_IDENTIFIER;		
		} else if(this.display.equals(DisplaySlots.SIDEBAR)) {
			return SIDEBAR_IDENTIFIER;
		}
		return "";
	}
}
