package de.daschi.javanettyapi.core;

import de.daschi.javanettyapi.packets.Packet;
import de.daschi.javanettyapi.packets.PacketID;
import de.daschi.javanettyapi.packets.client.PacketPlayOutClientRegistered;
import de.daschi.javanettyapi.packets.client.PacketPlayOutClientUnregistered;
import de.daschi.javanettyapi.packets.server.PacketPlayOutClientDisconnect;
import io.netty.channel.epoll.Epoll;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Core {

    public static final boolean EPOLL_IS_AVAILABLE = Epoll.isAvailable();

    private static final List<Class<? extends Packet>> systemPackets = new ArrayList<>(Arrays.asList(PacketPlayOutClientRegistered.class, PacketPlayOutClientUnregistered.class, PacketPlayOutClientDisconnect.class));
    private static final List<Class<? extends Packet>> packets = new ArrayList<>();

    static {
        Core.loadPackets();
    }

    private static void loadPackets() {
        final Reflections reflections = new Reflections();
        final Set<Class<? extends Packet>> packets = reflections.getSubTypesOf(Packet.class);
        for (final Class<? extends Packet> packet : packets) {
            if (Core.systemPackets.contains(packet) || Core.getPacketId(packet) >= 0) {
                Core.packets.add(packet);
                continue;
            }
            throw new JavaNettyAPIException("The packet '" + packet + "' is not allowed to have a packetID below zero.");
        }
    }

    public static int getPacketId(final Class<? extends Packet> packet) {
        if (!packet.isAnnotationPresent(PacketID.class)) {
            throw new JavaNettyAPIException("Could not find the packetID for the packet '" + packet + "'.");
        }
        return packet.getAnnotation(PacketID.class).value();
    }

    public static Class<? extends Packet> getPacketById(final int id) {
        for (final Class<? extends Packet> packet : Core.packets) {
            if (Core.getPacketId(packet) == id) {
                return packet;
            }
        }
        throw new JavaNettyAPIException("Could not find the packet for the packetId '" + id + "'.");
    }

}
