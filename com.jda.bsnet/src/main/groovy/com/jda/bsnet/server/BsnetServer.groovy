package com.jda.bsnet.server

import org.eclipse.jetty.server.Connector
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.NCSARequestLog
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.SessionManager
import org.eclipse.jetty.server.handler.ContextHandlerCollection
import org.eclipse.jetty.server.handler.HandlerCollection
import org.eclipse.jetty.server.handler.RequestLogHandler
import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.eclipse.jetty.server.session.AbstractSessionManager
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.server.ssl.SslSocketConnector
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.eclipse.jetty.webapp.WebAppContext
import org.mortbay.setuid.SetUIDServer

import com.jda.bsnet.server.config.BsnetServerConfig
import com.jda.bsnet.server.config.WebApp

class BsnetServer {
	BsnetServerConfig config
	private Server server

	BsnetServer(BsnetServerConfig argConfig){
		config = argConfig
	}

	public void init() {
		Server newServer = new Server(config
				.host.httpPort)
		Server setUidServer = createSetUidServer()
		if (setUidServer != null){
			newServer = setUidServer
		}
		QueuedThreadPool qtp = createThreadPool()
		newServer.setThreadPool(qtp)
		Connector connector = createHttpConnector()
		Connector sslConnector = createSslConnector()
		List<Connector> connectors = new ArrayList<Connector>()
		if (connector != null){
			connectors.add(connector)
		}
		if (sslConnector != null){
			connectors.add(sslConnector)
		}
		newServer.setConnectors(connectors as Connector[])
		List<Handler> handlers = createWebAppHandlers()
		ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection()
		contextHandlerCollection.setHandlers(handlers as Handler[])
		RequestLogHandler requestLogHandler = createRequestLogHander()
		if (requestLogHandler != null) {
			HandlerCollection handlerCollection = new HandlerCollection()
			handlerCollection.setHandlers([
				contextHandlerCollection,
				requestLogHandler
			] as Handler[])
			newServer.setHandler(handlerCollection)
		} else {
			newServer.setHandler(contextHandlerCollection)
		}
		if (newServer != null) {
			server = newServer
		}
	}

	private List<Handler> createWebAppHandlers() {
		List<Handler> handlers = new ArrayList<Handler>()
		config.webApps.each{WebApp webApp ->
			WebAppContext webAppContext = new WebAppContext()
			 webAppContext.setResourceBase(webApp.appDir)
			 webAppContext.setAliases(true)
			 webAppContext.setDescriptor(webApp.appDir
			 + "/WEB-INF/web.xml")
			 webAppContext.setContextPath(webApp.contextPath)
			 webAppContext.setParentLoaderPriority(true)
			 webAppContext.setVirtualHosts(webApp.hostNames)
			 webAppContext.setConnectorNames([
			 "httpConnector",
			 "httpsConnector"
			 ] as String[])
			 SessionHandler sessionHandler = webAppContext
			 .getSessionHandler()
			 SessionManager sessionManager = sessionHandler
			 .getSessionManager()
			 if (sessionManager instanceof AbstractSessionManager) {
			 AbstractSessionManager abstractSessionManager = (AbstractSessionManager) sessionManager
			 abstractSessionManager.setUsingCookies(true)
			 abstractSessionManager.setHttpOnly(true)
			 }

			handlers.add(webAppContext)
		}
		println handlers
		return handlers
	}

	private Connector createSslConnector() {
		SslSocketConnector sslConnector = null
		if (config.keystore
		.keystoreFile != null
		&& !config.keystore
		.keystoreFile.isEmpty()
		&& config.keystore
		.keystorePassword != null
		&& !config.keystore
		.keystorePassword.isEmpty()) {
			SslContextFactory sslContextFactory = new SslContextFactory()
			sslContextFactory.setKeyStorePath(this
					.getClass()
					.getResource(
					config.keystore
					.keystoreFile).toExternalForm())
			sslContextFactory.setKeyStorePassword(config
					.keystore.keystorePassword)
			if (config.cipherSuites
			.excluded != null) {
				if (config.cipherSuites.excluded.length > 0) {
					sslContextFactory.setExcludeCipherSuites(this
							.config.cipherSuites.excluded)
				}
			}
			sslConnector = new SslSocketConnector(sslContextFactory)
			if (config.host.hostName != null
			&& !config.host
			.hostName.isEmpty()) {
				sslConnector.setHost(config
						.host.hostName)
			}
			sslConnector.setPort(config.host
					.httpsPort)
			sslConnector.setAcceptors(config
					.threadPool.acceptorThreadSize)
			sslConnector.setAcceptQueueSize(config
					.threadPool.acceptorQueueSize)
			sslConnector.setRequestHeaderSize(config
					.httpHeaders.httpsRequestHeaderSize)
			sslConnector.setResponseHeaderSize(config
					.httpHeaders.httpsResponseHeaderSize)
			if (config.cipherSuites.included != null) {
				if (config.cipherSuites.included.length > 0) {
					sslContextFactory.setIncludeCipherSuites(this
							.config.cipherSuites
							.included)
				}
			}
			sslConnector.setName("httpsConnector")
		}
		return sslConnector
	}

	private QueuedThreadPool createThreadPool() {
		QueuedThreadPool qtp = new QueuedThreadPool()
		qtp.setMinThreads(config.threadPool.threadPoolSize)
		qtp.setMaxThreads(config.threadPool.threadPoolSize)
		return qtp
	}

	private Connector createHttpConnector() {
		SelectChannelConnector connector = new SelectChannelConnector()
		if (config.host.hostName != null
		&& !config.host
		.hostName.isEmpty()) {
			connector.setHost(config.host
					.hostName)
		}
		connector.setAcceptors(config.threadPool.acceptorThreadSize)
		connector.setAcceptQueueSize(config
				.threadPool.acceptorQueueSize)
		connector.setPort(config.host
				.httpPort)
		connector.setMaxIdleTime(config
				.threadPool.maxIdleTime)
		connector.setConfidentialPort(config
				.host.httpsPort)
		connector.setStatsOn(config
				.getRequestLog().statsOn)
		connector.setName("httpConnector")
		connector.setRequestHeaderSize(config
				.httpHeaders.httpRequestHeaderSize)
		connector.setResponseHeaderSize(config
				.httpHeaders.httpResponseHeaderSize)
		return connector
	}

	private Server createSetUidServer() {
		if (config.processOwner != null && this.validateProcessConfiguration()) {
			SetUIDServer setUidServer = new SetUIDServer()
			if (config.processOwner.getUserName() != null
			&& !config.processOwner
			.getUserName().isEmpty()) {
				setUidServer.setUsername(config
						.processOwner.getUserName())
			}
			if (config.processOwner
			.getGroupName() != null
			&& !config.processOwner
			.getGroupName().isEmpty()) {
				setUidServer.setGroupname(config
						.processOwner.getGroupName())
			}
			if (config.processOwner.getUmask() != null
			&& !config.processOwner
			.getUmask().isEmpty()) {
				setUidServer.setUmaskOctal(config
						.processOwner.getUmask())
			}
			setUidServer.setStartServerAsPrivileged(config
					.processOwner.isStartAsPrivileged())
			return setUidServer
		}
		return null
	}

	private void setHandlers(Server newServer,
			ContextHandlerCollection contextHandlerCollection,
			RequestLogHandler requestLogHandler) {
		if (requestLogHandler != null) {
			HandlerCollection handlerCollection = new HandlerCollection()
			handlerCollection.setHandlers([
				contextHandlerCollection,
				requestLogHandler
			])
			newServer.setHandler(handlerCollection)
		} else {
			newServer.setHandler(contextHandlerCollection)
		}
	}

	private RequestLogHandler createRequestLogHander() {
		RequestLogHandler requestLogHandler = null
		if (config.getRequestLog()
		.isAccessLogEnabled()) {
			requestLogHandler = new RequestLogHandler()
			NCSARequestLog requestLog = new NCSARequestLog(this
					.config.getRequestLog()
					.getAccessLogDirectory()
					+ "/"
					+ config.getRequestLog()
					.getAccessLogFileName())
			requestLog.setRetainDays(config
					.getRequestLog().getAccessLogRetainDays())
			requestLog.setAppend(config
					.getRequestLog().isAccessLogAppend())
			requestLog.setExtended(config
					.getRequestLog().isAccessLogExtended())
			requestLog.setLogTimeZone(config
					.getRequestLog().getAccessLogTimeZone())
			requestLog.setLogServer(config
					.getRequestLog().isAccessLogServerName())
			requestLog.setLogCookies(config
					.getRequestLog().isAccessLogCookies())
			requestLog.setLogLatency(config
					.getRequestLog().isAccessLogEnableLatency())
			requestLogHandler.setRequestLog(requestLog)
		}
		return requestLogHandler
	}

	private boolean validateProcessConfiguration() {
		return (config.processOwner.getUserName() != null && !this
		.config.processOwner.getUserName()
		.isEmpty()) || (config.processOwner
		.getGroupName() != null && !config
		.processOwner.getGroupName().isEmpty()) || (config.processOwner
		.getUmask() != null && !config
		.processOwner.getUmask().isEmpty())
	}

	/*
	 protected void createSSLCertificate() {
	 println 'Creating SSL Certificate...'
	 if (!keystoreFile.parentFile.exists() && !keystoreFile.parentFile.mkdir()) {
	 throw new RuntimeException("Unable to create keystore folder: $keystoreFile.parentFile.canonicalPath")
	 }
	 String[] keytoolArgs = [
	 '-genkey',
	 '-alias',
	 'localhost',
	 '-dname',
	 'CN=localhost,OU=Test,O=Test,C=US',
	 '-keyalg',
	 'RSA',
	 '-validity',
	 '365',
	 '-storepass',
	 'key',
	 '-keystore',
	 keystore,
	 '-storepass',
	 keyPassword,
	 '-keypass',
	 keyPassword
	 ]
	 getKeyToolClass().main keytoolArgs
	 println 'Created SSL Certificate.'
	 }
	 */
	protected Class<?> getKeyToolClass() {
		try {
			Class.forName 'sun.security.tools.KeyTool'
		}
		catch (ClassNotFoundException e) {
			// no try/catch for this one, if neither is found let it fail
			Class.forName 'com.ibm.crypto.tools.KeyTool'
		}
	}


	public void start() throws Exception {
		if (server != null){
			Runtime.addShutdownHook {
				println "Running shutdown hook..."
				stop()
			}
			server.start()
			//	server.join()
		}
	}

	public void stop() throws Exception {
		if (server != null){
			println "Stopping server..."
			server.stop()
			server.join()
		}
	}
}
