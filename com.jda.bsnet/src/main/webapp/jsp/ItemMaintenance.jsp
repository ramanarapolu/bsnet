<link rel="stylesheet" type="text/css" href="jtable/themes/metro/blue/jtable.min.css" />
<div id="uploadDataFromFiles">
	<span>
		<div class="uploadHead">
			<span class="textFont">Upload files</span>
		</div>
		<form action="bsnet/item/uploadItems" method="post"
			enctype="multipart/form-data">
			<span class="floatRight">
				<span>
					<input class="textFont uploadWidth" type="file" name="file" />
					<input type="submit" value="Upload" class="uploadBtn textFont font14" />
				</span>
			</span>
		</form>
</div>
<div id="uploadSingleItem" class="marginTop80"></div>
<script type="text/javascript" src="jtable/jquery.jtable.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$('#uploadSingleItem').jtable({
			messages: {
				addNewRecord: 'Add New Item'
			},
			title : 'Upload Single Item',
			actions : {
				listAction: 'bsnet/item/listAll',
				createAction : 'bsnet/item/create',
				updateAction: '/bsnet/bsnet/item/update',
				deleteAction: '/bsnet/bsnet/item/delete'
			},
			fields : {
				_id: {
					key: true,
					create: false,
					edit: false,
					list: false
                },
				itemName : {
					title : "Item Name",
					width : "10%"
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
					width : "10%"
				}
			}
		});
		$('#uploadSingleItem').jtable('load');
	});
</script>