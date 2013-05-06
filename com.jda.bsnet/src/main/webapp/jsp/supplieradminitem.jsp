<link rel="stylesheet" type="text/css"
	href="jtable/themes/metro/blue/jtable.min.css" />

<div id="SupplierItemDetails"></div>

<script type="text/javascript" src="jtable/jquery.jtable.min.js"></script>

<script type="text/javascript">
	$(document).ready(function(){
		$("#SupplierItemDetails").jtable({
			messages: {
				addNewRecord: 'Add New Item'
			},
			title : 'Add Item to be Supplied',
			actions : {
				listAction: 'bsnet/ietms/getSupplietrItems',
				createAction : 'bsnet/item/saveSupplierItem'
			},
			fields : {
				itemName : {
					title : "Item Name",
					edit : false,
					width : "20%",
					function(data) {
						if (data.source == 'list') {
							//Return url all options for optimization.
							return 'bsnet/item/getItem?supp_id='+data.record.supp_id;
						}
						//This code runs when user opens edit/create form to create combobox.
						//data.source == 'edit' || data.source == 'create'
						return 'bsnet/ietm/getAllItems';
					}
				},
				Description : {
					title : "Description",
					width : "20%"				
				},
				Price : {
					title : "Price",
					width : "10%"				
				},
				Category : {
					title : "Category",
					width : "20%"
				}
			}
		});
		$('#SupplierItemDetails').jtable('load');
	});
</script>