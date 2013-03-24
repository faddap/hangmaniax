$('document').ready(function() {
	$signupDialog = $('div#signupDialog');
	$signupDialog.dialog({
		title: 'SignUp!',
		modal: true,
		autoOpen: false,
		draggable: false
	});
	
	//handle POST submit from signIn form
	$('form#loginForm').bind({
		'submit': function(e) {
			e.preventDefault();
			var cr = {email: this.email.value, password: this.password.value};
			sendJsonRPC('login', cr, onLoginSucceeded);
		}
	});
	
	//handle POST submit from signUp form
	$('form#signupForm').bind({
		'submit': function(e) {
			e.preventDefault();
			var cr = {username: this.username.value, email: this.email.value, password: this.password.value};
			sendJsonRPC('signup', cr, onSignupSucceeded);
		}
	});
	
	function sendJsonRPC(method, params, successCallback, errorCallback) {
		var jsonObj = {method: method, params: params};
		var onErr = (errorCallback != undefined && jQuery.isFunction(errorCallback)) ? errorCallback : onAjaxError
		$.ajax({
			url: '/hangmaniax2',
			method: 'POST',
			dataType: 'json',
			data: {"jsonRPC": JSON.stringify(jsonObj)},
			success: successCallback,
			error: onErr
		});
	};
	
	function onLoginSucceeded(data) {
		console.log(data);
	};
	
	function onSignupSucceeded(data) {
		$signupDialog.dialog('close');
		console.log(data);
	};
	
	function onAjaxError(data) {
		console.log('AJAX error:');
		console.log(data);
	};
	
	//handle signUp menu click
	$('nav a#signup').click(function(e){
		e.preventDefault();
		$signupDialog.children('form').children('input[type=submit]').button({
			icons: {primary: 'ui-icon-gear'}
		}).css('margin-top', '10px');
		$signupDialog.dialog('open');
	});
	
	$("body").keydown(function(e) {
        var key = e.keyCode;
        $("#" + key).addClass("keyboardKeyDown");
	    }    
	);
	
	$("body").keyup(function(e) {
	        var key = e.keyCode;
	        $("#" + key).removeClass("keyboardKeyDown");
	    }    
	);
});