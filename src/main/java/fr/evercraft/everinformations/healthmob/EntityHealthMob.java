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

import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

public class EntityHealthMob {	

	private final Text name;
	private Task task;
	
	public EntityHealthMob(Optional<Text> name) {
		this.name = name.orElse(Text.EMPTY);
	}
	
	public EntityHealthMob() {
		this.name = Text.EMPTY;
	}

	/**
	 * Retourne le vrai nom du l'entité
	 * @return Le vrai nom du l'entité
	 */
	public Text getName() {
		return name;
	}
	
	/**
	 * Retourne la Task qui va supprimer le nom temporaire de l'entité
	 * @return La Task
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * Permet d'ajouter la Task qui va supprimer le nom temporaire de l'entité
	 * @param task La Task
	 */
	public void setTask(Task task) {
		this.task = task;
	}

	/**
	 * Annule la Task qui devait supprimer le nom temporaire de l'entité
	 * @return True si la Task a bien été annulé
	 */
	public boolean cancel() {
		if(this.task != null) {
			return this.task.cancel();
		}
		return false;
	}
	
	
}
