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
package fr.evercraft.everinformations.connection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.connection.config.IConfig;
import fr.evercraft.everinformations.message.BossBarMessage;
import fr.evercraft.everinformations.message.IMessage;

public class ConnectionPlayer<T extends IMessage> extends Connection<T> {	
	private final ConcurrentMap<UUID, Player> players;

	public ConnectionPlayer(final EverInformations plugin, final IConfig<T> config, Type type) {
		super(plugin, config, type);
		
		this.players = new ConcurrentHashMap<UUID, Player>();
		
		this.reload();
	}

	@Override
	public void reload() {
		this.stop();
		
		this.loadPriority();
		this.enable = this.config.isPlayerEnable();
		this.messages_join.clear();
		this.messages_quit.clear();
		this.messages_kick.clear();
		
		if(this.type.equals(Type.CHAT_OTHERS)) {
			this.messages_join.putAll(this.config.getOthersJoinMessages());
			this.messages_quit.putAll(this.config.getOthersQuitMessages());
			this.messages_kick.putAll(this.config.getOthersKickMessages());
		} else {
			this.messages_join.putAll(this.config.getPlayerJoinMessages());
		}
		
		if (this.enable && this.messages_join.isEmpty() && this.messages_quit.isEmpty() && this.messages_kick.isEmpty()) {
			this.plugin.getLogger().warn("Connection (type='" + this.type.name() + "') : There is no message");
			this.enable = false;
		}
	}
	
	@Override
	public void stop() {
		for(Player player : this.players.values()) {
			player.stop();
		}
		this.players.clear();
	}
	
	@Override
	public void joinPlayer(EPlayer player, Optional<Subject> subject) {
		if(this.enable) {
			this.remove(player);
			Player player_connection = new Player(this, player);
			this.players.put(player.getUniqueId(), player_connection);
			player_connection.join(subject);
		}
	}

	@Override
	public void quitPlayer(EPlayer player, Optional<Subject> subject) {
		if(this.enable) {
			this.remove(player);
			Player player_connection = new Player(this, player);
			this.players.put(player.getUniqueId(), player_connection);
			player_connection.quit(subject);
		}
	}
	
	@Override
	public void kickPlayer(EPlayer player, Optional<Subject> subject, Text reason) {
		if(this.enable) {
			this.remove(player);
			Player player_connection = new Player(this, player);
			this.players.put(player.getUniqueId(), player_connection);
			player_connection.kick(subject, reason);
		}
	}
	
	private void remove(EPlayer player) {
		Player titlePlayer = this.players.get(player.getUniqueId());
		if(titlePlayer != null) {
			titlePlayer.stop();
			this.players.remove(player.getUniqueId());
		}
	}
	
	private class Player {
		private final ConnectionPlayer<T> connection;
		
		protected List<T> messages;
		
		private final EPlayer player;
		private Text reason;
		private int numero;
		private Task task;
		
		protected Player(final ConnectionPlayer<T> connection, final EPlayer player) {
			this.connection = connection;
			
			this.player = player;
			this.numero = -1;
		}
		
		public void join(Optional<Subject> subject) {
			this.messages = this.connection.getMessagesJoin(subject);
			this.next();
		}

		public void quit(Optional<Subject> subject) {
			this.messages = this.connection.getMessagesQuit(subject);
			this.next();
		}
		
		public void kick(Optional<Subject> subject, Text reason) {
			this.messages = this.connection.getMessagesKick(subject);
			this.reason = reason;
			this.next();
		}

		public void stop() {
			this.remove();
			
			if(this.task != null) {
				this.task.cancel();
				this.task = null;
			}
		}
		
		public void next() {
			if(!this.messages.isEmpty()) {
				this.numero++;
				this.view();
				
				//Si il y a encore un message
				if(this.numero < this.messages.size() - 1){
					this.task();
				} else if(this.connection.type.equals(Type.BOSSBAR_PLAYER)) {
					// Si il y a pas de délai
					if(((BossBarMessage) this.getMessage()).getNext() <= 0) {
						this.remove();
					// Il y a un délai
					} else {
						this.taskRemoveBossBar();
					}
				} else {
					this.remove();
				}
			} else {
				this.connection.players.remove(player.getUniqueId());
			}
		}
		
		protected void view() {
			T message = this.getMessage();
			this.connection.plugin.getLogger().debug("Connection (type='" + this.connection.type.name() + "';priority='" + this.connection.priority + ";player='" + player.getIdentifier() + "',message='" + message + "')");
			
			if(this.reason == null) {
				if(this.connection.type.equals(Type.CHAT_OTHERS)) {
					// Affiche le message à tous les autres joueurs
					for(EPlayer player : this.connection.plugin.getEServer().getOnlineEPlayers()) {
						if(!this.player.equals(player)) {
							message.send(IDENTIFIER_PLAYER, this.connection.priority, player, this.player);
						}
					}
				} else {
					// Affiche le message au joueur
					message.send(IDENTIFIER_PLAYER, this.connection.priority, this.player);
				}
			} else {
				if(this.connection.type.equals(Type.CHAT_OTHERS)) {
					// Affiche le message à tous les autres joueurs
					for(EPlayer player : this.connection.plugin.getEServer().getOnlineEPlayers()) {
						if(!this.player.equals(player)) {
							message.send(IDENTIFIER_PLAYER, this.connection.priority, player, this.player, this.reason);
						}
					}
				} else {
					// Affiche le message au joueur
					message.send(IDENTIFIER_PLAYER, this.connection.priority, this.player, this.reason);
				}
			}
		}
		
		public void task() {
			T message = this.getMessage();
			
			if(this.connection.plugin.equals(Type.BOSSBAR_PLAYER)) {
				// Si il y a pas de délai
				if(((BossBarMessage) message).getTimeNext() <= 0) {
					this.taskNext();
				// Il y a un délai
				} else {
					this.taskRemoveBossBar();
				}
			} else {
				// Si il y a pas de délai
				if(message.getNext() <= 0) {
					this.next();
				// Il y a un délai
				} else {
					this.taskNext();
				}
			}
		}
		
		public void taskNext() {
			T message = this.getMessage();
			this.task = this.connection.plugin.getGame().getScheduler().createTaskBuilder()
					.execute(() -> this.next())
					.async()
					.delay(message.getNext(), TimeUnit.MILLISECONDS)
					.name("Connection : Next (type='" + this.connection.type.name() + "')")
					.submit(this.connection.plugin);
		}
		
		/**
		 * Le temps avant la prochaine bossbar
		 */
		public void taskNextBossBar() {
			if(this.getMessage() instanceof BossBarMessage) {
				BossBarMessage message = ((BossBarMessage) this.getMessage());
				this.task = this.connection.plugin.getGame().getScheduler().createTaskBuilder()
						.execute(() -> this.next())
						.async()
						.delay(message.getTimeNext(), TimeUnit.MILLISECONDS)
						.name("Connection : NextBossBar (type='" + this.connection.type.name() + "')")
						.submit(this.connection.plugin);
			}
		}
		
		/**
		 * Si il y a un délai en 2 bossbars, il faut supprimer la première au bout d'un certain temps
		 */
		public void taskRemoveBossBar() {
			if(this.getMessage() instanceof BossBarMessage) {
				BossBarMessage message = ((BossBarMessage) this.getMessage());
				this.task = this.connection.plugin.getGame().getScheduler().createTaskBuilder()
						.execute(() -> {
							this.remove();
							if(this.numero < this.messages.size() - 1) {
								this.taskNextBossBar();
							}
						})
						.async()
						.delay(message.getNext(), TimeUnit.MILLISECONDS)
						.name("Connection : RemoveBossBar (type='" + this.connection.type.name() + "')")
						.submit(this.connection.plugin);
			}
		}
		
		public T getMessage() {
			return this.messages.get(this.numero);
		}
		
		public void remove() {
			if(this.messages != null && !this.messages.isEmpty() && this.getMessage() instanceof BossBarMessage) {
				BossBarMessage message = ((BossBarMessage) this.getMessage());
				
				this.connection.plugin.getLogger().debug("Connection : RemoveBossbar (type='" + this.connection.type.name() + "';priority='" + this.connection.priority + "';message='" + message + "')");
				message.remove(IDENTIFIER_PLAYER, this.player);
			}
			this.connection.players.remove(player.getUniqueId());
		}
		
	}
}
