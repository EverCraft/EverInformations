/*
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import fr.evercraft.everapi.message.format.EFormatString;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsTick;

public class TitleMessage implements IMessage {
	
	// En Secondes
	private final double stay;
	private final double interval;
	private final double fadeIn;
	private final double fadeOut;
	
	private final String title;
	private final String subTitle;

	public TitleMessage(double stay, double interval, double fadeIn, double fadeOut, String title, String subTitle) {
		this.stay = stay;
		this.interval = interval;
		
		this.fadeIn = fadeIn;
		this.fadeOut = fadeOut;
		
		this.title = title;
		this.subTitle = subTitle;
	}

	@Override
	public long getNext() {
		return (long) ((this.stay + this.interval) * 1000);
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
	public boolean send(String identifier, int priority, EPlayer player) {
		return player.sendTitle(identifier, priority, Title.builder()
				.stay(this.getStay())
				.fadeIn(this.getFadeIn())
				.fadeOut(this.getFadeOut())
				.title(EFormatString.of(this.title).toText(player.getReplaces()))
				.subtitle(EFormatString.of(this.subTitle).toText(player.getReplaces()))
				.build());
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, Text reason) {
		Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		replaces.putAll(player.getReplaces());
		replaces.put(Pattern.compile("<reason>"), EReplace.of(reason));
		
		return player.sendTitle(identifier, priority, Title.builder()
				.stay(this.getStay())
				.fadeIn(this.getFadeIn())
				.fadeOut(this.getFadeOut())
				.title(EFormatString.of(this.title).toText(replaces))
				.subtitle(EFormatString.of(this.subTitle).toText(replaces))
				.build());
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace) {
		return player.sendTitle(identifier, priority, Title.builder()
				.stay(this.getStay())
				.fadeIn(this.getFadeIn())
				.fadeOut(this.getFadeOut())
				.title(EFormatString.of(this.title).toText(replace.getReplaces()))
				.subtitle(EFormatString.of(this.subTitle).toText(replace.getReplaces()))
				.build());
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace, Text reason) {
		Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		replaces.putAll(replace.getReplaces());
		replaces.put(Pattern.compile("<reason>"), EReplace.of(reason));
		
		return player.sendTitle(identifier, priority, Title.builder()
				.stay(this.getStay())
				.fadeIn(this.getFadeIn())
				.fadeOut(this.getFadeOut())
				.title(EFormatString.of(this.title).toText(replaces))
				.subtitle(EFormatString.of(this.subTitle).toText(replaces))
				.build());
	}

	@Override
	public String toString() {
		return "TitleMessage [stay=" + stay + ", interval=" + interval
				+ ", fadeIn=" + fadeIn + ", fadeOut=" + fadeOut + ", title="
				+ title + ", subTitle=" + subTitle + "]";
	}
}
