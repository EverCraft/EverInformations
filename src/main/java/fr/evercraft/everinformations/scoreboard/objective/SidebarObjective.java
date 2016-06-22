package fr.evercraft.everinformations.scoreboard.objective;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.scheduler.Task;

import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.sidebar.SidebarTitle;

public abstract class SidebarObjective extends EObjective {
	public static enum Type {
		NUMBERS,
		INFORMATIONS,
		ECONOMY,
		STATS;
    }
	
	protected final Type type;
	
	protected final List<SidebarTitle> titles;

	private Task task;

	private int numero;

	
	public SidebarObjective(final EverInformations plugin, final double stay, final double update, Type type, List<SidebarTitle> titles) {
		super(plugin, stay, update);
		this.type = type;
		this.titles = titles;
		
		this.numero = 0;
	}
	
	@Override
	public long getNext() {
		return (long) (this.stay * 1000);
	}
	
	protected abstract boolean subStart();
	protected abstract boolean subStop();
	protected abstract void updateTitle();
	
	public boolean start() {
		this.subStart();
		
		if(this.titles.size() > 1) {
			this.task();
		}
		
		return true;
	}

	public boolean stop() {
		// Si start
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
		this.subStop();
		
		return true;
	}
	
	public void task() {
		SidebarTitle title = this.getSidebarTitle();
		
		// Si il n'y a pas de délai
		if(title.getNext() == 0) {
			this.next();
		// Il y a un délai
		} else {
			this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
							.execute(() -> this.next())
							.async()
							.delay(title.getNext(), TimeUnit.MILLISECONDS)
							.name("SidebarObjective : SidebarTitle")
							.submit(this.plugin);
		}
	}
	
	public void next() {
		this.numero++;
		if(this.numero >= this.titles.size()){
			this.numero = 0;
		}
		this.updateTitle();
		this.task();
	}

	public SidebarTitle getSidebarTitle() {
		return this.titles.get(this.numero);
	}
	
	@Override
	public String toString() {
		return "SidebarObjective [type=" + type + ", titles=" + titles
				+ ", stay=" + stay + ", update=" + update_time + "]";
	}

}
