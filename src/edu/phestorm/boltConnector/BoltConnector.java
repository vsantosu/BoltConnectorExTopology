package edu.phestorm.boltConnector;

import java.util.ArrayList;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class BoltConnector {

	private int port;
	private String host;
	private BoltConnectorReader reader;
	private boolean isConnected;
	private Channel writer;
	private ArrayList<ChannelHandlerContext> ctxList;

	public BoltConnector() {
		/* Initializing connection list */
		ctxList = new ArrayList<ChannelHandlerContext>();
		isConnected = false;
	}

	public void channelActive(final ChannelHandlerContext ctx) {

		System.out.println("New Connection! " + ctx.name());
		ctxList.add(ctx);
	}

	/* Listen Method (Server) */
	public void listen(int port) throws Exception {

		/* Validating object reader object */
		if (reader == null)
			throw new Exception("Reader object not set. Aborting.");

		/* Assigning the port */
		this.port = port;

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {

			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch)
								throws Exception {
							ch.pipeline().addLast(reader);
						}
					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			// Bind and start to accept incoming connections.
			ChannelFuture f;

			f = b.bind(port).sync();
			this.isConnected = f.isSuccess();
			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to
			// gracefully
			// shut down your server.
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}


	public void connect(String inHost, int inPort) {
		this.port = inPort;
		this.host = inHost;

		EventLoopGroup workerGroup = new NioEventLoopGroup();


			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(reader);
				}
			});

			/* Start the client. */
			ChannelFuture f;

			try {

				/* Connect to specific host */
				f = b.connect(host, port);
				
				if (!f.awaitUninterruptibly().isSuccess()) {
		            return;
		        }
				isConnected = f.isSuccess();
				/* Get Channel into writer */
				BoltConnector.this.writer = f.channel();
				/* Wait until the connection is closed. */
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


	}

	public void write(String msg) {
		
	  final ByteBuf buff = this.writer.alloc().buffer(msg.length());
	  buff.writeBytes(msg.getBytes());
	  this.writer.writeAndFlush(buff);
	  
	}

	public boolean isConnected() {
		return this.isConnected;
	}

	/******************************* Getters and setters **************************/
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the address
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param reader
	 *            the reader to set
	 */
	public BoltConnectorReader getReader() {
		return reader;
	}

	/**
	 * @param reader
	 *            the reader to set
	 */
	public void setReader(BoltConnectorReader reader) {
		this.reader = reader;
	}

}
