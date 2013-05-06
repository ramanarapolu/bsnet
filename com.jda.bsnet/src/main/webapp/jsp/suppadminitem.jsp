<link rel="stylesheet" type="text/css" href="jtable/themes/metro/blue/jtable.min.css" />

<div id="SupplierItemDetails"></div>

<script type="text/javascript" src="jtable/jquery.jtable.min.js"></script>

<script type="text/javascript">
	$(document).ready(function(){
		$("#SupplierItemDetails").jtable({
			messages: {
				addNewRecord: 'Add New Item'
			},
			paging: true, 
			title : 'Add Item to be Supplied',
			actions : {
				listAction: 'bsnet/supplierItem/listAll',
				createAction : 'bsnet/supplierItem/create',
				updateAction: 'bsnet/supplierItem/update',
				deleteAction: 'bsnet/supplierItem/delete'
			},
			fields: {
				_id: {
					key: true,
                    create: false,
                    edit: false,
                    list: false
                },
				item: {
				   	create: true,
				    edit: false,
					list: false,
                    title: "Item Name",
					width:"20%",
					options: 'bsnet/supplierItem/optionsList'
				},
    			itemName: {
                   title: "Item Name",
	    		   width:"20%",
    	   		   create: false,
                   edit: false,
				},
				category: {
					title: "Category",
					width: "10%",
					edit: false,
					create: false
				},			
				listprice: {
					title: "Price",
					width: "10%"
				},
				deliveryWindow: {
					title: "Delivery Window",
					width: "20%", 
				}
			}
		});
		$("#SupplierItemDetails").jtable('load');
	});
</script>