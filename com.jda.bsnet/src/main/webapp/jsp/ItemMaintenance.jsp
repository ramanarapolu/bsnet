<link rel="stylesheet" type="text/css"
	href="jtable/themes/metro/blue/jtable.min.css" />
<link rel="stylesheet" type="text/css"
	href="css/ajaxfileupload.css" />

<div id="uploadDataFromFiles">
	<span class="floatright">
		<div class="uploadHead">
			<span class="textFont" >Upload files</span>
		</div> <img id="loading" src="img/loading.gif" style="display: none;">
		<form name="form" action="" method="POST"
			enctype="multipart/form-data">
			<table cellpadding="0" cellspacing="0" class="tableForm">
				<tbody>
					<tr>
						<td><input id="fileToUpload" type="file" size="45"
							name="fileToUpload" class="input"></td>
					</tr>
					<tr>
						<td>Please select a file and click Upload button</td>
					</tr>
				</tbody>
				<tfoot>
					<tr>
						<td><button class="button" id="buttonUpload"
								onclick="return ajaxFileUpload();">Upload</button></td>
					</tr>
				</tfoot>
			</table>
		</form>
	</span>
</div>
<div id="uploadSingleItem" class="marginTop80"></div>
<script type="text/javascript" src="jtable/jquery.jtable.min.js"></script>
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
					width : "10%"
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
			$(this).show();
		}).ajaxComplete(function() {
			$(this).hide();
		});

		/*
		    prepareing ajax file upload
		    url: the url of script file handling the uploaded files
		                fileElementId: the file type of input element id and it will be the index of  $_FILES Array()
		    dataType: it support json, xml
		    secureuri:use secure protocol
		    success: call back function when the ajax complete
		    error: callback function when the ajax failed

		 */
		$.ajaxFileUpload({
			url : 'bsnet/item/uploadItems',
			secureuri : false,
			fileElementId : 'fileToUpload',
			dataType : 'json',
			success : function(data, status) {
				if (typeof (data.error) != 'undefined') {
					if (data.error != '') {
						alert(data.error);
					} else {
						alert(data.msg);
					}
				}
			},
			error : function(data, status, e) {
				alert(e);
			}
		})
		return false;

	}
</script>