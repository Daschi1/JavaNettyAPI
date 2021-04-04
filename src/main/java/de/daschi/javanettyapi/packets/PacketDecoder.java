package de.daschi.javanettyapi.packets;

import de.daschi.javanettyapi.client.ClientSession;
import de.daschi.javanettyapi.core.Core;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(final ChannelHandlerContext channelHandlerContext, final ByteBuf byteBuf, final List<Object> out) throws Exception {
        if (!byteBuf.isReadable(Core.MAX_BYTE_LENGTH)){
            return;
        }
        byteBuf.markReaderIndex();
        int length = byteBuf.readInt() - Core.MAX_BYTE_LENGTH;

        if (byteBuf.readableBytes() < length){
            byteBuf.resetReaderIndex();
            return;
        }

        final Packet packet = Core.getPacketById(length).getDeclaredConstructor().newInstance();
        if (Core.isPacketRegistered(packet)){
            UUID uuid = null;
            if (ClientSession.getChannel() == null) {
                uuid = packet.readUuid(byteBuf);
            }
            packet.read(byteBuf);
            if (uuid == null) {
                // System.out.println("Received packet '" + packet.getClass().getSimpleName() + "'.");
                packet.clientReceived();
            } else {
                // System.out.println("Received packet '" + packet.getClass().getSimpleName() + "' from '" + uuid + "'.");
                packet.serverReceived(uuid);
            }
            out.add(packet);
        }
    }

}
