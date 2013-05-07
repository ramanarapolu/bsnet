package com.jda.bsnet.util;

import com.jda.bsnet.rest.BuyerItemResource;
import com.jda.bsnet.rest.ItemResource
import com.jda.bsnet.rest.LoginResource
import com.jda.bsnet.rest.MarketplaceResource
import com.jda.bsnet.rest.SupplierItemResource
import com.jda.bsnet.rest.UserResource
import com.yammer.metrics.Metrics
import com.yammer.metrics.core.HealthCheckRegistry
import com.yammer.metrics.core.Metric
import com.yammer.metrics.core.MetricName
import com.yammer.metrics.core.Timer
import com.yammer.metrics.core.TimerContext
import com.yammer.metrics.reporting.ConsoleReporter

public class MetricsUtils {

	final static HealthCheckRegistry healthChecks = new HealthCheckRegistry();
	// per record measurement counters
	static {
		// Graphite Reporter
	}
	//login resource counters.
	public final static Timer logOnCounter = Metrics.defaultRegistry()
	.newTimer(LoginResource.class, "logOn");
	public final static Timer logOutCounter = Metrics.defaultRegistry()
	.newTimer(LoginResource.class, "logOut");

	//User Resource counters
	public final static Timer createOrgAndUserCounter = Metrics.defaultRegistry()
	.newTimer(UserResource.class, "createOrgAndUser");
	public final static Timer createUserCounter = Metrics.defaultRegistry()
	.newTimer(UserResource.class, "createUser");
	public final static Timer getPendingOrgsCounter = Metrics.defaultRegistry()
	.newTimer(UserResource.class, "getPendingOrgs");
	public final static Timer approveOrgsCounter = Metrics.defaultRegistry()
	.newTimer(UserResource.class, "approveOrgs");
	public final static Timer getUserByOrgCounter = Metrics.defaultRegistry()
	.newTimer(UserResource.class, "getUserByOrg");

	//Supplier Item Resources

	public final static Timer createSupItemCounter = Metrics.defaultRegistry()
	.newTimer(SupplierItemResource.class, "createSupItem");
	public final static Timer updateSupItemCounter = Metrics.defaultRegistry()
	.newTimer(SupplierItemResource.class, "updateSupItem");
	public final static Timer deleteSupItemCounter = Metrics.defaultRegistry()
	.newTimer(SupplierItemResource.class, "deleteSupItem");
	public final static Timer listAllCounter = Metrics.defaultRegistry()
	.newTimer(SupplierItemResource.class, "listAllCounter");
	public final static Timer getBSRelationStateCounter = Metrics.defaultRegistry()
	.newTimer(SupplierItemResource.class, "getBSRelationState");

	public final static Timer optionListCounter = Metrics.defaultRegistry()
	.newTimer(SupplierItemResource.class, "optionListCounter");

	public final static Timer requestBuyersCounter = Metrics.defaultRegistry()
	.newTimer(SupplierItemResource.class, "requestBuyer");


	//MerketPlace timers

	public final static Timer itemDetailsCounter = Metrics.defaultRegistry()
	.newTimer(MarketplaceResource.class, "itemDetails");
	public final static Timer categoryListCounter = Metrics.defaultRegistry()
	.newTimer(MarketplaceResource.class, "categoryList");
	public final static Timer getItemsCounter = Metrics.defaultRegistry()
	.newTimer(MarketplaceResource.class, "getItems");


	//Item resource timers
	public final static Timer createItemCounter = Metrics.defaultRegistry()
	.newTimer(ItemResource.class, "createItem");
	public final static Timer updateItemCounter = Metrics.defaultRegistry()
	.newTimer(ItemResource.class, "updateItem");
	public final static Timer deleteItemCounter = Metrics.defaultRegistry()
	.newTimer(ItemResource.class, "deleteItem");
	public final static Timer listAllItemCounter = Metrics.defaultRegistry()
	.newTimer(ItemResource.class, "listAllCounter");
	public final static Timer uploadItemsCounter = Metrics.defaultRegistry()
	.newTimer(ItemResource.class, "uploadItems");
	public final static Timer getSuppliersCounter = Metrics.defaultRegistry()
	.newTimer(ItemResource.class, "getSuppliers");


	// Buyer Item resources

	public final static Timer createBuyerUserCounter = Metrics.defaultRegistry()
	.newTimer(BuyerItemResource.class, " createBuyerUser");
	public final static Timer buyerUserlistAllCounter = Metrics.defaultRegistry()
	.newTimer(BuyerItemResource.class, "buyerUserlistAll");
	public final static Timer buyerUserUpdateCounter = Metrics.defaultRegistry()
	.newTimer(BuyerItemResource.class, "buyerUserUpdate");
	public final static Timer buyerUserDeleteCounter = Metrics.defaultRegistry()
	.newTimer(BuyerItemResource.class, "buyerUserDelete");
	public final static Timer storeBuyerItemsCounter = Metrics.defaultRegistry()
	.newTimer(BuyerItemResource.class, " storeBuyerItems");
	public final static Timer getBuyerItemsCounter = Metrics.defaultRegistry()
	.newTimer(BuyerItemResource.class, "getBuyerItems");
	public final static Timer approveCounter = Metrics.defaultRegistry()
	.newTimer(BuyerItemResource.class, "approve");
	public final static Timer buyerItemlistAllCounter = Metrics.defaultRegistry()
	.newTimer(BuyerItemResource.class, "buyerItemlistAll");
	public final static Timer buyerItemCreateCounter = Metrics.defaultRegistry()
	.newTimer(BuyerItemResource.class, "buyerItemCreate");

	public final static Timer buyerItemDeleteCounter = Metrics.defaultRegistry()
	.newTimer(BuyerItemResource.class, "buyerItemDelete");
	public final static Timer optionsListCounter = Metrics.defaultRegistry()
	.newTimer(BuyerItemResource.class, "optionsList");
	public final static Timer buyerItemSupplierListCounter = Metrics.defaultRegistry()
	.newTimer(BuyerItemResource.class, "buyerItemSupplierList");
	public final static Timer buyerItemSupplierCreateCounter = Metrics.defaultRegistry()
	.newTimer(BuyerItemResource.class, "buyerItemSupplierCreate");
	public final static Timer buyerItemSupplierDeleteCounter = Metrics.defaultRegistry()
	.newTimer(BuyerItemResource.class, "buyerItemSupplierDelete");
	public final static Timer optionsListSupplierCounter = Metrics.defaultRegistry()
	.newTimer(BuyerItemResource.class, "optionsListSupplier");




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
		ConsoleReporter cr = new ConsoleReporter(System.out);
		cr.run();
		Map<MetricName, Metric> metricMap = Metrics.defaultRegistry()
				.allMetrics();
		for (Metric m : metricMap.values()) {
			((Timer) m).clear();
		}

	}

}
