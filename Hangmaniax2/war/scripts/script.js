$('document').ready(function() {
	$signupDialog = $('div#signupDialog');
	$loginForm = $('form#loginForm');
	$userDetails = $('div#userDetails');
	$wordContainer = $('div#wordContainer');
	$tries = 7;
	
	// Check for browser session.
	// If not needed a simple refresh would serve as logout instead.
	sendJsonRPC('getActiveUser', {}, function(resp) {
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
	
	$resultDialog = $("<div />").css({'text-align': 'center'}).dialog({
		title: 'Game Over',
		modal: true,
		autoOpen: false,
		draggable: false,
		buttons: [{
			text: 'Ok',
			click: function() {
				$(this).dialog('close');
				resetGame();
			}
		}]
	});
	
	$letterContainer = $('<div />').attr('class', 'letterContainer').html('&nbsp;');
	
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
	
	//handle game start
	$('a#startGame').bind({
		'click': function(e) {
			e.preventDefault();
			sendJsonRPC('startGame', {}, onStartGame);
		}
	});
	
	//handle letter submition
	$('div#letterBox a').bind({
		'click': function(e) {
			e.preventDefault();
			var reqObj = {letter: $(this).text()};
			sendJsonRPC('letterSubmit', reqObj, onLetterSubmitted);
		}
	});
	
	//handle word input
	$('form#wordInForm').bind({
		'submit': function(e) {
			e.preventDefault();
			var reqObj = {word: this.word.value};
			sendJsonRPC('wordSubmit', reqObj, onWordAdded);
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
	
	function onStartGame(resp) {
		if (responseIsSuccessful(resp)) {
			$tries = 7;
			if (resp.result.length != undefined) {
				var letterCount = resp.result.length;
				$wordContainer.children('a#startGame').hide();
				for (var i=0; i<letterCount; i++) {
					$wordContainer.append($letterContainer.clone());
				}
			}
		} else {
			showError(resp.error.message);
		}
	};
	
	function onLetterSubmitted(resp) {
		if (responseIsSuccessful(resp)) {
			console.log(resp);
			if(typeof resp.result.occurrences != 'undefined' && resp.result.hit == true){
				for(var i = 0; i < resp.result.occurrences.length; i++){
					var $letter = $('.letterContainer:eq('+resp.result.occurrences[i]+')');
					$letter.html(resp.result.letter);
					if(resp.result.gameStatus == 'win'){
						showResult('W');
					}
				}
			} else if(resp.result.gameStatus == 'in progress' || resp.result.hit == false){
				$tries--;
				drawHangman($tries);
			}
		} else {
			showError(resp.error.message);
		}
	};
	
	function drawHangman($tries){
		var canvas = document.getElementById('hangmann');
		var ctx = canvas.getContext('2d');
		ctx.lineWidth = 3;
		ctx.strokeStyle = '#017B86';
		switch($tries)
		{
		case 6:
			ctx.beginPath();
			ctx.moveTo(290, 380);
			ctx.lineTo(290, 20);
			ctx.stroke();
			break;
		case 5:
			ctx.beginPath();
			ctx.moveTo(290, 20);
			ctx.lineTo(30, 20);
			ctx.stroke();
			break;
		case 4:
			ctx.beginPath();
			ctx.arc(160, 125, 35, 0, 2 * Math.PI, false);
			ctx.stroke();
			break;
		case 3:
			ctx.beginPath();
			ctx.moveTo(160, 160);
			ctx.lineTo(160, 260);
			ctx.lineTo(100, 320);
			ctx.moveTo(160, 260);
			ctx.lineTo(220, 320);
			ctx.stroke();
			break;
		case 2:
			ctx.beginPath();
			ctx.moveTo(160, 200);
			ctx.lineTo(100, 110);
			ctx.stroke();
			break;
		case 1:
			ctx.beginPath();
			ctx.moveTo(160, 200);
			ctx.lineTo(220, 110);
			ctx.stroke();
			break;
		case 0:
			ctx.beginPath();
			ctx.moveTo(160, 20);
			ctx.lineTo(160, 90);
			ctx.stroke();
			showResult('L');
			break;
		}
	};
	
	function resetGame(){
		$('.letterContainer').remove();
		var canvas = document.getElementById('hangmann');
		var ctx = canvas.getContext('2d');
		ctx.save();
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, canvas.width, canvas.height);
		ctx.restore();
		$('#startGame').show();
	};
	
	function onWordAdded(resp) {
		console.log(resp);
	};
	
	function onAjaxError(data) {
		console.log('AJAX error:');
		console.log(data);
	};
	
	function showError(errMsg) {
		$errorDialog.text(errMsg).dialog('open');
	};
	
	function showResult(resultMsg) {
		$resultDialog.css({'text-align': 'center', 'color': '#DD0B00', 'font-size': '50px', 'font-wight': '900'});
		$resultDialog.text(resultMsg).dialog('open');
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