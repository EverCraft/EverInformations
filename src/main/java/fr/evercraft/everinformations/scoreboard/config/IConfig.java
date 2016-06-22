package fr.evercraft.everinformations.scoreboard.config;

import java.util.List;

import fr.evercraft.everinformations.scoreboard.objective.EObjective;

public interface IConfig<T extends EObjective> {
	public boolean isEnable();
	public List<T> getObjectives();
}
