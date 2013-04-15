package com.test.metrics.util;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.yammer.metrics.HealthChecks;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.HealthCheckRegistry;
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.Metric;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;
import com.yammer.metrics.reporting.ConsoleReporter;
import com.yammer.metrics.reporting.CsvReporter;
import com.yammer.metrics.reporting.GraphiteReporter;

public class MetricsUtils {
	private static final int DISPLAY_INTERVAL = 1;
	private static final String GRAPHITE = "graphite";
	private static final String CSV = "csv";
	private static final String CSV_REPORT_PATH = "log/metrics";
	private static final String IS_METRICS_ENABLED = "METRICS_ENABLED";
	private static final String METRICS_REPORT_FORMAT = "METRICS_REPORT_FORMAT";
	private static final String GRAPHITE_HOST = "GRAPHITE_HOST";
	private static final String GRAPHITE_PORT = "GRAPHITE_PORT";
	private static boolean isMetricsenabled = false;

	final static HealthCheckRegistry healthChecks = new HealthCheckRegistry();

	private static final long MEGABYTE = 1024L * 1024L;

	// per record measurement counters


	static {
		// Graphite Reporter
		GraphiteReporter.enable(DISPLAY_INTERVAL, TimeUnit.MINUTES,
				"10.104.206.76", 2003);

		// CSV Reporter
		File targetFileObj = new File("/log/metrics/");
		System.out.println("Abs path "+ targetFileObj.getAbsolutePath());
		if (!targetFileObj.exists()) {
			System.out.println("log/metrics... so creating ");
			boolean result = targetFileObj.mkdir();
			System.out.println("folder created :"+result);
		} else {
			// empty the directory
			final File[] files = targetFileObj.listFiles();
			for (File f : files)
				f.delete();
		}
		CsvReporter.enable(new File("/log/metrics/"), DISPLAY_INTERVAL,
				TimeUnit.MINUTES);

		// Console Reporter
		ConsoleReporter.enable(DISPLAY_INTERVAL, TimeUnit.MINUTES);

		//Register Health Check

		HealthChecks.defaultRegistry().register(new SiteUrlCheck("http://www.google.com"));

	}

	//Gauge Def

	public final static Gauge<Long> memoryOccu = Metrics.defaultRegistry()
			.newGauge(MetricsUtils.class, "jvmMemoryOccupied", new Gauge<Long>() {
				@Override
				public Long value() {
					Runtime runtime = Runtime.getRuntime();
					long memory = runtime.totalMemory() - runtime.freeMemory();
					return bytesToMegabytes(memory);
				}
			});


	//Counter Def
	public final static Counter primeCalcCounter = Metrics.defaultRegistry()
			.newCounter(MetricsUtils.class, "primeCalcCounter");
	// Meter Def
	public final static Meter primeCalcMeter = Metrics.defaultRegistry()
			.newMeter(MetricsUtils.class, "primeRequests", "requests", TimeUnit.SECONDS);
	//Histogram Def
	public final static Histogram primeCalcHistro = Metrics.defaultRegistry().newHistogram(MetricsUtils.class, "requestsHistro");

	//Timer Def
	public final static Timer primeCalcTimer = Metrics.defaultRegistry()
			.newTimer(MetricsUtils.class, "primeExecTimer",
					TimeUnit.MILLISECONDS, TimeUnit.SECONDS);

	public static long bytesToMegabytes(long bytes) {
		return bytes / MEGABYTE;
	}

	public static TimerContext startTimer(Timer argTimer) {
		if (argTimer != null) {
			return argTimer.time();
		}
		return null;
	}

	public static void stopTimer(TimerContext argTimerContext) {
		if (argTimerContext != null) {
			argTimerContext.stop();
		}
	}

	public static void printMetricsReport() {
		System.out.println("printing counters ...");
		ConsoleReporter cr = new ConsoleReporter(System.out);
		cr.run();
		Map<MetricName, Metric> metricMap = Metrics.defaultRegistry()
				.allMetrics();
		for (Metric m : metricMap.values()) {
			((Timer) m).clear();
		}

	}

}
