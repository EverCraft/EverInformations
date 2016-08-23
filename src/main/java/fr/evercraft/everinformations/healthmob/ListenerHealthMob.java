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
package fr.evercraft.everinformations.healthmob;

import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Creature;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.health.source.EntityHealingSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.HealEntityEvent;

import fr.evercraft.everinformations.EverInformations;

public class ListenerHealthMob {
	private EverInformations plugin;
	
	public ListenerHealthMob(final EverInformations plugin) {
		this.plugin = plugin;
	}
	
	@Listener(order=Order.POST)
	public void onPlayerDamage(DamageEntityEvent event) {
		if (this.plugin.getHealthMob().isEnable()) {
			// Si l'entité est une créature
			if (event.getTargetEntity() instanceof Creature && !event.willCauseDeath()) {
				Optional<EntityDamageSource> optDamageSource = event.getCause().first(EntityDamageSource.class);
				if (optDamageSource.isPresent()) {
					// Si le dégât est un projectile 
					if (optDamageSource.get().getSource() instanceof Projectile) {
						ProjectileSource projectile = ((Projectile)optDamageSource.get().getSource()).getShooter();
						// Si c'est un joueur qui a lancé le projectile
			        	if (projectile instanceof Player) {
			        		this.plugin.getHealthMob().add(event.getTargetEntity(), event.getTargetEntity().get(Keys.HEALTH).orElse(0.0) - event.getFinalDamage());
			        	} else {
							this.plugin.getHealthMob().update(event.getTargetEntity(), event.getTargetEntity().get(Keys.HEALTH).orElse(0.0) - event.getFinalDamage());
						}
			        // Si c'est un joueur qui a fait le dégât
					} else if (optDamageSource.get().getSource() instanceof Player) {
						this.plugin.getHealthMob().add(event.getTargetEntity(), event.getTargetEntity().get(Keys.HEALTH).orElse(0.0) - event.getFinalDamage());
					} else {
						this.plugin.getHealthMob().update(event.getTargetEntity(), event.getTargetEntity().get(Keys.HEALTH).orElse(0.0) - event.getFinalDamage());
					}
				}
			// Si l'entité est une joueur et qu'il est mort
			} else if (event.getTargetEntity() instanceof Player && event.willCauseDeath()) {
				Optional<EntityDamageSource> optDamageSource = event.getCause().first(EntityDamageSource.class);
				if (optDamageSource.isPresent()) {
					// Si le dégât est un projectile
					if (optDamageSource.get().getSource() instanceof Projectile) {
						ProjectileSource projectile = ((Projectile)optDamageSource.get().getSource()).getShooter();
						// Si c'est un animal qui a lancé le projectile
			        	if (projectile instanceof Creature) {
							this.plugin.getHealthMob().remove((Creature) projectile);
			        	}
			        // Si c'est un animal qui a fait le dégât
					} else if (optDamageSource.get().getSource() instanceof Creature) {
						this.plugin.getHealthMob().remove(optDamageSource.get().getSource());
					}
				}
			}
		}
	}
	
	@Listener(order=Order.PRE)
	public void onPlayerDamage(DestructEntityEvent.Death event) {
		if (event.getTargetEntity() instanceof Creature && this.plugin.getHealthMob().isEnable()) {
			this.plugin.getHealthMob().remove(event.getTargetEntity());
		}
	}
	
	/*
	 * Event pas encore implémenté
	 */
	@Listener(order=Order.POST)
	public void onPlayerDamage(HealEntityEvent event) {
		if (event.getTargetEntity() instanceof Creature && this.plugin.getHealthMob().isEnable()) {			
			Optional<EntityHealingSource> optHealingSource = event.getCause().first(EntityHealingSource.class);
			if (optHealingSource.isPresent() && optHealingSource.get().getSource() instanceof Player) {
				this.plugin.getHealthMob().add(event.getTargetEntity(), event.getTargetEntity().get(Keys.HEALTH).orElse(0.0) - event.getFinalHealAmount());
			} else {
				this.plugin.getHealthMob().update(event.getTargetEntity(), event.getTargetEntity().get(Keys.HEALTH).orElse(0.0) - event.getFinalHealAmount());
			}
		}
	}	
}
