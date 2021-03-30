package de.daschi.javanettyapi.core;

import de.daschi.javanettyapi.packets.Packet;
import de.daschi.javanettyapi.packets.PacketID;
import de.daschi.javanettyapi.packets.client.PacketPlayOutClientRegistered;
import de.daschi.javanettyapi.packets.client.PacketPlayOutClientUnregistered;
import de.daschi.javanettyapi.packets.server.PacketPlayOutClientDisconnect;
import io.netty.channel.epoll.Epoll;
import org.reflections.Reflections;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Core {

    public static final boolean EPOLL_IS_AVAILABLE = Epoll.isAvailable();

    private static final List<Class<? extends Packet>> systemPackets = new ArrayList<>(Arrays.asList(PacketPlayOutClientRegistered.class, PacketPlayOutClientUnregistered.class, PacketPlayOutClientDisconnect.class));
    private static final List<Class<? extends Packet>> packets = new ArrayList<>();

    private static Class[] packetArray = new Class[0];

    static {
        Core.loadPackets("de.daschi.javanettyapi.packets.server");
        Core.loadPackets("de.daschi.javanettyapi.packets.client");
    }

    public static void loadPackets(final String packagePrefix) {
        final Reflections reflections = new Reflections(packagePrefix);
        final Set<Class<? extends Packet>> packets = reflections.getSubTypesOf(Packet.class);
        packets.forEach(packet -> {
            if (Core.systemPackets.contains(packet) || Core.getPacketId(packet) >= 0) {
                Core.packets.add(packet);
                return;
            }
            throw new JavaNettyAPIException("The packet '" + packet + "' is not allowed to have a packetID below zero.");
        });
        Core.packets.sort(Comparator.comparing(Core::getPacketId));
        packetArray = new Class[Core.packets.size()];
        AtomicInteger i = new AtomicInteger();
        Core.packets.forEach(aClass -> packetArray[i.getAndIncrement()] = aClass);
    }

    public static int getPacketId(final Class<? extends Packet> packet) {
        if (!packet.isAnnotationPresent(PacketID.class)) {
            throw new JavaNettyAPIException("Could not find the packetID for the packet '" + packet + "'.");
        }
        return packet.getAnnotation(PacketID.class).value();
    }

    public static Class<? extends Packet> getPacketById(int id) {
        if (id < packetArray.length){
            return (Class<? extends Packet>) packetArray[id + systemPackets.size()];
        }
        return null;
    }

    /*public static Class<? extends Packet> getPacketById(final int id) {
        Class<? extends Packet> aClass = null;
        for (final Class<? extends Packet> packet : Core.packets){
            if (Core.getPacketId(packet) == id){
                aClass = packet;
            }
        }
        if (aClass != null){
            return aClass;
        } else {
            throw new JavaNettyAPIException("Could not find the packet for the packetId '" + id + "'.");
        }
    }*/

}
