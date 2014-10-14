package edu.phestorm.boltConnector;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public abstract  class BoltConnectorReader extends ChannelInboundHandlerAdapter {
	
	
	public abstract void read(ChannelHandlerContext conCtx, BoltConnectorMessage msg);
	public abstract void exceptionCatcher(Throwable cause);
	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        try
        {
        	
        	read(ctx, new BoltConnectorMessage(msg));
        }
        finally{
        	ReferenceCountUtil.release(msg);
        }
    }

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
    	
        exceptionCatcher(cause);
        ctx.close();
    }

}
