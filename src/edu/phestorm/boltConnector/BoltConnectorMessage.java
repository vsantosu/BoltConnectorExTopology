package edu.phestorm.boltConnector;

import io.netty.buffer.ByteBuf;

public class BoltConnectorMessage {

	private Object data;
	
	public BoltConnectorMessage(Object data) {
		this.data = data;
	}
	
	public byte[] getBytes()
	{
        return getString().getBytes();
        
	}
	
	public String getString()
	{
		ByteBuf in = (ByteBuf) data;
		StringBuffer strBuff =  new StringBuffer();	
		
        while (in.isReadable()) {
            strBuff.append(((char) in.readByte()));     
        }
        
        return strBuff.toString();
        
	}
}
