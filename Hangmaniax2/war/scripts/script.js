$('document').ready(function() {
	
	//handle POST submit from signIn form
	$('form#loginForm').bind({
		'submit': function(e) {
			e.preventDefault();
			var cr = {username: this.username.value, password: this.password.value};
			sendJsonRPC('login', cr, onLoginSucceeded);
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
	
	function onAjaxError(data) {
		console.log('AJAX error:');
		console.log(data);
	};
});