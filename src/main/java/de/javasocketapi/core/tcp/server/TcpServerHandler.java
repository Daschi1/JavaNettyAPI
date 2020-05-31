package de.javasocketapi.core.tcp.server;

import de.javasocketapi.core.packet.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class TcpServerHandler extends SimpleChannelInboundHandler<Packet> {

    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void sendToAllClients(final Packet packet) {
        for (final Channel channel : TcpServerHandler.CHANNEL_GROUP) {
            channel.writeAndFlush(packet, channel.voidPromise());
        }
    }

    public static void shutdownAllCLients() {
        for (final Channel channel : TcpServerHandler.CHANNEL_GROUP) {
            channel.close();
        }
    }

    private Channel channel;

    @Override
    public void channelActive(final ChannelHandlerContext channelHandlerContext) throws Exception {
        this.channel = channelHandlerContext.channel();
        TcpServerHandler.CHANNEL_GROUP.add(this.channel);
        System.out.println("client connected -" + channelHandlerContext.toString());
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final Packet packet) throws Exception {
        /*if (packet instanceof PacketPlayInOutPing) {
            this.channel.writeAndFlush(packet, this.channel.voidPromise());
        } else if (packet instanceof PacketPlayInTime) {
            this.channel.writeAndFlush(new PacketPlayOutTime(System.currentTimeMillis()), this.channel.voidPromise());
        } else if (packet instanceof PacketPlayInExit) {
            this.channel.close();
        }*/
        // TODO: 25.05.2020 packet calling via id
    }
}
