<!-- <link rel="stylesheet" type="text/css" href="css/ajaxfileupload.css" /> -->

<div id="uploadDataFromFiles">
	<span class="floatright">
		<div class="uploadHead"><span class="textFont">Upload files</span></div> 
		<form name="form" action="" method="POST"
			enctype="multipart/form-data">
			<span class="textFont textBlack font14">Please select a file and click Upload button</span>
			<div>
				<span><input id="fileToUpload" type="file" size="45" name="fileToUpload" class="input"></span>
				<span>
					<button class="button uploadBtn textFont" id="buttonUpload" onclick="return ajaxFileUpload();">Upload</button>
					<img id="loading" src="img/loading.gif" class="hideBlock" />
				</span>
			</div>
		</form>
	</span>
</div>
<div id="uploadSingleItem" class="marginTop30"></div>

<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$('#uploadSingleItem').jtable({
			messages : {
				addNewRecord : 'Add New Item here'
			},
			paging : true, //Enable paging
			pageSize : 10, //Set page size (default: 10)
			sorting : true, //Enable sorting
			//   defaultSorting: 'Name ASC', //Set default sorting

			title : 'Upload Single Item',
			actions : {
				listAction : 'bsnet/item/listAll',
				createAction : 'bsnet/item/create',
				updateAction : '/bsnet/bsnet/item/update',
				deleteAction : '/bsnet/bsnet/item/delete'
			},
			fields : {
				_id : {
					key : true,
					create : false,
					edit : false,
					list : false
				},
				itemName : {
					title : "Item Name",
					width : "10%",
					edit  : false
				},
				description : {
					title : "Description",
					width : "20%"
				},
				price : {
					title : "Price",
					width : "10%"
				},
				category : {
					title : "Category",
					width : "10%"
				}
			}
		});
		$('#uploadSingleItem').jtable('load');
	});

	function ajaxFileUpload() {
		//starting setting some animation when the ajax starts and completes
		$("#loading").ajaxStart(function() {
			$("#buttonUpload").addClass("hideBlock");
			$(this).removeClass("hideBlock");
		}).ajaxComplete(function() {
			$(this).addClass("hideBlock");
			$("#buttonUpload").removeClass("hideBlock");
		});

		$.ajaxFileUpload({
			url : 'bsnet/item/uploadItems',
			secureuri : false,
			fileElementId : 'fileToUpload',
			success : function(data, status) {
				$('#uploadSingleItem').jtable('load');
			},
			error : function(data, status, e) {
				console.log("in error..."+e);
			}
		})
		return false;
	}
</script>