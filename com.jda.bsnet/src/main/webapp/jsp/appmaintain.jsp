<a href="/bsnet/metrics/threads"  target=_blank>Get Thread dump here. . .  :)</a> </br></br>
<div id="MetricsContainer"></div>

<script type="text/javascript">
	$(document).ready(function() {
		$('#MetricsContainer').jtable({
			title : 'App Metrics',
			actions : {
				listAction : 'bsnet/login/getResourceStats',
				//updateAction : 'bsnet/user/approveOrgs1'
			},
			fields : {
				className : {
					//key : true,
					title : "Class Name",
					width : "20%",
					
				},
					methodName : {
					title : "Method Name",
					width : "10%",
					
				},
					
					avgResTime : {
					title : "Avg ResTime",
					width : "10%",
					
				},
					count : {
					title : "Count",
					width : "10%",
					
				},

					oneMinRate : {
					title : "One MinRate",
					width : "10%",
					
				},
					fiveMinRate : {
					title : "Five MinRate",
					width : "10%",
					
				},

					meanRate : {
					title : "Mean Rate",
					width : "10%",
					
				},

			
			
				
			}
		});
		$('#MetricsContainer').jtable('load');
	});
</script>






