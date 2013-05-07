
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
			title : 'Buyer Organization users',
			actions : {
				listAction: 'bsnet/buyerItem/buyerUserlistAll',
				createAction : 'bsnet/buyerItem/createBuyerUser',
				updateAction: 'bsnet/buyerItem/buyerUserUpdate',
				deleteAction: 'bsnet/buyerItem/buyerUserDelete'
			},
			fields: {
				_id: {
					key: true,
                    create: false,
                    edit: false,
                    list: false
                },
				username: {
				   
                    title: "User Name",
					width:"20%",
						
					
				},

    			password: {
                   title: "Password",
	    		   width:"20%",
					   type:"password",
					   list:false
    	   		   
				},
			
				emailId: {
					title: "Email Id",
					width: "10%"
				},
				mobileNo: {
					title: "Mobile Number",
					width: "20%", 
				}
			}
		});
		$("#SupplierItemDetails").jtable('load');
	});
</script>