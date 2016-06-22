package fr.evercraft.everinformations.scoreboard.objective;

import fr.evercraft.everapi.scoreboard.IObjective;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;

public abstract class EObjective implements IObjective {
	
	protected final EverInformations plugin;
	
	// En secondes
	protected final double stay;
	protected final double update_time;
	
	protected boolean update;
	
	public EObjective(EverInformations plugin, double stay, double update) {
		this.plugin = plugin;
		this.stay = stay;
		this.update_time = update;
	}
	
	/**
	 * En Millisecondes
	 * @return
	 */
	public long getNext() {
		return (long) (this.stay * 1000);
	}
	
	/**
	 * En Millisecondes
	 * @return
	 */
	public long getUpdate() {
		return (long) (this.update_time * 1000);
	}

	public boolean isUpdate() {
		return this.update;
	}
	
	public abstract boolean add(int priority, EPlayer player);
	public abstract boolean remove(EPlayer player);
	
	public abstract boolean start();
	public abstract boolean stop();

	public abstract void update();
}
