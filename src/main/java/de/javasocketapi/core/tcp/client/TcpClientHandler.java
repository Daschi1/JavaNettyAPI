package de.javasocketapi.core.tcp.client;

import de.javasocketapi.core.packet.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.text.SimpleDateFormat;

public class TcpClientHandler extends SimpleChannelInboundHandler<Packet> {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final Packet packet) throws Exception {
        /*if (packet instanceof PacketPlayOutInPing) {
            System.out.println("Ping is: " + (System.currentTimeMillis() - ((PacketPlayOutInPing) packet).getTime()) + "ns");
        } else if (packet instanceof PacketPlayInTime) {
            System.out.println("Time is: " + SIMPLE_DATE_FORMAT.format(new Date(((PacketPlayInTime) packet).getTime())));
        }*/
        // TODO: 25.05.2020 packet calling via id
    }
}
