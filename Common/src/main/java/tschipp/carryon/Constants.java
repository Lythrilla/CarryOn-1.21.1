/*
 * GNU Lesser General Public License v3
 * Copyright (C) 2024 Tschipp
 * mrtschipp@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package tschipp.carryon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tschipp.carryon.common.config.CarryConfig;
import tschipp.carryon.platform.Services;

public class Constants {

	public static final String MOD_ID = "carryon";
	public static final String MOD_NAME = "Carry On";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static final CarryConfig.Common COMMON_CONFIG = new CarryConfig.Common();
	public static final CarryConfig.Client CLIENT_CONFIG = new CarryConfig.Client();

	public static final ResourceLocation PACKET_ID_KEY_PRESSED =  ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "key_pressed");
	public static final ResourceLocation PACKET_ID_START_RIDING =  ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "start_riding");
	public static final ResourceLocation PACKET_ID_SYNC_SCRIPTS =  ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "sync_scripts");
	public static final ResourceLocation PACKET_ID_START_RIDING_OTHER =  ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "start_riding_other");
	public static final ResourceLocation PACKET_ID_SYNC_CARRY_ON_DATA =  ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "sync_carry_data");

	// ---- NBT conflict detection ----

	/**
	 * Mods that are known to mixin into CompoundTag and may potentially
	 * cause NullPointerException by inserting null keys.
	 */
	private static final String[][] NBT_CONFLICT_SUSPECTS = {
		// {modId, modDisplayName}
		{"kubejs", "KubeJS"},
		{"compactmachines", "Compact Machines"},
		{"accessories", "Accessories"},
		{"owo", "o\u03c9o"},
		{"create", "Create"},
		{"supplementaries", "Supplementaries"},
		{"evilcraft", "EvilCraft"},
		{"occultism", "Occultism"},
		{"industrialforegoing", "Industrial Foregoing"},
	};

	private static boolean nbtConflictWarned = false;

	/**
	 * Detects which loaded mods may be causing CompoundTag conflicts.
	 * Returns a comma-separated string of mod names, or "Unknown" if none matched.
	 */
	public static String detectNbtConflictingMods() {
		StringBuilder sb = new StringBuilder();
		for (String[] suspect : NBT_CONFLICT_SUSPECTS) {
			try {
				if (Services.PLATFORM.isModLoaded(suspect[0])) {
					if (sb.length() > 0) sb.append(", ");
					sb.append(suspect[1]).append(" (").append(suspect[0]).append(")");
				}
			} catch (Exception ignored) {}
		}
		return sb.length() > 0 ? sb.toString() : "Unknown (no known suspects loaded)";
	}

	/**
	 * Safely converts a CompoundTag to its string representation.
	 * Some mods may insert null keys into CompoundTag, causing the default
	 * toString() to throw a NullPointerException during internal key sorting.
	 * On first failure, logs the conflicting mods and notifies the player via chat.
	 */
	public static String safeTagToString(CompoundTag tag, ServerPlayer notifyPlayer) {
		if (tag == null)
			return "{}";
		try {
			return tag.toString();
		} catch (Exception e) {
			if (!nbtConflictWarned) {
				nbtConflictWarned = true;
				String conflicting = detectNbtConflictingMods();
				LOG.error("[Carry On] CompoundTag.toString() failed — likely caused by null keys " +
						"injected by another mod. Suspected conflicting mods: {}", conflicting, e);

				if (notifyPlayer != null) {
					try {
						notifyPlayer.sendSystemMessage(Component.literal(
								"\u00a7c[Carry On] \u00a7eNBT conflict detected! " +
								"The following mods may be causing issues: \u00a7c" + conflicting +
								"\u00a7e. The game will continue, but slowdown calculation may be inaccurate."));
					} catch (Exception ignored) {}
				}
			}
			return "{size=" + tag.size() + "}";
		}
	}

	/**
	 * Overload for contexts where no player is available for notification.
	 */
	public static String safeTagToString(CompoundTag tag) {
		return safeTagToString(tag, null);
	}

}