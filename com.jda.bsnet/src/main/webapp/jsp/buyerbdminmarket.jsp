<link rel="stylesheet" type="text/css" href="css/MetroJs.min.css">
<div>Please click on category to see the list of categories:</div>
<div id="navigationMenu"> 
	<form method="post" action='' id="navForm">
		<h4 class="menuLink category"><a href="#category" id="#category" class="links textWhite">Category</a></h4>

		<div id="category_div">
		</div>
		<h4 class="menuLink"><a href="#price" id="#price" class="links textWhite">Price Range</a></h4> 
		<div> 
			<ul class="ulstyle"> 
				<li>Min Price: <INPUT TYPE="text" NAME="minprice"></input></li>
				<li>Max Price: <INPUT TYPE="text" NAME="maxprice"></input></li>
				
			</ul>		
		</div>
	</form>
</div>
<div id="btnDiv">
	<button type="button" id="displayChoice" class="menuLink textWhite">Show</button>
	<button type="button" id="resetChoice" class="menuLink textWhite">Reset</button>
</div>

<div id="menuContent" class="floatLeft"></div>
<script type="text/javascript" src="js/MetroJs.min.js"></script>