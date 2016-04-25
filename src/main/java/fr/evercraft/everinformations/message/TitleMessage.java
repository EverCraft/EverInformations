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
package fr.evercraft.everinformations.message;

import org.spongepowered.api.text.title.Title;

import fr.evercraft.everapi.server.player.EPlayer;

public class TitleMessage  {
	private final int stay;
	private final int interval;
	
	private final int fadeIn;
	private final int fadeOut;
	
	private final String title;
	private final String subTitle;

	public TitleMessage(int stay, int interval, int fadeIn, int fadeOut, String title, String subTitle) {
		this.stay = stay;
		this.interval = interval;
		
		this.fadeIn = fadeIn;
		this.fadeOut = fadeOut;
		
		this.title = title;
		this.subTitle = subTitle;
	}

	public int getStay() {
		return this.stay;
	}

	public int getNext() {
		return this.interval;
	}
	
	public int getFadeIn() {
		return this.fadeIn;
	}

	public int getFadeOut() {
		return this.fadeOut;
	}

	public String getTitle() {
		return this.title;
	}
	
	public String getSubTitle() {
		return this.subTitle;
	}
	
	public boolean send(int priority, EPlayer player) {
		player.sendTitle(priority, Title.builder()
				.stay(this.stay)
				.fadeIn(this.fadeIn)
				.fadeOut(this.fadeOut)
				.title(player.replaceVariable(this.title))
				.subtitle(player.replaceVariable(this.subTitle))
				.build());
		return true;
	}
	
	public void send(int priority, EPlayer player, EPlayer replace) {
		player.sendTitle(priority, Title.builder()
				.stay(this.stay)
				.fadeIn(this.fadeIn)
				.fadeOut(this.fadeOut)
				.title(replace.replaceVariable(this.title))
				.subtitle(replace.replaceVariable(this.subTitle))
				.build());
	}

	@Override
	public String toString() {
		return "TitleMessage [stay=" + stay + ", interval=" + interval
				+ ", fadeIn=" + fadeIn + ", fadeOut=" + fadeOut + ", title="
				+ title + ", subTitle=" + subTitle + "]";
	}
}
