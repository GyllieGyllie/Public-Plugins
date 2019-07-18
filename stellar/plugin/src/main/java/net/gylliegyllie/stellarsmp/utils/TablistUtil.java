package net.gylliegyllie.stellarsmp.utils;

import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class TablistUtil {

	public static void sendHeader(Player player, String header, String footer) {
		CraftPlayer craftPlayer = (CraftPlayer) player;
		PlayerConnection playerConnection = craftPlayer.getHandle().playerConnection;

		PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter();
		headerPacket.header = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}");
		headerPacket.footer = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");

		playerConnection.sendPacket(headerPacket);
	}

	public static void sendFooter(Player player, String footer) {
		CraftPlayer craftPlayer = (CraftPlayer) player;
		PlayerConnection playerConnection = craftPlayer.getHandle().playerConnection;

		IChatBaseComponent bottom = IChatBaseComponent.ChatSerializer.a("{text: '" + footer + "'}");

		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

		try {
			Field headerField = packet.getClass().getDeclaredField("b");
			headerField.setAccessible(true);
			headerField.set(packet, bottom);
			headerField.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		playerConnection.sendPacket(packet);
	}
}
