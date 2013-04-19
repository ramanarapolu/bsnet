<link rel="stylesheet" type="text/css" href="jtable/themes/metro/blue/jtable.min.css" />
hello
<div id="uploadDataFromFiles">
	<div class="jtable-toolbar">Upload files</div>
	<form action="bsnet/item/uploadItems" method="post" enctype="multipart/form-data">
       <p>
        Choose a file : <input type="file" name="file" />
       </p>
       <input type="submit" value="Upload" />
    </form>
</div>
<div id="uploadSingleItem"></div>

<script type="text/javascript" src="jtable/jquery.jtable.min.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	$('#uploadSingleItem').jtable({
		title : 'Organizations for Approval',
		actions : {
			createAction : 'bsnet/item/create'
		},
		fields : {
			itemName : {
				title : "Item Name",
				create : false,
				edit : false,
				width : "20%"
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
	$('#OrgTableContainer').jtable('load');
});
</script>