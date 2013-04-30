package com.jda.bsnet.util

import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class JsoupUtils {
	private static final int MAX_RETRY_COUNT = -1
	private static final int DEFAULT_TIMEOUT = 60*1000

	public static Document getContent(String requestUrl){
		/*int tryCount = 0
		while(MAX_RETRY_COUNT == -1 || (MAX_RETRY_COUNT != -1 && tryCount < MAX_RETRY_COUNT)){
			++tryCount
			try {
				return Jsoup.connect(requestUrl).timeout(DEFAULT_TIMEOUT).get()
			}catch(ConnectException | SocketTimeoutException e){
				println "retrying ${requestUrl}"
			}catch(Exception e){
				// println "unexpectedexceptionon requestUrl: ${requestUrl} Exception: ${e.getClass().getName()} : ${e.getMessage()}"
			}
		}
		*/return Jsoup.connect(requestUrl).ignoreContentType(true).timeout(DEFAULT_TIMEOUT).get()
	}
}
