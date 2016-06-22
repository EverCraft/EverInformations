package fr.evercraft.everinformations.scoreboard.sidebar;

import org.spongepowered.api.text.Text;

public class SidebarTitle  {
	// En secondes
	private final double stay;
	
	private final Text title;
	
	public SidebarTitle(final double stay, Text title) {
		this.stay = stay;
		this.title = title;
	}

	public long getNext() {
		return (long) (this.stay * 1000);
	}
	
	public Text getTitle() {
		return this.title;
	}

	@Override
	public String toString() {
		return "SidebarTitle [stay=" + stay + ", title=" + title + "]";
	}
	
	
}
