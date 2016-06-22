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
