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
package fr.evercraft.everinformations.scoreboard.objective;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.scheduler.Task;

import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everinformations.scoreboard.objective.sidebar.SidebarTitle;

public abstract class SidebarObjective extends EObjective {
	public static enum Type {
		INFORMATIONS,
		ECONOMY;
    }
	
	protected final Type type;
	
	protected final List<SidebarTitle> titles;

	private Task task;

	private int numero;

	
	public SidebarObjective(final EPlugin plugin, final double stay, final double update, Type type, List<SidebarTitle> titles) {
		super(plugin, stay, update);
		this.type = type;
		this.titles = titles;
		
		this.numero = 0;
	}
	
	@Override
	public long getNext() {
		return (long) (this.stay * 1000);
	}
	
	protected abstract boolean subStart();
	protected abstract boolean subStop();
	protected abstract void updateTitle();
	
	public boolean start() {
		this.subStart();
		
		if(this.titles.size() > 1) {
			this.task();
		}
		
		return true;
	}

	public boolean stop() {
		// Si start
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
		this.subStop();
		
		return true;
	}
	
	public void task() {
		SidebarTitle title = this.getSidebarTitle();
		
		// Si il n'y a pas de délai
		if(title.getNext() == 0) {
			this.next();
		// Il y a un délai
		} else {
			this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
							.execute(() -> this.next())
							.async()
							.delay(title.getNext(), TimeUnit.MILLISECONDS)
							.name("SidebarObjective : SidebarTitle")
							.submit(this.plugin);
		}
	}
	
	public void next() {
		this.numero++;
		if(this.numero >= this.titles.size()){
			this.numero = 0;
		}
		this.updateTitle();
		this.task();
	}

	public SidebarTitle getSidebarTitle() {
		return this.titles.get(this.numero);
	}
	
	@Override
	public String toString() {
		return "SidebarObjective [type=" + type + ", titles=" + titles
				+ ", stay=" + stay + ", update=" + update_time + "]";
	}

}
