package practise.webfluxpractise.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyEchoServer {

    @SneakyThrows
    public static void main(String[] args) {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup(4);
        EventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(4);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            ServerBootstrap server = serverBootstrap.group(parentGroup,
                    childGroup) // accept 이벤트를 받는 parent 그룹, read 이벤트를 받는 child 그룹
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() { // accept 됐을 때 init 실행
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(
                            eventExecutorGroup, new LoggingHandler(LogLevel.INFO)
                        );
                        ch.pipeline().addLast(
                            new StringEncoder(),
                            new StringDecoder(),
                            new NettyEchoServerHandler()
                        );
                    }
                });
            server.bind(8080).sync()
                .addListener(new FutureListener<>() {
                    @Override
                    public void operationComplete(Future<Void> future) throws Exception {
                        if (future.isSuccess()) {
                            System.out.println("Server bound");
                        } else {
                            System.err.println("Bound attempt failed");
                            future.cause().printStackTrace();
                        }
                    }
                }).channel().closeFuture().sync();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
            eventExecutorGroup.shutdownGracefully();
        }
    }
}
