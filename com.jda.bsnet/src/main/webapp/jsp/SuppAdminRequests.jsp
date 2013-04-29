
<button id="approveBtn" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" role="button" aria-disabled="false"><span class="ui-button-text">Approve all selected records</span></button>
<div id="SupplierBuyerRequests" class="marginTop10"></div>


<script type="text/javascript">
	$(document).ready(function(){
		$("#SupplierBuyerRequests").jtable({
			paging: true, 
			selecting: true,
			multiselect: true,
			selectingCheckboxes: true,
			title : 'Approve Buyers',
			actions : {
				listAction: 'bsnet/supplierItem/getBSRelationState'
			},
			fields: {
				subscribed: {
					title: "Subscription Status",
					width: "20%",
					options: ["Not subscribed","Subscribed"]
				},
				buyerName: {
				   	create: true,
				    edit: false,
					list: true,
                    title: "Buyer",
					width:"20%"
				},
    			item: {
                   title: "Item",
	    		   width:"20%",
    	   		   create: false,
                   edit: false
				}				
			}
		});
		$("#SupplierBuyerRequests").jtable('load');
	});
</script>