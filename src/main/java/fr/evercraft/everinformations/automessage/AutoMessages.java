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
package fr.evercraft.everinformations.automessage;

import org.spongepowered.api.scheduler.Task;

import fr.evercraft.everinformations.EverInformations;

public abstract class AutoMessages {
	
	protected final EverInformations plugin;
	
	protected Task task;
	protected boolean running;
	
	protected boolean enable;
	protected int numero;
	
	public AutoMessages(final EverInformations plugin){
		this.plugin = plugin;
		
		this.running = false;
		
		this.enable = false;
		this.numero = 0;
	}
	
	protected abstract void reload();
	protected abstract void init();
	
	protected abstract void start();
	protected abstract void stop();
	
	protected abstract void next();
	protected abstract void view();
}
