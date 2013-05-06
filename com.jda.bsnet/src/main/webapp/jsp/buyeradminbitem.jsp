<link rel="stylesheet" type="text/css" href="jtable/themes/metro/blue/jtable.min.css" />

<div id="BuyerItemDetails"></div>

<script type="text/javascript" src="jtable/jquery.jtable.min.js"></script>

<script type="text/javascript">
	$(document).ready(function(){
		$("#BuyerItemDetails").jtable({
			messages: {
				addNewRecord: 'Add New Item'
			},
			paging: true, 
			title : 'Buyer Items',
			//	openChildAsAccordion: true, 
			actions : {
				listAction: 'bsnet/buyerItem/buyerItemlistAll',
				createAction : 'bsnet/buyerItem/buyerItemCreate',
//				updateAction: 'bsnet/supplierItem/update',
				deleteAction: 'bsnet/buyerItem/buyerItemDelete'
			},
			fields: {
				_id: {
					key: true,
                    create: false,
                    edit: false,
                    list: false
                },
// Child table
 Phones: {
                    title: '',
                    width: '1%',
                    sorting: false,
                    edit: false,
                    create: false,
                    display: function (studentData) {
                        //Create an image that will be used to open child table
                        var $img = $('<img src="img/list_metro.png" title="Manage Suppliers" />');
                        //Open child table when user clicks the image
                        $img.click(function () {
                            $('#BuyerItemDetails').jtable('openChildTable',
                                    $img.closest('tr'),
                                    {
                                        title: studentData.record.item + ' - Suppliers',
                                        actions: {
                                            listAction: 'bsnet/buyerItem/buyerItemSupplierList?item=' + studentData.record.item,
                                            deleteAction: 'bsnet/buyerItem/buyerItemSupplierDelete',
                                          //  updateAction: '/Demo/UpdatePhone',
                                            createAction: 'bsnet/buyerItem/buyerItemSupplierCreate'
                                        },
                                        fields: {
                                            item: {
                                                type: 'hidden',
                                                defaultValue: studentData.record.item
                                            },
											_id: {
												title: 'Supplier Name',
                                                key: true,
                                                create: false,
                                                edit: false,
                                                list: false
                                            },
							
                                            supplier: {
												title: 'Supplier Name',
                                                //key: true,
                                                create: false,
                                                //edit: false,
                                                list: true
                                            },
											supplierAdd: {
													create: true,
													edit: false,
													list: false,
													title: "Supplier Name",
													width:"20%",
													options: 'bsnet/buyerItem/optionsListSupplier?item='+ studentData.record.item
												}
		                                        }
                                    }, function (data) { //opened handler
                                        data.childTable.jtable('load');
                                    });
                        });
                        //Return image to show on the person row
                        return $img;
                    }
                },

// Child table

				itemName: {
				   	create: true,
				    edit: false,
					list: false,
                    title: "Item Name",
					width:"20%",
					options: 'bsnet/buyerItem/optionsList'
				},

				  item: {
                   title: "Item Name",
	    		   width:"20%",
    	   		   create: false,
                   edit: false,
				}



    			
				
			}
		});
		$("#BuyerItemDetails").jtable('load');
	});
</script>