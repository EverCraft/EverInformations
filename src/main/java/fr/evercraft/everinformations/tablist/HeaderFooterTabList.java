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
package fr.evercraft.everinformations.tablist;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.scheduler.Task;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.tablist.config.ConfigTabList;

public class HeaderFooterTabList {
	private final EverInformations plugin;
	
	private boolean enable;
	private int numero;
	
	private final ConfigTabList config;
	private final CopyOnWriteArrayList<TabListMessage> tablists;
	
	private Task task;
	private Task task_update;
	
	public HeaderFooterTabList(final EverInformations plugin, ConfigTabList config) {		
		this.plugin = plugin;
		
		this.enable = false;
		this.numero = 0;
		
		this.config = config;
		
		this.tablists = new CopyOnWriteArrayList<TabListMessage>();
		
		reload();
	}

	public void reload(){		
		stop();
		
		this.numero = 0;
		this.enable = this.config.isEnable();
		
		this.tablists.clear();
		this.tablists.addAll(this.config.getTabLists());
		
		if (this.tablists.size() == 0 && this.enable) {
			this.plugin.getLogger().warn("TabList : There is empty");
			this.enable = false;
		} else if (this.enable) {
			this.start();
		}
	}

	public void start() {
		this.stop();
		this.view();
		
		if(this.tablists.size() > 1) {
			this.task();
		}
	}

	public void stop() {
		this.stopUpdate();
		
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
		
		if(this.enable) {
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				player.getTabList().setHeaderAndFooter(null, null);
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
		TabListMessage objective = this.getTabList();
		
		if(objective.getNext() == 0) {
			this.next();
		} else {
			this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
							.execute(() -> this.next())
							.async()
							.delay(objective.getNext(), TimeUnit.MILLISECONDS)
							.name("TabList")
							.submit(this.plugin);
		}
	}
	
	public void addPlayer(EPlayer player) {
		if(this.enable) {
			TabListMessage tablist = this.getTabList();
			this.plugin.getLogger().debug("TabList add (player='" + player.getIdentifier() + "';"
														+ "tablist='" + tablist + "')");
			tablist.add(0, player);
		}
	}
	
	public void removePlayer(EPlayer player) {
		if(this.enable) {
			TabListMessage tablist = this.getTabList();
			this.plugin.getLogger().debug("TabList remove (player='" + player.getIdentifier() + "';"
														+ "tablist='" + tablist + "')");
			tablist.remove(player);
		}
	}
	
	public void next() {
		this.stopUpdate();
		this.getTabList().stop();

		this.numero++;
		if(this.numero >= this.tablists.size()){
			this.numero = 0;
		}
		this.view();
		this.task();
	}

	protected void view() {
		if(this.enable) {
			TabListMessage tablist = this.getTabList();
			tablist.start();
			this.plugin.getLogger().debug("TabList (objective='" + tablist + "')");
			
			tablist.update();

			this.stopUpdate();
			// Si l'Objective ne s'actualise pas tout seul
			if(!tablist.isUpdate()) {
				this.task_update = this.plugin.getGame().getScheduler().createTaskBuilder()
						.execute(() -> {
							this.plugin.getLogger().debug("TabList Update (tablist='" + tablist + "')");
							this.getTabList().update();
						})
						.async()
						.delay(tablist.getUpdate(), TimeUnit.MILLISECONDS)
						.interval(tablist.getUpdate(), TimeUnit.MILLISECONDS)
						.name("TabList Update")
						.submit(this.plugin);
			}
		}
	}
	
	public TabListMessage getTabList() {
		return this.tablists.get(this.numero);
	}
}
