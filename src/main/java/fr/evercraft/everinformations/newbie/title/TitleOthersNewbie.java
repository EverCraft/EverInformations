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
package fr.evercraft.everinformations.newbie.title;

import java.util.concurrent.CopyOnWriteArrayList;

import org.spongepowered.api.scheduler.Task;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.priority.PriorityService;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.TitleMessage;
import fr.evercraft.everinformations.newbie.Newbie;

public class TitleOthersNewbie extends Newbie {
	private final TitleConfig config;
	
	private final CopyOnWriteArrayList<TitleMessage> messages;

	private int priority;
	
	private EPlayer player;
	private int numero;
	private Task task;
	
	public TitleOthersNewbie(final EverInformations plugin) {
		super(plugin);
		
		this.config = new TitleConfig(this.plugin);
		this.messages = new CopyOnWriteArrayList<TitleMessage>();
		
		reload();
	}

	public void reload(){		
		stop();
		
		this.numero = 0;
		
		if(this.plugin.getEverAPI().getManagerService().getPriority().isPresent()) {
			this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get().getTitle(Newbie.IDENTIFIER_OTHER);
		} else {
			this.priority = PriorityService.DEFAULT;
		}
		
		this.enable = this.config.isOthersEnable();
		this.messages.clear();
		this.messages.addAll(this.config.getOthersMessages());
		
		if (this.messages.size() == 0 && this.enable) {
			this.plugin.getLogger().warn("TitleOthersNewbie : There is no message");
			this.enable = false;
		}
	}

	public void start() {
		this.numero = 0;
		
		this.view();
		this.task();
	}

	public void stop() {
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
		this.player = null;
	}
	
	public void task() {
		TitleMessage message = this.getMessage();
		
		if(this.hasNext()) {
			if(message.getNext() == 0) {
				this.next();
			} else {
				this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
								.execute(() -> this.next())
								.async()
								.delayTicks(message.getNext() + message.getStay())
								.name("TitleOthersNewbie")
								.submit(this.plugin);
			}
		}
	}
	
	public TitleMessage getMessage() {
		return this.messages.get(this.numero);
	}
	
	private boolean hasNext() {
		return this.numero < this.messages.size() - 1;
	}

	public void next() {		
		this.numero++;
		this.view();
		this.task();
	}

	protected void view() {
		if(this.enable) {
			TitleMessage message = this.getMessage();
			this.plugin.getLogger().debug("TitleOthersNewbie (priority='" + this.priority + "';title='" + message + "')");

			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				if(!this.player.equals(player)) {
					message.send(this.priority, player, this.player);
				}
			}
		}
	}

	@Override
	public void addPlayer(EPlayer player) {
		this.stop();
		this.player = player;
		this.start();
	}

	@Override
	public void removePlayer(EPlayer player) {
		if(this.player.equals(player)) {
			this.stop();
		}
	}
}
