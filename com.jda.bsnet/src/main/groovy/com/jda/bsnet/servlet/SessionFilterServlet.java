package com.jda.bsnet.servlet;

import java.io.IOException;

import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class SessionFilterServlet implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String contextURL = request.getContextPath();
		String url = request.getRequestURI();
		boolean allowedRequest = false;
		try {
			if ((url.indexOf(contextURL+"user/create") != -1) || (url.indexOf(contextURL+"login/logon") != -1) || (url.indexOf(contextURL+"bsnet/index.html")!= -1)) {
				allowedRequest = true;
				chain.doFilter(req, res);
			}
			if (!allowedRequest) {
				HttpSession session = request.getSession(false);
				if (null == session) {
					response.sendRedirect(response.encodeRedirectURL(contextURL + "/index.html") );
				} else {
					chain.doFilter(req, res);
				}
			}
		} catch (IOException e) {
			PrintWriter writer = getWriter(response);
			if (writer == null)
				return;
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (ServletException e) {
			PrintWriter writer = getWriter(response);
			if (writer == null)
				return;
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public void init(FilterConfig config) throws ServletException {

	}

	private static PrintWriter getWriter(HttpServletResponse response) {
		try {
			return response.getWriter();
		} catch (IOException e) {
			return null;
		}
	}
}