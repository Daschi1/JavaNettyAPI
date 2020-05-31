package de.javasocketapi.core.codec.client;

import de.javasocketapi.core.packet.Packet;
import de.javasocketapi.core.tcp.client.TcpClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TcpClientPacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> output) throws Exception {
        int id = byteBuf.readInt();
        Class<? extends Packet> packetClass = TcpClient.PACKETS_PLAY_IN.get(id); // TODO: 25.05.2020 make via id
        if (packetClass == null) {
            throw new IllegalArgumentException("PacketId '" + id + "' is not registerd.");
        }

        Packet packet = packetClass.newInstance();
        packet.receive(byteBuf);

        output.add(packet);
    }
}
