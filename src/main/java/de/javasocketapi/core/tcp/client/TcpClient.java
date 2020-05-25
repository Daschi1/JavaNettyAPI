package de.javasocketapi.core.tcp.client;

import de.javasocketapi.core.handler.client.TcpClientPacketDecoder;
import de.javasocketapi.core.handler.client.TcpClientPacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TcpClient extends Thread {

    public static final boolean EPOLL_IS_AVAILABLE = Epoll.isAvailable();
//    public static final List<Class<? extends Packet>> PACKETS_PLAY_OUT = Arrays.asList(PacketPlayOutInPing.class, PacketPlayOutTime.class, PacketPlayOutExit.class);
//    public static final List<Class<? extends Packet>> PACKETS_PLAY_IN = Arrays.asList(PacketPlayOutInPing.class, PacketPlayInTime.class);

    // TODO: 25.05.2020 register methods

    private final String hostname;
    private final int port;

    public TcpClient(final String hostname, final int port) {
        this.hostname = hostname;
        this.port = port;
        this.start();
    }

    @Override
    public void run() {
        super.run();
        final EventLoopGroup eventLoopGroup = TcpClient.EPOLL_IS_AVAILABLE ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        try {
            final Channel channel = new Bootstrap().group(eventLoopGroup).channel(TcpClient.EPOLL_IS_AVAILABLE ? EpollSocketChannel.class : NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(final Channel channel) throws Exception {
                    channel.pipeline().addLast(new TcpClientPacketDecoder()).addLast(new TcpClientPacketEncoder()).addLast(new TcpClientHandler());
                }
            }).connect(this.hostname, this.port).sync().channel();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                // TODO: 25.05.2020 packet queue
            }
        } catch (final InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
