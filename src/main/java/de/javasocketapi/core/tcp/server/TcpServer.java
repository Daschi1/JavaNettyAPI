package de.javasocketapi.core.tcp.server;

import de.javasocketapi.core.handler.server.TcpServerPacketDecoder;
import de.javasocketapi.core.handler.server.TcpServerPacketEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TcpServer extends Thread {
    public static final boolean EPOLL_IS_AVAILABLE = Epoll.isAvailable();
    //    public static final List<Class<? extends Packet>> PACKETS_PLAY_OUT = Arrays.asList(PacketPlayInOutPing.class, PacketPlayOutTime.class);
//    public static final List<Class<? extends Packet>> PACKETS_PLAY_IN = Arrays.asList(PacketPlayInOutPing.class, PacketPlayInTime.class, PacketPlayInExit.class);
// TODO: 25.05.2020 register methods

    private final String hostname;
    private final int port;

    public TcpServer(final String hostname, final int port) {
        this.hostname = hostname;
        this.port = port;
        this.start();
    }

    @Override
    public void run() {
        super.run();
        final EventLoopGroup eventLoopGroup = TcpServer.EPOLL_IS_AVAILABLE ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        try {
            new ServerBootstrap().group(eventLoopGroup).channel(TcpServer.EPOLL_IS_AVAILABLE ? EpollServerSocketChannel.class : NioServerSocketChannel.class).childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(final Channel channel) throws Exception {
                    channel.pipeline().addLast(new TcpServerPacketDecoder()).addLast(new TcpServerPacketEncoder()).addLast(new TcpServerHandler());
                }
            }).bind(this.hostname, this.port).sync().channel().closeFuture().syncUninterruptibly();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
