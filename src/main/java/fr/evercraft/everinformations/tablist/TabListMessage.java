package fr.evercraft.everinformations.tablist;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;

import fr.evercraft.everapi.scoreboard.TypeScores;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.PriorityService;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.objective.EObjective;

public class TabListMessage extends EObjective {
	
	private final Set<TypeScores> scores;
	
	private final String header;
	private final String footer;
	
	public TabListMessage(EverInformations plugin, double stay, double update, String header, String footer) {
		super(plugin, stay, update);

		this.header = header;
		this.footer = footer;
		
		this.scores = Sets.newConcurrentHashSet();
		this.update = true;
		
		for(TypeScores score : TypeScores.values()) {
			if(this.header.contains("<" + score.name() + ">") || this.footer.contains("<" + score.name() + ">")) {
				this.scores.add(score);
				if(!score.isUpdate()) {
					this.update = false;
				}
			}
		}
	}
	
	/*
	 * Activation
	 */

	@Override
	public boolean start() {		
		for(TypeScores score : this.scores) {
			score.addListener(this.plugin, this);
		}
		return false;
	}

	@Override
	public boolean stop() {		
		for(TypeScores score : this.scores) {
			score.removeListener(this.plugin, this);
		}
		return true;
	}
	
	/*
	 * Player
	 */
	
	@Override
	public boolean add(int priority, EPlayer player) {
		if(player.sendTabList(ManagerTabList.IDENTIFIER, this.plugin.getTabList().getPriority())) {
			player.getTabList().setHeaderAndFooter(player.replaceVariable(this.header), player.replaceVariable(this.footer));
		}
		return true;
	}
	
	@Override
	public boolean remove(EPlayer player) {
		player.removeTabList(ManagerTabList.IDENTIFIER);
		return true;
	}
	
	/*
	 * Update 
	 */
	
	@Override
	public void update() {
		for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			if(player.sendTabList(ManagerTabList.IDENTIFIER, this.plugin.getTabList().getPriority())) {
				this.add(PriorityService.DEFAULT, player);
			}
		}
	}

	@Override
	public void update(TypeScores score) {
		this.update();
	}

	@Override
	public void update(UUID uuid, TypeScores score) {
		Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(uuid);
		if(player.isPresent() && player.get().sendTabList(ManagerTabList.IDENTIFIER, this.plugin.getTabList().getPriority())) {
			this.add(PriorityService.DEFAULT, player.get());
		}
	}

	@Override
	public String toString() {
		return "TabListMessage [scores=" + scores + ", header=" + header
				+ ", footer=" + footer + ", stay=" + stay + ", update_time="
				+ update_time + ", update=" + update + "]";
	}
	
}
