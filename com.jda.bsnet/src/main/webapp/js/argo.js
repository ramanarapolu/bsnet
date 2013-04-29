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
				$("#userSuccess").removeClass("invisible");
			},
			error : function(errMsg) {
				alert(errMsg);
				$("#userError").removeClass("invisible");
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

	/* Call on click of submit button in the registration page */
	$("#regisForm").submit(function() {
		argoJS.submitRegistration( );
		return false;
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
});
