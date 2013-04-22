<title>ARGO</title>

<link rel="stylesheet" type="text/css" href="jtable/themes/metro/blue/jtable.min.css" />
<div id="OrgTableContainer"></div>
<script type="text/javascript" src="jtable/jquery.jtable.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$('#OrgTableContainer').jtable({
			title : 'Organizations for Approval',
			actions : {
				listAction : 'bsnet/user/getPendingOrgs',
				updateAction : 'bsnet/user/approveOrgs1'
			},
			fields : {
				orgName : {
					title : "Organization",
					width : "20%"
				},
				buyer : {
					title : "Buyer",
					width : "10%",
					type : 'checkbox',
					values : {
						'false' : 'No',
						'true' : 'Yes'
					},
				},
				supplier : {
					title : "Supplier",
					width : "10%",
					type : 'checkbox',
					values : {
						'false' : 'No',
						'true' : 'Yes'
					},
				},
				approved : {
					title : "Status",
					width : "20%",
					type : 'checkbox',
					values : {
						'false' : 'Not Approved',
						'true' : 'Approved'
					},
				}
			}
		});
		$('#OrgTableContainer').jtable('load');
	});
</script>
