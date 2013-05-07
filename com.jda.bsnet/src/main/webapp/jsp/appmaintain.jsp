<a href="/bsnet/metrics/threads"  target=_blank>Get Thread dump here. . .  :)</a> </br></br>
<div id="MetricsContainer"></div>

<script type="text/javascript">
	$(document).ready(function() {
		$('#MetricsContainer').jtable({
			title : 'Organizations for Approval',
			actions : {
				listAction : 'bsnet/user/getPendingOrgs',
				//updateAction : 'bsnet/user/approveOrgs1'
			},
			fields : {
				className : {
					//key : true,
					title : "className",
					width : "20%",
					
				},
					methodName : {
					title : "methodName",
					width : "10%",
					
				},
					
					avgResTime : {
					title : "avgResTime",
					width : "10%",
					
				},
					count : {
					title : "count",
					width : "10%",
					
				},

					oneMinRate : {
					title : "oneMinRate",
					width : "10%",
					
				},
					fiveMinRate : {
					title : "fiveMinRate",
					width : "10%",
					
				},

					meanRate : {
					title : "meanRate",
					width : "10%",
					
				},

			
			
				
			}
		});
		$('#MetricsContainer').jtable('load');
	});
</script>






