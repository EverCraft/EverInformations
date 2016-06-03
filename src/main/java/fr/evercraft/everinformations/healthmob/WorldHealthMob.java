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

import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everinformations.EverInformations;

public class WorldHealthMob {
	
	private final EverInformations plugin;
	
	private final UUID world;
	private final ConcurrentMap<UUID, EntityHealthMob> entities;
	
	public WorldHealthMob(final EverInformations plugin, UUID world) {
		this.plugin = plugin;
		
		this.world = world;
		this.entities = new ConcurrentHashMap<UUID, EntityHealthMob>();
	}
	
	public void reset() {
		Optional<World> world = this.plugin.getGame().getServer().getWorld(this.world);
		if(world.isPresent()) {
			for(Entry<UUID, EntityHealthMob> healthmob : this.entities.entrySet()) {
				healthmob.getValue().getTask().cancel();
				Optional<Entity> entity = world.get().getEntity(healthmob.getKey());
				if(entity.isPresent()) {
					this.plugin.getLogger().debug("HealthMob : Remove (uuid='" + healthmob.getKey() + "';name='" + healthmob.getValue() + "'");
					entity.get().offer(Keys.DISPLAY_NAME, healthmob.getValue().getName());
				}
			}
		}
		
		this.entities.clear();
	}
	
	public boolean send(Entity entity, Double health) {
		EntityHealthMob healthmob = this.entities.get(entity.getUniqueId());
		if(healthmob == null) {
			Optional<Text> display_name = entity.get(Keys.DISPLAY_NAME);
			this.plugin.getLogger().debug("HealthMob : Add (uuid='" + entity.getUniqueId() + "';name='" + display_name + "'");
			healthmob = new EntityHealthMob(display_name);
			this.entities.put(entity.getUniqueId(), new EntityHealthMob(display_name));
		}
		entity.offer(Keys.DISPLAY_NAME, this.getName(entity, health));
		
		healthmob.cancel();
		this.task(healthmob, entity.getUniqueId());
		
		return true;
	}
	
	public void update(Entity entity, Double health) {
		EntityHealthMob healthmob = this.entities.get(entity.getUniqueId());
		if(healthmob != null) {
			entity.offer(Keys.DISPLAY_NAME, this.getName(entity, health));
			
			healthmob.cancel();
			this.task(healthmob, entity.getUniqueId());
		}
	}
	
	public boolean remove(UUID uuid) {
		Optional<World> world = this.plugin.getGame().getServer().getWorld(this.world);
		if(world.isPresent()) {
			Optional<Entity> entity = world.get().getEntity(uuid);
			if(entity.isPresent()) {
				return this.remove(entity.get());
			}
		}
		return false;
	}
	
	public boolean remove(Entity entity) {
		EntityHealthMob healthmob = this.entities.remove(entity.getUniqueId());
		if(healthmob != null) {
			this.plugin.getLogger().debug("HealthMob : Remove (uuid='" + entity.getUniqueId() + "';name='" + healthmob.getName() + "'");
        	
			healthmob.cancel();
			
        	Optional<World> world = this.plugin.getGame().getServer().getWorld(this.world);
			if(world.isPresent()) {
				entity.offer(Keys.DISPLAY_NAME, healthmob.getName());
			}
			return true;
		}
		return false;
	}
	
	public Text getName(final Entity entity, final Double health) {
		String message = null;
		Double max_health = entity.get(Keys.MAX_HEALTH).orElse(0.0);
		if(this.plugin.getHealthMob().getMessage().size() == 1) {
			message = this.plugin.getHealthMob().getMessage().get(0);
		} else {
			message = this.plugin.getHealthMob().getMessage().get(Math.max(0, (int) Math.round((this.plugin.getHealthMob().getMessage().size()-1)*(health/max_health))));
		}
		message = message.replaceAll(EChat.HEALTH, health.toString());
		message = message.replaceAll(EChat.MAX_HEALTH, max_health.toString());
		return EChat.of(this.plugin.getChat().replaceGlobal(message));
	}
	
	public void task(final EntityHealthMob healthmob, final UUID uuid) {
		healthmob.setTask(this.plugin.getGame().getScheduler().createTaskBuilder()
				.execute(()->this.remove(uuid))
				.delay(this.plugin.getHealthMob().getStay(), TimeUnit.SECONDS)
				.name("HealthMob")
				.submit(this.plugin));
	}
}