package io.moquette.server.netty;

import io.moquette.server.ServerAcceptor;
import io.moquette.server.config.IConfig;
import io.moquette.server.netty.BugSnagErrorsHandler;
import io.moquette.server.netty.MoquetteIdleTimeoutHandler;
import io.moquette.server.netty.NettyMQTTHandler;
import io.moquette.server.netty.metrics.BytesMetrics;
import io.moquette.server.netty.metrics.BytesMetricsCollector;
import io.moquette.server.netty.metrics.BytesMetricsHandler;
import io.moquette.server.netty.metrics.DropWizardMetricsHandler;
import io.moquette.server.netty.metrics.MQTTMessageLogger;
import io.moquette.server.netty.metrics.MessageMetrics;
import io.moquette.server.netty.metrics.MessageMetricsCollector;
import io.moquette.server.netty.metrics.MessageMetricsHandler;
import io.moquette.spi.impl.ProtocolProcessor;
import io.moquette.spi.security.ISslContextCreator;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Panxinmiao
 * moquette 中 MQTT消息的最大值
 */
public class NettyAcceptor implements ServerAcceptor {
    private static final String MQTT_SUBPROTOCOL_CSV_LIST = "mqtt, mqttv3.1, mqttv3.1.1";
    private static final Logger LOG = LoggerFactory.getLogger(NettyAcceptor.class);
    EventLoopGroup m_bossGroup;
    EventLoopGroup m_workerGroup;
    BytesMetricsCollector m_bytesMetricsCollector = new BytesMetricsCollector();
    MessageMetricsCollector m_metricsCollector = new MessageMetricsCollector();
    private Optional<? extends ChannelInboundHandler> metrics;
    private Optional<? extends ChannelInboundHandler> errorsCather;
    private int nettySoBacklog;
    private boolean nettySoReuseaddr;
    private boolean nettyTcpNodelay;
    private boolean nettySoKeepalive;
    private int nettyChannelTimeoutSeconds;
    private Class<? extends ServerSocketChannel> channelClass;
    private static final int MAX_PAYLOAD_SIZE = 256 * 1024 * 1024;

    public void initialize(ProtocolProcessor processor, IConfig props, ISslContextCreator sslCtxCreator)
            throws IOException {
        LOG.info("Initializing Netty acceptor...");
        this.nettySoBacklog = Integer.parseInt(props.getProperty("netty.so_backlog", "128"));
        this.nettySoReuseaddr = Boolean.parseBoolean(props.getProperty("netty.so_reuseaddr", "true"));
        this.nettyTcpNodelay = Boolean.parseBoolean(props.getProperty("netty.tcp_nodelay", "true"));
        this.nettySoKeepalive = Boolean.parseBoolean(props.getProperty("netty.so_keepalive", "true"));
        this.nettyChannelTimeoutSeconds = Integer.parseInt(props.getProperty("netty.channel_timeout.seconds", "10"));
        boolean epoll = Boolean.parseBoolean(props.getProperty("netty.epoll", "false"));
        if (epoll) {
            LOG.info("Netty is using Epoll");
            this.m_bossGroup = new EpollEventLoopGroup();
            this.m_workerGroup = new EpollEventLoopGroup();
            this.channelClass = EpollServerSocketChannel.class;
        } else {
            LOG.info("Netty is using NIO");
            this.m_bossGroup = new NioEventLoopGroup();
            this.m_workerGroup = new NioEventLoopGroup();
            this.channelClass = NioServerSocketChannel.class;
        }

        NettyMQTTHandler mqttHandler = new NettyMQTTHandler(processor);
        boolean useFineMetrics = Boolean.parseBoolean(props.getProperty("use_metrics", "false"));
        if (useFineMetrics) {
            DropWizardMetricsHandler useBugSnag = new DropWizardMetricsHandler();
            useBugSnag.init(props);
            this.metrics = Optional.of(useBugSnag);
        } else {
            this.metrics = Optional.empty();
        }

        boolean useBugSnag1 = Boolean.parseBoolean(props.getProperty("use_bugsnag", "false"));
        if (useBugSnag1) {
            BugSnagErrorsHandler sslTcpPortProp = new BugSnagErrorsHandler();
            sslTcpPortProp.init(props);
            this.errorsCather = Optional.of(sslTcpPortProp);
        } else {
            this.errorsCather = Optional.empty();
        }

        this.initializePlainTCPTransport(mqttHandler, props);
        this.initializeWebSocketTransport(mqttHandler, props);
        String sslTcpPortProp1 = props.getProperty("ssl_port");
        String wssPortProp = props.getProperty("secure_websocket_port");
        if (sslTcpPortProp1 != null || wssPortProp != null) {
            SSLContext sslContext = sslCtxCreator.initSSLContext();
            if (sslContext == null) {
                LOG.error("Can\'t initialize SSLHandler layer! Exiting, check your configuration of jks");
                return;
            }

            this.initializeSSLTCPTransport(mqttHandler, props, sslContext);
            this.initializeWSSTransport(mqttHandler, props, sslContext);
        }

    }

    private void initFactory(String host, int port, String protocol,
            final NettyAcceptor.PipelineInitializer pipeliner) {
        LOG.info("Initializing server. Protocol={}", protocol);
        ServerBootstrap b = new ServerBootstrap();
        ((ServerBootstrap) ((ServerBootstrap) ((ServerBootstrap) ((ServerBootstrap) b
                .group(this.m_bossGroup, this.m_workerGroup).channel(this.channelClass))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            public void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();

                                try {
                                    pipeliner.init(pipeline);
                                } catch (Throwable arg3) {
                                    NettyAcceptor.LOG.error("Severe error during pipeline creation", arg3);
                                    throw arg3;
                                }
                            }
                        }).option(ChannelOption.SO_BACKLOG, Integer.valueOf(this.nettySoBacklog)))
                                .option(ChannelOption.SO_REUSEADDR, Boolean.valueOf(this.nettySoReuseaddr)))
                                        .option(ChannelOption.TCP_NODELAY, Boolean.valueOf(this.nettyTcpNodelay)))
                                                .childOption(ChannelOption.SO_KEEPALIVE,
                                                        Boolean.valueOf(this.nettySoKeepalive));

        try {
            LOG.info("Binding server. host={}, port={}", host, Integer.valueOf(port));
            ChannelFuture ex = b.bind(host, port);
            LOG.info("Server has been bound. host={}, port={}", host, Integer.valueOf(port));
            ex.sync();
        } catch (InterruptedException arg6) {
            LOG.error("An interruptedException was caught while initializing server. Protocol={}", protocol, arg6);
        }

    }

    private void initializePlainTCPTransport(final NettyMQTTHandler handler, IConfig props) throws IOException {
        LOG.info("Configuring TCP MQTT transport");
        final MoquetteIdleTimeoutHandler timeoutHandler = new MoquetteIdleTimeoutHandler();
        String host = props.getProperty("host");
        String tcpPortProp = props.getProperty("port", "disabled");
        if ("disabled".equals(tcpPortProp)) {
            LOG.info("Property {} has been set to {}. TCP MQTT will be disabled", "port", "disabled");
        } else {
            int port = Integer.parseInt(tcpPortProp);
            this.initFactory(host, port, "TCP MQTT", new NettyAcceptor.PipelineInitializer() {
                void init(ChannelPipeline pipeline) {
                    pipeline.addFirst("idleStateHandler",
                            new IdleStateHandler(NettyAcceptor.this.nettyChannelTimeoutSeconds, 0, 0));
                    pipeline.addAfter("idleStateHandler", "idleEventHandler", timeoutHandler);
                    if (NettyAcceptor.this.errorsCather.isPresent()) {
                        pipeline.addLast("bugsnagCatcher", (ChannelHandler) NettyAcceptor.this.errorsCather.get());
                    }

                    pipeline.addFirst("bytemetrics",
                            new BytesMetricsHandler(NettyAcceptor.this.m_bytesMetricsCollector));
                    pipeline.addLast("decoder", new MqttDecoder(MAX_PAYLOAD_SIZE));
                    pipeline.addLast("encoder", MqttEncoder.INSTANCE);
                    pipeline.addLast("metrics", new MessageMetricsHandler(NettyAcceptor.this.m_metricsCollector));
                    pipeline.addLast("messageLogger", new MQTTMessageLogger());
                    if (NettyAcceptor.this.metrics.isPresent()) {
                        pipeline.addLast("wizardMetrics", (ChannelHandler) NettyAcceptor.this.metrics.get());
                    }

                    pipeline.addLast("handler", handler);
                }
            });
        }
    }

    private void initializeWebSocketTransport(final NettyMQTTHandler handler, IConfig props) throws IOException {
        LOG.info("Configuring Websocket MQTT transport");
        String webSocketPortProp = props.getProperty("websocket_port", "disabled");
        if ("disabled".equals(webSocketPortProp)) {
            LOG.info("Property {} has been setted to {}. Websocket MQTT will be disabled", "websocket_port",
                    "disabled");
        } else {
            int port = Integer.parseInt(webSocketPortProp);
            final MoquetteIdleTimeoutHandler timeoutHandler = new MoquetteIdleTimeoutHandler();
            String host = props.getProperty("host");
            this.initFactory(host, port, "Websocket MQTT", new NettyAcceptor.PipelineInitializer() {
                void init(ChannelPipeline pipeline) {
                    pipeline.addLast(new ChannelHandler[]{new HttpServerCodec()});
                    pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                    pipeline.addLast("webSocketHandler",
                            new WebSocketServerProtocolHandler("/mqtt", "mqtt, mqttv3.1, mqttv3.1.1"));
                    pipeline.addLast("ws2bytebufDecoder", new NettyAcceptor.WebSocketFrameToByteBufDecoder());
                    pipeline.addLast("bytebuf2wsEncoder", new NettyAcceptor.ByteBufToWebSocketFrameEncoder());
                    pipeline.addFirst("idleStateHandler",
                            new IdleStateHandler(NettyAcceptor.this.nettyChannelTimeoutSeconds, 0, 0));
                    pipeline.addAfter("idleStateHandler", "idleEventHandler", timeoutHandler);
                    pipeline.addFirst("bytemetrics",
                            new BytesMetricsHandler(NettyAcceptor.this.m_bytesMetricsCollector));
                    pipeline.addLast("decoder", new MqttDecoder(MAX_PAYLOAD_SIZE));
                    pipeline.addLast("encoder", MqttEncoder.INSTANCE);
                    pipeline.addLast("metrics", new MessageMetricsHandler(NettyAcceptor.this.m_metricsCollector));
                    pipeline.addLast("messageLogger", new MQTTMessageLogger());
                    pipeline.addLast("handler", handler);
                }
            });
        }
    }

    private void initializeSSLTCPTransport(final NettyMQTTHandler handler, IConfig props, final SSLContext sslContext)
            throws IOException {
        LOG.info("Configuring SSL MQTT transport");
        String sslPortProp = props.getProperty("ssl_port", "disabled");
        if ("disabled".equals(sslPortProp)) {
            LOG.info("Property {} has been set to {}. SSL MQTT will be disabled", "ssl_port", "disabled");
        } else {
            int sslPort = Integer.parseInt(sslPortProp);
            LOG.info("Starting SSL on port {}", Integer.valueOf(sslPort));
            final MoquetteIdleTimeoutHandler timeoutHandler = new MoquetteIdleTimeoutHandler();
            String host = props.getProperty("host");
            String sNeedsClientAuth = props.getProperty("need_client_auth", "false");
            final boolean needsClientAuth = Boolean.valueOf(sNeedsClientAuth).booleanValue();
            this.initFactory(host, sslPort, "SSL MQTT", new NettyAcceptor.PipelineInitializer() {
                void init(ChannelPipeline pipeline) throws Exception {
                    pipeline.addLast("ssl", NettyAcceptor.this.createSslHandler(sslContext, needsClientAuth));
                    pipeline.addFirst("idleStateHandler",
                            new IdleStateHandler(NettyAcceptor.this.nettyChannelTimeoutSeconds, 0, 0));
                    pipeline.addAfter("idleStateHandler", "idleEventHandler", timeoutHandler);
                    pipeline.addFirst("bytemetrics",
                            new BytesMetricsHandler(NettyAcceptor.this.m_bytesMetricsCollector));
                    pipeline.addLast("decoder", new MqttDecoder(MAX_PAYLOAD_SIZE));
                    pipeline.addLast("encoder", MqttEncoder.INSTANCE);
                    pipeline.addLast("metrics", new MessageMetricsHandler(NettyAcceptor.this.m_metricsCollector));
                    pipeline.addLast("messageLogger", new MQTTMessageLogger());
                    pipeline.addLast("handler", handler);
                }
            });
        }
    }

    private void initializeWSSTransport(final NettyMQTTHandler handler, IConfig props, final SSLContext sslContext)
            throws IOException {
        LOG.info("Configuring secure websocket MQTT transport");
        String sslPortProp = props.getProperty("secure_websocket_port", "disabled");
        if ("disabled".equals(sslPortProp)) {
            LOG.info("Property {} has been set to {}. Secure websocket MQTT will be disabled", "secure_websocket_port",
                    "disabled");
        } else {
            int sslPort = Integer.parseInt(sslPortProp);
            final MoquetteIdleTimeoutHandler timeoutHandler = new MoquetteIdleTimeoutHandler();
            String host = props.getProperty("host");
            String sNeedsClientAuth = props.getProperty("need_client_auth", "false");
            final boolean needsClientAuth = Boolean.valueOf(sNeedsClientAuth).booleanValue();
            this.initFactory(host, sslPort, "Secure websocket", new NettyAcceptor.PipelineInitializer() {
                void init(ChannelPipeline pipeline) throws Exception {
                    pipeline.addLast("ssl", NettyAcceptor.this.createSslHandler(sslContext, needsClientAuth));
                    pipeline.addLast("httpEncoder", new HttpResponseEncoder());
                    pipeline.addLast("httpDecoder", new HttpRequestDecoder());
                    pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                    pipeline.addLast("webSocketHandler",
                            new WebSocketServerProtocolHandler("/mqtt", "mqtt, mqttv3.1, mqttv3.1.1"));
                    pipeline.addLast("ws2bytebufDecoder", new NettyAcceptor.WebSocketFrameToByteBufDecoder());
                    pipeline.addLast("bytebuf2wsEncoder", new NettyAcceptor.ByteBufToWebSocketFrameEncoder());
                    pipeline.addFirst("idleStateHandler",
                            new IdleStateHandler(NettyAcceptor.this.nettyChannelTimeoutSeconds, 0, 0));
                    pipeline.addAfter("idleStateHandler", "idleEventHandler", timeoutHandler);
                    pipeline.addFirst("bytemetrics",
                            new BytesMetricsHandler(NettyAcceptor.this.m_bytesMetricsCollector));
                    pipeline.addLast("decoder", new MqttDecoder(MAX_PAYLOAD_SIZE));
                    pipeline.addLast("encoder", MqttEncoder.INSTANCE);
                    pipeline.addLast("metrics", new MessageMetricsHandler(NettyAcceptor.this.m_metricsCollector));
                    pipeline.addLast("messageLogger", new MQTTMessageLogger());
                    pipeline.addLast("handler", handler);
                }
            });
        }
    }

    public void close() {
        LOG.info("Closing Netty acceptor...");
        if (this.m_workerGroup != null && this.m_bossGroup != null) {
            Future<?> workerWaiter = this.m_workerGroup.shutdownGracefully();
            Future<?> bossWaiter = this.m_bossGroup.shutdownGracefully();
            LOG.info("Waiting for worker and boss event loop groups to terminate...");

            try {
                workerWaiter.await(10L, TimeUnit.SECONDS);
                bossWaiter.await(10L, TimeUnit.SECONDS);
            } catch (InterruptedException arg4) {
                LOG.warn("An InterruptedException was caught while waiting for event loops to terminate...");
            }

            if (!this.m_workerGroup.isTerminated()) {
                LOG.warn("Forcing shutdown of worker event loop...");
                this.m_workerGroup.shutdownGracefully(0L, 0L, TimeUnit.MILLISECONDS);
            }

            if (!this.m_bossGroup.isTerminated()) {
                LOG.warn("Forcing shutdown of boss event loop...");
                this.m_bossGroup.shutdownGracefully(0L, 0L, TimeUnit.MILLISECONDS);
            }

            LOG.info("Collecting message metrics...");
            MessageMetrics metrics = this.m_metricsCollector.computeMetrics();
            LOG.info("Metrics have been collected. Read messages={}, written messages={}",
                    Long.valueOf(metrics.messagesRead()), Long.valueOf(metrics.messagesWrote()));
            LOG.info("Collecting bytes metrics...");
            BytesMetrics bytesMetrics = this.m_bytesMetricsCollector.computeMetrics();
            LOG.info("Bytes metrics have been collected. Read bytes={}, written bytes={}",
                    Long.valueOf(bytesMetrics.readBytes()), Long.valueOf(bytesMetrics.wroteBytes()));
        } else {
            LOG.error("Netty acceptor is not initialized");
            throw new IllegalStateException("Invoked close on an Acceptor that wasn\'t initialized");
        }
    }

    private ChannelHandler createSslHandler(SSLContext sslContext, boolean needsClientAuth) {
        SSLEngine sslEngine = sslContext.createSSLEngine();
        sslEngine.setUseClientMode(false);
        if (needsClientAuth) {
            sslEngine.setNeedClientAuth(true);
        }

        return new SslHandler(sslEngine);
    }

    abstract class PipelineInitializer {
        abstract void init(ChannelPipeline arg0) throws Exception;
    }

    static class ByteBufToWebSocketFrameEncoder extends MessageToMessageEncoder<ByteBuf> {
        protected void encode(ChannelHandlerContext chc, ByteBuf bb, List<Object> out) throws Exception {
            BinaryWebSocketFrame result = new BinaryWebSocketFrame();
            result.content().writeBytes(bb);
            out.add(result);
        }
    }

    static class WebSocketFrameToByteBufDecoder extends MessageToMessageDecoder<BinaryWebSocketFrame> {
        protected void decode(ChannelHandlerContext chc, BinaryWebSocketFrame frame, List<Object> out)
                throws Exception {
            try {
              ByteBuf bb = frame.content();
              bb.retain();
              out.add(bb);
            } finally {
              frame.release();
            }
        }
    }
}