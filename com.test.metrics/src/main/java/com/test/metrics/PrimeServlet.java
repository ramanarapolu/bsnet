package com.test.metrics;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.test.metrics.util.MetricsUtils;
import com.yammer.metrics.core.TimerContext;

public class PrimeServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//counter
		MetricsUtils.primeCalcCounter.inc();

		//Meter
		MetricsUtils.primeCalcMeter.mark();

		//Histrogram

		MetricsUtils.primeCalcHistro.update(MetricsUtils.memoryOccu.value());

		//Timer
		TimerContext tc = MetricsUtils.startTimer(MetricsUtils.primeCalcTimer);


		double num = Double.parseDouble(request.getParameter("num"));
		System.out.println("Number :" + num + "  ISPrime :" +isPrime(num));
		response.getWriter().write("<html><body>GET response</body></html>");


		//stop timer
		MetricsUtils.stopTimer(tc);

	}

	private boolean isPrime(double num) {

		if (num <= 1)
			return false;
		if (num == 2)
			return true;
		if (num % 2 == 0)
			return false;
		double sRoot = Math.sqrt(num * 1.0);
		for (int i = 3; i <= sRoot; i += 2) {
			if (num % i == 0)
				return false;
		}
		return true;

	}
}
