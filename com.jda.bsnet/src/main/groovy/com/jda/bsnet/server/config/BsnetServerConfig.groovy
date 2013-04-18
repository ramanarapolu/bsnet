package com.jda.bsnet.server.config


public class BsnetServerConfig {
	public static final String RECO_DB_URI = "mongodb://localhost:27017/bsnet"
	Host host
	List<WebApp> webApps
	Keystore keystore
	ProcessOwner processOwner
	ThreadPool threadPool
	RequestLog requestLog
	CipherSuites cipherSuites
	HttpHeaders httpHeaders
	MailConfig mailConfig
}
