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
package fr.evercraft.everinformations.healthmob;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.spongepowered.api.entity.Entity;

import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.healthmob.config.ConfigHealthMob;

public class ManagerHealthMob {
	private final EverInformations plugin;
	
	private final ConfigHealthMob config;

	private boolean enable;
	private int stay;
	private final CopyOnWriteArrayList<String> messages;
	
	private final ConcurrentHashMap<UUID, WorldHealthMob> worlds;
	
	private final CopyOnWriteArrayList<String> disable_worlds;
	private final CopyOnWriteArrayList<String> disable_entities;
	 
	public ManagerHealthMob(final EverInformations plugin) {		
		this.plugin = plugin;
		
		this.config = new ConfigHealthMob(this.plugin);
		this.enable = false;
		
		this.worlds = new ConcurrentHashMap<UUID, WorldHealthMob>();
		
		this.messages = new CopyOnWriteArrayList<String>();
		this.disable_worlds = new CopyOnWriteArrayList<String>();
		this.disable_entities = new CopyOnWriteArrayList<String>();
		
		this.plugin.getGame().getEventManager().registerListeners(this.plugin, new ListenerHealthMob(this.plugin));

		this.reload();
	}

	public void reload(){		
		this.reset();
		
		this.enable = this.config.isEnable();
		this.stay = this.config.getStay();
		
		this.messages.clear();
		this.disable_worlds.clear();
		this.disable_entities.clear();
		
		this.messages.addAll(this.config.getMessages());
		this.disable_worlds.addAll(this.config.getDisableWorlds());
		this.disable_entities.addAll(this.config.getDisableEntities());
	}

	public void reset() {
		for(WorldHealthMob world : this.worlds.values()) {
			world.reset();
		}
		this.worlds.clear();
	}
	
	public boolean add(Entity entity, double health) {
		// Si ce type d'entité n'est pas désactivé
		if(!this.disable_entities.contains(entity.getType().getName().toUpperCase())) {
			WorldHealthMob world = this.worlds.get(entity.getWorld().getUniqueId());
			if(world == null) {
				// Si le monde n'est pas désactivé
				if(!this.disable_worlds.contains(entity.getWorld().getName())) {
					world = new WorldHealthMob(this.plugin, entity.getWorld().getUniqueId());
					this.worlds.put(entity.getWorld().getUniqueId(), world);
				} else {
					return false;
				}
			}
			world.send(entity, health);
			return true;
		}
		return false;
	}
	
	public void update(Entity entity, double health) {
		WorldHealthMob world = this.worlds.get(entity.getWorld().getUniqueId());
		// Si le monde existe
		if(world != null) {
			world.update(entity, health);
		}
	}
	
	public void remove(Entity entity) {
		WorldHealthMob world = this.worlds.get(entity.getWorld().getUniqueId());
		// Si le monde existe
		if(world != null) {
			world.remove(entity);
		}
	}

	public boolean isEnable() {
		return this.enable;
	}
	
	public int getStay() {
		return this.stay;
	}

	public List<String> getMessage() {
		return this.messages;
	}
}
