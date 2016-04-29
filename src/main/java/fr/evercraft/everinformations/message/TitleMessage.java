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

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsTick;

public class TitleMessage implements IMessage {
	
	// En Secondes
	private final double stay;
	private final double next;
	private final double fadeIn;
	private final double fadeOut;
	
	private final String title;
	private final String subTitle;

	public TitleMessage(double stay, double interval, double fadeIn, double fadeOut, String title, String subTitle) {
		this.stay = stay;
		this.next = interval;
		
		this.fadeIn = fadeIn;
		this.fadeOut = fadeOut;
		
		this.title = title;
		this.subTitle = subTitle;
	}

	@Override
	public long getNext() {
		return (long) ((this.next + this.stay) * 1000);
	}
	
	public int getStay() {
		return UtilsTick.parseSeconds(this.stay);
	}
	
	public int getFadeIn() {
		return UtilsTick.parseSeconds(this.fadeIn);
	}
	
	public int getFadeOut() {
		return UtilsTick.parseSeconds(this.fadeOut);
	}
	
	@Override
	public boolean send(int priority, EPlayer player) {
		return player.sendTitle(priority, Title.builder()
				.stay(this.getStay())
				.fadeIn(this.getFadeIn())
				.fadeOut(this.getFadeOut())
				.title(player.replaceVariable(this.title))
				.subtitle(player.replaceVariable(this.subTitle))
				.build());
	}
	
	@Override
	public boolean send(int priority, EPlayer player, Text reason) {
		return player.sendTitle(priority, Title.builder()
				.stay(this.getStay())
				.fadeIn(this.getFadeIn())
				.fadeOut(this.getFadeOut())
				.title(player.replaceVariable(this.title.replaceAll("<reason>", EChat.serialize(reason))))
				.subtitle(player.replaceVariable(this.subTitle.replaceAll("<reason>", EChat.serialize(reason))))
				.build());
	}
	
	@Override
	public boolean send(int priority, EPlayer player, EPlayer replace) {
		return player.sendTitle(priority, Title.builder()
				.stay(this.getStay())
				.fadeIn(this.getFadeIn())
				.fadeOut(this.getFadeOut())
				.title(replace.replaceVariable(this.title))
				.subtitle(replace.replaceVariable(this.subTitle))
				.build());
	}
	
	@Override
	public boolean send(int priority, EPlayer player, EPlayer replace, Text reason) {
		return player.sendTitle(priority, Title.builder()
				.stay(this.getStay())
				.fadeIn(this.getFadeIn())
				.fadeOut(this.getFadeOut())
				.title(replace.replaceVariable(this.title.replaceAll("<reason>", EChat.serialize(reason))))
				.subtitle(replace.replaceVariable(this.subTitle.replaceAll("<reason>", EChat.serialize(reason))))
				.build());
	}

	@Override
	public String toString() {
		return "TitleMessage [stay=" + stay + ", next=" + next
				+ ", fadeIn=" + fadeIn + ", fadeOut=" + fadeOut + ", title="
				+ title + ", subTitle=" + subTitle + "]";
	}
}
