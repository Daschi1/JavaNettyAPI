package de.javasocketapi.core.server;

import de.javasocketapi.core.packet.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerHandler extends SimpleChannelInboundHandler<Packet> {

    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void sendToAllClients(final Packet packet) {
        for (final Channel channel : ServerHandler.CHANNEL_GROUP) {
            channel.writeAndFlush(packet, channel.voidPromise());
        }
    }

    public static void shutdownAllClients() {
        for (final Channel channel : ServerHandler.CHANNEL_GROUP) {
            channel.close();
        }
    }

    private Channel channel;

    @Override
    public void channelActive(final ChannelHandlerContext channelHandlerContext) throws Exception {
        this.channel = channelHandlerContext.channel();
        ServerHandler.CHANNEL_GROUP.add(this.channel);
        System.out.println("client connected -" + channelHandlerContext.toString());
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final Packet packet) throws Exception {
        System.out.println("Server received '" + packet.toString() + "' from "); // TODO: 31.05.2020 specific clients?
    }
}
