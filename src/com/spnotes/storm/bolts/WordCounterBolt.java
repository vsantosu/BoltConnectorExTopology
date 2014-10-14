package com.spnotes.storm.bolts;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

import edu.phestorm.boltConnector.BoltConnector;
import edu.phestorm.boltConnector.BoltConnectorMessage;
import edu.phestorm.boltConnector.BoltConnectorReader;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

public class WordCounterBolt implements IRichBolt {

	Integer id;
	String name;
	Map<String, Integer> counters;
	private OutputCollector collector;

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.counters = new HashMap<String, Integer>();
		this.collector = collector;
		this.name = context.getThisComponentId();
		this.id = context.getThisTaskId();

	}

	@Override
	public void execute(Tuple input) {
		String str = input.getString(0);
		if (!counters.containsKey(str)) {
			counters.put(str, 1);
		} else {
			Integer c = counters.get(str) + 1;
			counters.put(str, c);
		}
		collector.ack(input);
	}

	@Override
	public void cleanup() {
		System.out.println(" -- Word Counter [" + name + "-" + id + "]");
		for (Map.Entry<String, Integer> entry : counters.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());

			/* Adding Connector Socket Here */
			sendViaSocket();
		}
	}

	public void sendViaSocket() {
		
		BoltConnector bcon = new BoltConnector();

		bcon.setReader(new BoltConnectorReader() {

			@Override
			public void read(ChannelHandlerContext conCtx,
					BoltConnectorMessage msg) {
			}

			@Override
			public void exceptionCatcher(Throwable cause) {
				// TODO Auto-generated method stub
			}
		});

		bcon.connect("localhost", 8000);

		if (bcon.isConnected())
		{
			for (Map.Entry<String, Integer> entry : counters.entrySet())
				bcon.write("<>"+entry.getKey() + " : " + entry.getValue());
		}
		else
			System.out.println("Client Not Connected!");

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
