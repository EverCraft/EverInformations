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
package fr.evercraft.everinformations.newbie;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.scheduler.Task;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.BossBarMessage;
import fr.evercraft.everinformations.message.IMessage;
import fr.evercraft.everinformations.newbie.config.IConfig;

public class NewbiePlayer<T extends IMessage> extends Newbie<T> {	
	private final ConcurrentMap<UUID, Player> players;

	public NewbiePlayer(final EverInformations plugin, final IConfig<T> config, Type type) {
		super(plugin, config, type);
		
		this.players = new ConcurrentHashMap<UUID, Player>();
		
		this.reload();
	}

	@Override
	public void reload() {
		this.stop();
		
		this.loadPriority();
		this.enable = this.config.isPlayerEnable();
		this.messages.clear();
		
		if (this.type.equals(Type.CHAT_OTHERS)) {
			this.messages.addAll(this.config.getOthersMessages());
		} else {
			this.messages.addAll(this.config.getPlayerMessages());
		}
		
		if (this.messages.size() == 0 && this.enable) {
			this.plugin.getELogger().warn("Newbie (type='" + this.type.name() + "') : There is no message");
			this.enable = false;
		}
	}
	
	@Override
	public void stop() {
		for (Player player : this.players.values()) {
			player.stop();
		}
		this.players.clear();
	}

	@Override
	public void addPlayer(EPlayer player) {
		if (this.enable) {
			this.players.put(player.getUniqueId(), new Player(this, player));
		}
	}
	
	@Override
	public void removePlayer(EPlayer player) {
		if (this.enable) {
			Player titlePlayer = this.players.get(player.getUniqueId());
			if (titlePlayer != null) {
				titlePlayer.stop();
				this.players.remove(player.getUniqueId());
			}
		}
	}
	
	private class Player {
		private final NewbiePlayer<T> newbie;
		
		private final EPlayer player;
		private int numero;
		private Task task;
		
		protected Player(final NewbiePlayer<T> newbie, final EPlayer player) {
			this.newbie = newbie;
			
			this.player = player;
			this.numero = -1;
			
			this.next();
		}
		
		public void stop() {
			this.remove();
			
			if (this.task != null) {
				this.task.cancel();
				this.task = null;
			}
		}
		
		public void next() {
			this.numero++;
			this.view();
			
			//Si il y a encore un message
			if (this.numero < this.newbie.messages.size() - 1){
				this.task();
			} else if (this.newbie.type.equals(Type.BOSSBAR_PLAYER)) {
				// Si il y a pas de délai
				if (((BossBarMessage) this.getMessage()).getNext() <= 0) {
					this.remove();
				// Il y a un délai
				} else {
					this.taskRemoveBossBar();
				}
			} else {
				this.remove();
			}
		}
		
		protected void view() {
			T message = this.getMessage();
			this.newbie.plugin.getELogger().debug("Newbie (type='" + this.newbie.type.name() + "';priority='" + this.newbie.priority + ";player='" + player.getIdentifier() + "',message='" + message + "')");
			
			if (this.newbie.type.equals(Type.CHAT_OTHERS)) {
				// Affiche le message à tous les autres joueurs
				for (EPlayer player : this.newbie.plugin.getEServer().getOnlineEPlayers()) {
					if (!this.player.equals(player)) {
						message.send(IDENTIFIER_PLAYER, this.newbie.priority, player, this.player);
					}
				}
			} else {
				// Affiche le message au joueur
				message.send(IDENTIFIER_PLAYER, this.newbie.priority, player);
			}
		}

		public void task() {
			T message = this.getMessage();
			
			if (this.newbie.type.equals(Type.BOSSBAR_PLAYER)) {
				// Si il y a pas de délai
				if (((BossBarMessage) message).getTimeNext() <= 0) {
					this.taskNext();
				// Il y a un délai
				} else {
					this.taskRemoveBossBar();
				}
			} else {
				// Si il y a pas de délai
				if (message.getNext() <= 0) {
					this.next();
				// Il y a un délai
				} else {
					this.taskNext();
				}
			}
		}
		
		public void taskNext() {
			T message = this.getMessage();
			this.task = this.newbie.plugin.getGame().getScheduler().createTaskBuilder()
					.execute(() -> this.next())
					.async()
					.delay(message.getNext(), TimeUnit.MILLISECONDS)
					.name("Newbie : Next (type='" + this.newbie.type.name() + "')")
					.submit(this.newbie.plugin);
		}
		
		/**
		 * Le temps avant la prochaine bossbar
		 */
		public void taskNextBossBar() {
			if (this.getMessage() instanceof BossBarMessage) {
				BossBarMessage message = ((BossBarMessage) this.getMessage());
				this.task = this.newbie.plugin.getGame().getScheduler().createTaskBuilder()
						.execute(() -> this.next())
						.async()
						.delay(message.getTimeNext(), TimeUnit.MILLISECONDS)
						.name("Newbie : NextBossBar (type='" + this.newbie.type.name() + "')")
						.submit(this.newbie.plugin);
			}
		}
		
		/**
		 * Si il y a un délai en 2 bossbars, il faut supprimer la première au bout d'un certain temps
		 */
		public void taskRemoveBossBar() {
			if (this.getMessage() instanceof BossBarMessage) {
				BossBarMessage message = ((BossBarMessage) this.getMessage());
				this.task = this.newbie.plugin.getGame().getScheduler().createTaskBuilder()
						.execute(() -> {
							this.remove();
							if (this.numero < this.newbie.messages.size() - 1) {
								this.taskNextBossBar();
							}
						})
						.async()
						.delay(message.getNext(), TimeUnit.MILLISECONDS)
						.name("Newbie : RemoveBossBar (type='" + this.newbie.type.name() + "')")
						.submit(this.newbie.plugin);
			}
		}
		
		public T getMessage() {
			return this.newbie.messages.get(this.numero);
		}
		
		public void remove() {
			if (this.getMessage() instanceof BossBarMessage) {
				BossBarMessage message = ((BossBarMessage) this.getMessage());
				
				this.newbie.plugin.getELogger().debug("Newbie : RemoveBossbar (type='" + this.newbie.type.name() + "';priority='" + this.newbie.priority + "';message='" + message + "')");
				message.remove(IDENTIFIER_PLAYER, this.player);
			}
			this.newbie.players.remove(player.getUniqueId());
		}
		
	}
}
