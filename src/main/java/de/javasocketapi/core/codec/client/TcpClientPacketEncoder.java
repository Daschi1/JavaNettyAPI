package de.javasocketapi.core.codec.client;

import de.javasocketapi.core.packet.Packet;
import de.javasocketapi.core.tcp.client.TcpClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TcpClientPacketEncoder extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(final ChannelHandlerContext channelHandlerContext, final Packet packet, final ByteBuf output) throws Exception {
        final int id = TcpClient.PACKETS_PLAY_OUT.indexOf(packet.getClass()); // TODO: 25.05.2020 make via id
        if (id == -1) {
            throw new IllegalArgumentException("Packet '" + packet.getClass().getSimpleName() + "' is not registerd.");
        }

        output.writeInt(id);
        packet.write(output);
    }
}
