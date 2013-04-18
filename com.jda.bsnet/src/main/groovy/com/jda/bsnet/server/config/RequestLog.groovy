package com.jda.bsnet.server.config

class RequestLog {
	String accessLogTimeZone
	boolean accessLogEnabled
	String accessLogFileName
	String accessLogDirectory
	boolean statsOn
	boolean accessLogAppend
	int accessLogRetainDays
	boolean accessLogServerName
	boolean accessLogExtended
	boolean accessLogEnableLatency
	boolean accessLogCookies
}