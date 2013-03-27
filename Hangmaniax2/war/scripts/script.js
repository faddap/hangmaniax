$('document').ready(function() {
	$signupDialog = $('div#signupDialog');
	$loginForm = $('form#loginForm');
	$userDetails = $('div#userDetails');
	
	// Check for browser session.
	// If not needed a simple refresh would serve as logout instead.
	sendJsonRPC('checkSession', {}, function(resp) {
		if (responseIsSuccessful(resp)) {
			onLoginSucceeded(resp);
		} else {
			
		}
	});
	
	$signupDialog.dialog({
		title: 'SignUp!',
		modal: true,
		autoOpen: false,
		draggable: false
	});
	
	$errorDialog = $("<div />").css({'text-align': 'center'}).dialog({
		title: 'Error!',
		modal: true,
		autoOpen: false,
		draggable: false,
		buttons: [{
			text: 'Ok',
			click: function() {
				$(this).dialog('close');
			}
		}]
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
	
	//handle logout
	$userDetails.children('a#logoutBtn').bind({
		'click': function(e) {
			e.preventDefault();
			sendJsonRPC('logout', {}, onLogoutSucceeded);
		}
	});
	
	function responseIsSuccessful(resp) {
		return resp.result != undefined && resp.result.success != undefined && resp.result.success === true;
	};
	
	function sendJsonRPC(method, params, successCallback, errorCallback) {
		var jsonObj = {method: method, params: params};
		var onErr = (errorCallback != undefined && jQuery.isFunction(errorCallback)) ? errorCallback : onAjaxError
		$.ajax({
			url: '/hangmaniax2',
			method: 'POST',
			dataType: 'json',
			data: {"jsonRPC": JSON.stringify(jsonObj)},
			success: successCallback,
			error: onErr,
			
			beforeSend: function() {
				mask();
			},
			complete: function() {
				mask(false);
			}
		});
	};
	
	function onLoginSucceeded(resp) {
		if (responseIsSuccessful(resp)) {
			var params = resp.result;
			$loginForm.hide();
			var usernameParam = $userDetails.children('span.username').text();
			var scoreParam = $userDetails.children('span.score').text();
			$userDetails.children('span.username').text(usernameParam.replace('{username}', params.name));
			$userDetails.children('span.score').text(scoreParam.replace('{score}', params.score));
			$userDetails.show();
		} else {
			showError("Wrong email and/or password!");
		}
	};
	
	function onSignupSucceeded(resp) {
		$signupDialog.dialog('close');
		console.log(resp);
	};
	
	function onLogoutSucceeded(resp) {
		if (responseIsSuccessful(resp)) {
			$userDetails.hide();
			$loginForm.show();
		}
	};
	
	function onAjaxError(data) {
		console.log('AJAX error:');
		console.log(data);
	};
	
	function showError(errMsg) {
		$errorDialog.text(errMsg).dialog('open');
	};
	
	function mask(openMask) {
		if (openMask == undefined || openMask == true) {
			$('<div id="mask" />')
				.css({
					width: '100%',
					height: '100%'
				})
				.attr('class', 'ui-widget-overlay ui-front')
				.appendTo($('body'));
		} else {
			$('div#mask').remove();
		}
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