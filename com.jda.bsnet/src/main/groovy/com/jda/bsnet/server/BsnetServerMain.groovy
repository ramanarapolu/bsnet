package com.jda.bsnet.server

import com.jda.bsnet.rest.BsnetDatabase;
import com.jda.bsnet.server.config.BsnetServerConfig;
import com.jda.bsnet.util.JsonUtils


class BsnetServerMain {
	public static void main(String[] args) {
		Properties p = BsnetDatabase.getInstance().getBsnetProp()
		BsnetServer server = new BsnetServer(JsonUtils.readFromJsonFile(p.getProperty("bsnet.jettyjson.loc"), BsnetServerConfig.class))
		server.init()
		try {
			println "Starting Jetty Web Application..."

			server.start()
			println "BSNET Server Started."
			while (System.in.available() == 0) {
				Thread.sleep(5*1000)
			}
			//server.stop()
		} catch (Exception e) {
			e.printStackTrace()
			System.exit(-1)
		}
	}
}
