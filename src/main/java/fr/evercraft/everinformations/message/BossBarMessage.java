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
import java.util.Optional;
import java.util.regex.Pattern;

import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarOverlay;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.message.format.EFormatString;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.server.player.EPlayer;

public class BossBarMessage implements IMessage {
	
	// En Secondes
	private final double stay;
	private final double interval;
	
	private final String name;
	
	private final float percent;
	private final BossBarColor color;
	private final BossBarOverlay overlay;
	private final boolean darkenSky;
	private final boolean playEndBossMusic;
	private final boolean createFog;
	
	public BossBarMessage(double stay, double interval, String name, float percent, 
			BossBarColor color, BossBarOverlay overlay, boolean darkenSky, boolean playEndBossMusic, boolean createFog) {
		this.stay = stay;
		this.interval = interval;
		
		this.name = name;
		
		this.percent = percent/100;
		this.color = color;
		this.overlay = overlay;
		this.darkenSky = darkenSky;
		this.playEndBossMusic = playEndBossMusic;
		this.createFog = createFog;
	}

	@Override
	public long getNext() {
		return (long) ((this.stay + this.interval) * 1000);
	}
	
	public long getStay() {
		return (long) ((this.stay) * 1000);
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player) {
		return this.sendText(identifier, priority, player, EFormatString.of(this.name).toText(player.getReplaces()));
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, Text reason) {
		Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		replaces.putAll(player.getReplaces());
		replaces.put(Pattern.compile("\\{reason}"), EReplace.of(reason));
		
		return this.sendText(identifier, priority, player, EFormatString.of(this.name).toText(replaces));
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace) {
		return this.sendText(identifier, priority, player, EFormatString.of(this.name).toText(replace.getReplaces()));
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace, Text reason) {
		Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		replaces.putAll(replace.getReplaces());
		replaces.put(Pattern.compile("\\{reason}"), EReplace.of(reason));
		
		return this.sendText(identifier, priority, player, EFormatString.of(this.name).toText(replaces));
	}
	
	private boolean sendText(String identifier, int priority, EPlayer player, Text text) {
		
		Optional<ServerBossBar> bossbar = player.getBossBar(identifier);
		// Si le joueur à déjà une bossbar
		if (bossbar.isPresent()) {
			bossbar.get().setName(text);
			bossbar.get().setPercent(this.percent);
			bossbar.get().setColor(this.color);
			bossbar.get().setOverlay(this.overlay);
			bossbar.get().setDarkenSky(this.darkenSky);
			bossbar.get().setPlayEndBossMusic(this.playEndBossMusic);
			bossbar.get().setCreateFog(this.createFog);
			return true;
		} else {
			return player.sendBossBar(identifier, ServerBossBar.builder()
					.name(text)
					.percent(this.percent)
					.color(this.color)
					.overlay(this.overlay)
					.darkenSky(this.darkenSky)
					.playEndBossMusic(this.playEndBossMusic)
					.createFog(this.createFog)
					.build(),
					Optional.of(priority),
					Optional.of(this.getStay()));
		}
	}
	
	public boolean remove(String identifier, EPlayer player) {
		return player.removeBossBar(identifier);
	}

	@Override
	public String toString() {
		return "BossBarMessage [stay=" + stay + ", interval=" + interval + ", name="
				+ name + ", percent=" + percent + ", color=" + color
				+ ", overlay=" + overlay + ", darkenSky=" + darkenSky
				+ ", playEndBossMusic=" + playEndBossMusic + ", createFog="
				+ createFog + "]";
	}
}
