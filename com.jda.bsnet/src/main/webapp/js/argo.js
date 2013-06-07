var userMenuMap =[];
/* This method serializes the data in the form and creates a json object */
$.fn.serializeObject = function() {
	var o = {};
	var a = this.serializeArray();
	$.each(a, function() {
		if (o[this.name] !== undefined) {
			if (!o[this.name].push) {
				o[this.name] = [ o[this.name] ];
			}
			o[this.name].push(this.value || '');
		} else {
			o[this.name] = this.value || '';
		}
	});
	return o;
};

/* function to create the json object when a new user is created */
$.fn.createRegisObject = function(){
	var data = {};
	var addr = {};
	var org = {};
	var formdata = $("form").serializeArray();
	$.each(formdata, function(idx,element) {
		if(element.name.indexOf("addr_") >= 0){
			var name = element.name.replace("addr_","");
			name = name.trim();
			addr[name] = element.value;
		} else if(element.name.indexOf("org_") >= 0){
			var name = element.name.replace("org_","");
			name = name.trim();
			org[name] = element.value;
		} else{
			data[element.name] = element.value;
		}
	});
	org["address"] = addr;
	data["org"] = org;
	var jsonData =  JSON.stringify(data);
	return jsonData;
};

var argoJS  =   {
	/* common ajax call method */
	argoAjaxCall: function(url, data, success,error) {
		var request = $.ajax({
		url: url,
		type: "POST",
		data: data,
		contentType: "application/json; charset=utf-8",
		dataType: "json"
		});

		request.done(function(msg) {
			console.log(msg);
			eval("argoJS."+success + " (" + msg +") " );
		});

		request.fail(function(jqXHR, textStatus) {
			alert("Request failed: " + textStatus);
			eval("argoJS."+error+"("+textStatus+")");
		}); 
	},

	/* common error call back method */
	argoErrorCallBack: function(status){
		alert("Request failed: " + status);
	},

	/*  login page */
	submitLogin: function( ) {
		$("#userNotPresentDiv").addClass("invisible");
		var loginData = {};
		$.each($('#loginForm').serializeArray(), function(i, field) {
			loginData[field.name] = field.value;
		});
		var formData = JSON.stringify(loginData);
		//this.argoAjaxCall("/bsnet/bsnet/login/logon", formData, "loginCallBack", "argoErrorCallBack");
		var request = $.ajax({
			url: "/bsnet/bsnet/login/logon",
			type: "POST",
			data: formData,
			contentType: "application/json; charset=utf-8",
			dataType: "json"
		});

		request.done(function(msg) {
			argoJS.loginCallBack(msg);
			$("div#argofooter").empty( );
			$("div#argofooter").load("jsp/footer.jsp");
		});

		request.fail(function(jqXHR, textStatus) {
			argoJS.argoErrorCallBack(textStatus);
		}); 
	},

	/*  call back method on login success */
	loginCallBack: function(result) {
		var errorString, loginSuccess, menulist;
		if(result.loginSuccess == true) {
			menulist = result.menuList;
			argoJS.populateMenuMap(menulist);
			argoJS.showHomePage(menulist);
		} else {
			$("#userloginErrorDiv").html(result.errorString);
			$("#userloginErrorDiv").removeClass("invisible");
		}
	},

	/* registration page */
	submitRegistration: function( ){
		var regisFormData = $("form").createRegisObject();
		$.ajax({
			type: "POST",
			url: "/bsnet/bsnet/user/create",
			// The key needs to match your method's input parameter
			// (case-sensitive).
			data : regisFormData,
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			success : function(data) {
				argoJS.resetForm();
				if(data.success === false){
					$("#userError").empty().html(data.failedReason).removeClass("invisible");
				}else {
					$("#userSuccess").removeClass("invisible");
				}
			},
			error : function(errMsg) {
				alert(errMsg);
				$("#userError").removeClass("invisible");
			}
		});
	},
	
	/* reset form objects after submit */
	resetForm: function(){
		$('#regisForm').each(function(){
			this.reset();
		});
		$('#regisForm').find('input').each(function(){
			if($(this).hasClass('valid')){
				$(this).removeClass('valid');
			}
		});
		$('#regisForm').find('label').each(function(){
			if($(this).hasClass('valid')){
				$(this).removeClass('valid');
			}
		});
	},

	/* to populate the gloabl menu map variable */
	populateMenuMap: function(menulist){
		$.each(menulist, function(i,val){
			userMenuMap.push({id:"#"+val.menuId, url: val.url});
		});
	},

	/* function to display the home page on login success */
	showHomePage: function(menulist){
		$("#argobody div").empty();
		var menus = [];
		var mId,pageURL,mname = '';

		$.each(menulist, function(i, val){
			mId = val.menuId;
			mname = val.displayName;
			menus.push({id: mId, name: mname});
		});
		argoJS.createMenus(menus);
		argoJS.createBodyDiv();
	},

	/*  function to create menus from the list from response */
	createMenus: function(menus){
		var items = [];
		var list = $('<ul/>').appendTo('#tabs');
		$.each(menus, function(i, item) {
			items.push('<li><a href="#' + item.id + '" class="menuclick">' + item.name + '</a></li>');
		});
		list.append( items.join('') );
		$.each(menus, function(i, item) {
			var div=$('<div/>').attr("id",item.id);
			$("#tabs").append(div);
		});
		$("#argobody").html($("#adminHeader").html());
		$("#tabs").tabs();
	},

	/* loading the div body based on menu clicked */
	createBodyDiv: function(){
		var id = $('#tabs').find('.ui-state-active').find("a").attr("href");
		var pageURL = '';
		$.each(userMenuMap,function(key, val){
			if(id == val.id){
				pageURL = val.url;
			}
		});
		$(id).load(pageURL);
	},

	/* submitting requests from buyers  in supplier admin page */
	approveSelectedBuyers: function(checkedRows ){
		var selectedItems =[];
		$(checkedRows).each(function(){
			var singleRow ={};
			var record = $(this).data('record');
			singleRow["buyerName"] = record.buyerName;
			singleRow["item"] = record.item;
			singleRow["subscribed"] = record.subscribed;
			selectedItems.push(singleRow);
		});

		selectedItems = JSON.stringify(selectedItems);
		console.log("selected items" + selectedItems);
		var request = $.ajax({
			url: "/bsnet/bsnet/supplierItem/requestBuyers",
			type: "POST",
			data: selectedItems,
			contentType: "application/json; charset=utf-8",
			dataType: "json"
		});

		request.done(function(msg) {
			$("#SupplierBuyerRequests").jtable("reload");
		});

		request.fail(function(jqXHR, textStatus) {
			alert("Request failed: " + textStatus);
		});
	},

	/* create tile object in the market place */
	createTileObj: function(obj){
		$("#menuContent").append("<div id ='"+obj.item+"' data-mode='flip' data-delay='2000'></div>");
		$("#"+obj.item).addClass("blue").addClass("live-tile");

		$('<div/>',{id: obj.item+'_div1'}).appendTo('#'+obj.item);
		var img = $('<img />').attr({ 'id': 'img_'+obj.item, 'src':'/bsnet/img/products/'+obj.item+'.jpg', 'alt':obj.item}).appendTo($('#'+obj.item+'_div1'));
		$('#'+obj.item+'_div1').append('<span class="tile-title">'+obj.item+'</span>');

		$('<div/>',{id: obj.item+'_div2'}).appendTo('#'+obj.item);
		var img = $('<img />').attr({ 'id': 'img_'+obj.item, 'src': '/bsnet/css/images/1pixel.gif', 'alt':obj.item }).appendTo($('#'+obj.item+'_div2'));
		$('#'+obj.item+'_div2').append('<span class="tile-title"> Supplier: '+obj.orgName+'</span><br/>').append('<div class="font14"> Price: '+obj.promoPrice+'</div>').append('<div class="font14"> Delivery: '+obj.deliveryWindow+'</div>').append('<div class="font14"> Description: '+obj.description+'</div>');
	}
}

$(document).ready(function() {

	// load the html content
	var request = $.ajax({
		url : "/bsnet/html/argo.html",
		type : "GET",
		contentType : "text/html",
		dataType : "html"
	});

	request.done(function(msg) {
		$("#argohtmlcontent").html(msg);
		onloadFun();
	});

	/* login submit function */
	$("#loginForm").submit(function() {
		argoJS.submitLogin( );
		return false;
	});

	/* toggle function to change the value of checkbox */
	$('input#org_buyer, #org_supplier').change(function() {
		if ($(this).is(':checked')) {
			$(this).val(true);
		} else {
			$(this).val("");
		}
	});

	/* action to be invoked on click of the  menu */
	$(document).on("click", ".menuclick", function() {
		argoJS.createBodyDiv();
	});

	/*  action to be invoked on selecting some items in supplier admin - buyer requests page */
	$(document).on("click", "#approveBtn", function() {
		var checkedRows = $('#SupplierBuyerRequests').jtable('selectedRows');
		argoJS.approveSelectedBuyers(checkedRows );		
	});

	/* display categories from server */
	$(document).on("click",".category", function( ){
		$("#category_div").append("<ul class='ulstyle'></ul>");
		$.getJSON("/bsnet/bsnet/marketPlace/categoryList", function(data){
			console.log("categories" + data);
			$.each(data, function (key,val){
				$("#category_div ul").append('<li><input type="radio" name="'+val+'">'+val+'</input></li>');
			});
		});
	});
	
	/* on click of show button on market place */
	$(document).on("click", "#displayChoice", function() {
		$.ajax({
			url: "/bsnet/bsnet/marketPlace/getItems",
			type: "POST",
			dataType : "json",
			success : function(data) {
				$.each(data, function (idx,obj){
					console.log(obj);
					argoJS.createTileObj(obj);
				});
				$(".live-tile").not(".exclude").liveTile();
			},
			error : function(errMsg) {
				alert(errMsg);
				argoJS.argoErrorCallBack(errMsg);
			}
		});
	});

	/* on selection of a particular item */
	$(document).on("click",".live-tile", function(e){
		/* var sel_id = $(this).attr("id");
		var suppname =	($('#'+sel_id+'_div2').find('.tile-title').html()).split(':')[1];
		console.log(suppname);
		$("div#menuContent").empty( );
		$.getJSON("/bsnet/bsnet/marketPlace/itemDetails",{item: sel_id, orgName: suppname} function (resp){
			console.log(resp);
			$("#menuContent").append("<div id='sel_item'><span id='sel_img'></span><span><div>Description: "+item.description+"</div><div>Price: Rs."+item.price+"</div></span>");
			var img = $('<img />').attr({ 'id': 'img_'+item.itemName, 'src':'/bsnet/img/products/'+item.itemName+'.jpg', 'alt':item.itemName}).appendTo($('#sel_img'));
		}); */
		e.preventDefault( );
		$("#argoAlertDiv").html('<strong>Item has been added to cart.</strong><a class="argoClose" href="#">&times;</a>');		
		$(".argoAlert").removeClass("hideBlock");		
	});

	/* on click of reset button */
	$(document).on("click","#resetChoice", function( ){
		$("div#menuContent").empty( );
		$("div#category_div").empty( );
	});

	/* on click of close button */
	$(document).on("click",".argoClose", function(e){
		e.preventDefault( );
		$(".argoAlert").addClass("hideBlock");
	});
	
	/* on click of place order button */
	$(document).on("click","#placeOrder",function(e){
		e.preventDefault( );
		$("#argoAlertDiv").empty();
		$("#argoAlertDiv").html("<strong>Your order has been placed!!</strong><a class='argoClose' href='#'>&times;</a>");
		$(".argoAlert").removeClass("hideBlock");
	});
});
