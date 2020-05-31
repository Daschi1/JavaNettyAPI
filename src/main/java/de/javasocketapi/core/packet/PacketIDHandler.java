package de.javasocketapi.core.packet;

import de.javasocketapi.core.codec.CodecException;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class PacketIDHandler {

    private static final List<Class<? extends Packet>> packets = new ArrayList<>();

    static {
        final Set<Class<? extends Packet>> packets = Reflections.collect().getSubTypesOf(Packet.class);
        packets.forEach(packet -> {
            if (!packet.isAnnotationPresent(PacketID.class)) {
                throw new CodecException("Could not find the PacketID for the packet '" + packet.toString() + "'. Is the annotation present?");
            }
        });
        PacketIDHandler.packets.sort(Comparator.comparing(packet -> packet.getAnnotation(PacketID.class).id()));
    }

    public static Class<? extends Packet> getPacketClassFromId(final int id) throws CodecException {
        return PacketIDHandler.packets.get(id);
    }

    public static int getIdFromPacketClass(final Class<? extends Packet> packet) throws CodecException {
        return packet.getAnnotation(PacketID.class).id();
    }
}
