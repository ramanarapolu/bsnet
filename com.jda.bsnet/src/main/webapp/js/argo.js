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
	});
	
	/* login submit function */
	$("#loginForm").submit(function() {
		$("#userNotPresentDiv").addClass("invisible");
		var loginData = {};
		$.each($('#loginForm').serializeArray(), function(i, field) {
			loginData[field.name] = field.value;
		});
		var formData = JSON.stringify(loginData);

		var request = $.ajax({
			url : "/bsnet/bsnet/login/logon",
			type : "POST",
			data : formData,
			contentType : "application/json; charset=utf-8",
			dataType : "json"
		});

		request.done(function(msg) {
			loginCallBack(msg);
		});

		request.fail(function(jqXHR, textStatus) {
			alert("Request failed: " + textStatus);
		});
		return false;
	});

	/* call back function in case of login success */
	function loginCallBack(result) {
		var errorString, loginSuccess, menulist;
		if(result.loginSuccess == true){
			menulist = result.menuList;
			populateMenuMap(menulist);
			showHomePage(menulist);
		} else {
			$("#userloginErrorDiv").html(result.errorString);
			$("#userloginErrorDiv").removeClass("invisible");
		}
	}
	
	/* toggle function to change the value of checkbox */
	$('input#org_buyer, #org_supplier').change(function() {
        if ($(this).is(':checked')) {
            $(this).val(true);
        } else {
            $(this).val("");
        }
		alert($(this).val());
	});
	
	/* Call on click of submit button in the registration page */
	$("#regisForm").submit(function() {
		var regisFormData = $("form").createRegisObject();
		alert(regisFormData);
		$.ajax({
			type : "POST",
			url : "/bsnet/bsnet/user/create",
			// The key needs to match your method's input parameter
			// (case-sensitive).
			data : regisFormData,
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			success : function(data) {
				alert(data);
				$("#userSuccess").removeClass("invisible");
			},
			error : function(errMsg) {
				alert(errMsg);
				$("#userError").removeClass("invisible");
			}
		});
		return false;
	});
	
	/* action to be invoked on click of the  menu */
	$(document).on("click", ".menuclick", function() {
		alert("hello");
		createBodyDiv();
	});
});

/* function to populate the menu details for future reference */
function populateMenuMap(menulist){
	$.each(menulist, function(i,val){
		userMenuMap.push({id:"#"+val.menuId, url: val.url});
		//userMenuMap["#"+val.menuId] = val.url;
	});
}

/* function to display the homepage on login success */
function showHomePage(menulist) {
	$("#argobody div").empty();
	var menus = [];
	var mId,pageURL,mname = '';
	
	$.each(menulist, function(i, val){
		mId = val.menuId;
		mname = val.displayName;
		menus.push({id: mId, name: mname});
	});
	createMenus(menus);
	createBodyDiv();
}

/* function to create the menus based on the list from server */
function createMenus(menus){
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
}

/* function to load the url in the div based on the current tab */
function createBodyDiv() {
	var id = $('#tabs').find('.ui-state-active').find("a").attr("href"); 
	var pageURL = '';
	$.each(userMenuMap,function(key, val){
		if(id == val.id){
			pageURL = val.url;
		}
	});
	$(id).load(pageURL);
	alert($(id).html());
}
