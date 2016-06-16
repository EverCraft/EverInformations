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
package fr.evercraft.everinformations.newbie;

import java.util.concurrent.TimeUnit;

import org.spongepowered.api.scheduler.Task;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.BossBarMessage;
import fr.evercraft.everinformations.message.IMessage;
import fr.evercraft.everinformations.newbie.Newbie;
import fr.evercraft.everinformations.newbie.config.IConfig;

public class NewbieOthers<T extends IMessage> extends Newbie<T> {
	private EPlayer player;
	private int numero;
	private Task task;
	
	public NewbieOthers(final EverInformations plugin, final IConfig<T> config, Type type) {
		super(plugin, config, type);
		
		reload();
	}

	public void reload(){		
		stop();
		
		this.loadPriority();
		this.numero = 0;
		this.enable = this.config.isOthersEnable();
		this.messages.clear();
		this.messages.addAll(this.config.getOthersMessages());
		
		if (this.messages.size() == 0 && this.enable) {
			this.plugin.getLogger().warn("Newbie (type='" + this.type.name() + "') : There is no message");
			this.enable = false;
		}
	}

	public void start() {
		// Enable et n'est pas Start
		if(this.enable && this.task == null) {
			this.numero = -1;
			this.next();
		}
	}

	public void stop() {
		this.remove();
		
		// Si Start
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
		this.player = null;
	}
	
	public void next() {		
		this.numero++;
		this.view();
		
		// Si il y a encore un message
		if(this.numero < this.messages.size() - 1) {
			this.task();
		} else if(this.type.equals(Type.BOSSBAR_OTHERS)) {
			// Si il y a pas de délai
			if(((BossBarMessage) this.getMessage()).getNext() <= 0) {
				this.remove();
			// Il y a un délai
			} else {
				this.taskRemoveBossBar();
			}
		}
	}
	
	protected void view() {
		T message = this.getMessage();
		this.plugin.getLogger().debug("Newbie (type='" + this.type.name() + "';priority='" + this.priority + "';title='" + message + "')");

		// Affiche le message à tous les autres joueurs
		for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			if(!this.player.equals(player)) {
				message.send(IDENTIFIER_OTHERS, this.priority, player, this.player);
			}
		}
	}
	
	public void task() {
		T message = this.getMessage();
		
		if(this.type.equals(Type.BOSSBAR_OTHERS)) {
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
		this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
				.execute(() -> this.next())
				.async()
				.delay(message.getNext(), TimeUnit.MILLISECONDS)
				.name("Newbie : Next (type='" + this.type.name() + "')")
				.submit(this.plugin);
	}
	
	/**
	 * Le temps avant la prochaine bossbar
	 */
	public void taskNextBossBar() {
		if(this.getMessage() instanceof BossBarMessage) {
			BossBarMessage message = ((BossBarMessage) this.getMessage());
			this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
					.execute(() -> this.next())
					.async()
					.delay(message.getTimeNext(), TimeUnit.MILLISECONDS)
					.name("Newbie : NextBossBar (type='" + this.type.name() + "')")
					.submit(this.plugin);
		}
	}
	
	/**
	 * Si il y a un délai en 2 bossbars, il faut supprimer la première au bout d'un certain temps
	 */
	public void taskRemoveBossBar() {
		if(this.getMessage() instanceof BossBarMessage) {
			BossBarMessage message = ((BossBarMessage) this.getMessage());
			this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
					.execute(() -> {
						this.remove();
						if(this.numero < this.messages.size() - 1) {
							this.taskNextBossBar();
						}
					})
					.async()
					.delay(message.getNext(), TimeUnit.MILLISECONDS)
					.name("Newbie : RemoveBossBar (type='" + this.type.name() + "')")
					.submit(this.plugin);
		}
	}
	
	public T getMessage() {
		return this.messages.get(this.numero);
	}

	@Override
	public void addPlayer(EPlayer player) {
		this.stop();
		this.player = player;
		this.start();
	}

	@Override
	public void removePlayer(EPlayer player) {
		if(this.player != null && this.player.equals(player)) {
			this.stop();
		}
	}
	
	public void remove() {
		if(this.enable && this.player != null && this.messages != null && !this.messages.isEmpty() && this.getMessage() instanceof BossBarMessage) {
			BossBarMessage message = ((BossBarMessage) this.getMessage());
			
			this.plugin.getLogger().debug("Newbie : RemoveBossbar (type='" + this.type.name() + "';priority='" + this.priority + "';message='" + message + "')");
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				if(!this.player.equals(player)) {
					message.remove(IDENTIFIER_OTHERS, player);
				}
			}
		}
	}
}
