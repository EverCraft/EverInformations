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
package fr.evercraft.everinformations.newbie.actionbar;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.spongepowered.api.scheduler.Task;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.priority.PriorityService;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.ActionBarMessage;
import fr.evercraft.everinformations.newbie.Newbie;

public class ActionBarPlayerNewbie extends Newbie {	
	private final ActionBarConfig config;
	
	private int priority;
	
	private final CopyOnWriteArrayList<ActionBarMessage> messages;
	private final ConcurrentMap<UUID, TitleOthers> players;

	public ActionBarPlayerNewbie(final EverInformations plugin) {
		super(plugin);
		this.config = new ActionBarConfig(this.plugin);
		this.messages = new CopyOnWriteArrayList<ActionBarMessage>();
		this.players = new ConcurrentHashMap<UUID, TitleOthers>();
		
		reload();
	}

	@Override
	public void reload() {
		stop();
		
		if(this.plugin.getEverAPI().getManagerService().getPriority().isPresent()) {
			this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get().getTitle(Newbie.IDENTIFIER_PLAYER);
		} else {
			this.priority = PriorityService.DEFAULT;
		}
		
		this.enable = this.config.isPlayerEnable();
		this.messages.clear();
		this.messages.addAll(this.config.getPlayerMessages());
		
		if (this.messages.size() == 0 && this.enable) {
			this.plugin.getLogger().warn("ActionBarPlayerNewbie : There is no message");
			this.enable = false;
		}
	}
	
	@Override
	public void stop() {
		for(TitleOthers player : this.players.values()) {
			player.stop();
		}
		this.players.clear();
	}

	@Override
	public void addPlayer(EPlayer player) {
		if(this.enable) {
			if(this.messages.size() > 1) {
				this.players.put(player.getUniqueId(), new TitleOthers(this, player));
			} else {
				this.messages.get(0).send(this.priority, player);
			}
		}
	}
	
	@Override
	public void removePlayer(EPlayer player) {
		TitleOthers titlePlayer = this.players.get(player.getUniqueId());
		if(titlePlayer != null) {
			titlePlayer.stop();
			this.players.remove(player.getUniqueId());
		}
	}
	
	private class TitleOthers {
		private final ActionBarPlayerNewbie chat;
		private final EPlayer player;
		private int numero;
		private Task task;
		
		protected TitleOthers(final ActionBarPlayerNewbie chat, final EPlayer player) {
			this.chat = chat;
			this.player = player;
			
			this.numero = 0;
			
			this.view();
			this.task();
		}
		
		public void stop() {
			if(this.task != null) {
				this.task.cancel();
				this.task = null;
			}
		}

		public void task() {
			ActionBarMessage message = this.getMessage();
			
			if(message.getNext() == 0) {
				this.next();
			} else {
				this.task = this.chat.plugin.getGame().getScheduler().createTaskBuilder()
								.execute(() -> this.next())
								.async()
								.delayTicks(message.getNext())
								.name("ActionBarPlayerNewbie")
								.submit(this.chat.plugin);
			}
		}
		
		public void next() {		
			this.numero++;
			this.view();
			if(this.numero < this.chat.messages.size() - 1){
				this.task();
			}
		}

		protected void view() {
			ActionBarMessage message = this.getMessage();
			this.chat.plugin.getLogger().debug("ActionBarPlayerNewbie (player='" + player.getIdentifier() + "',message='" + message + "')");
			message.send(this.chat.priority, player);
		}
		
		public ActionBarMessage getMessage() {
			return this.chat.messages.get(this.numero);
		}
		
	}
}
