package com.test.metrics.util;

import com.gargoylesoftware.htmlunit.WebClient;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.yammer.metrics.core.HealthCheck;

public class SiteUrlCheck extends HealthCheck{

	private String url = null;
	public SiteUrlCheck(String url) {
		super("urlCheck");
		this.url = url;
    }

	@Override
	public HealthCheck.Result check() throws Exception {
		WebClient webClient = new WebClient();
		HtmlPage currentPage = webClient.getPage(url);
		if (url!= null && currentPage != null) {
            return HealthCheck.Result.healthy();
        } else {
            return HealthCheck.Result.unhealthy("Cannot connect to " + url);
        }
    }

}
