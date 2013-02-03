%{--
  - Copyright (c) 2013. the original author or authors:
  -
  -    Ken Siprell (ken.siprell@gmail.com)
  -
  - Licensed under the Apache License, Version 2.0 (the "License");
  - you may not use this file except in compliance with the License.
  - You may obtain a copy of the License at
  -
  -      http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  --}%

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title>Atmosphere Test</title>
	<r:require module="atmosphere2"/>
	<r:layoutResources/>
	<style>
	#chat-window {
		height: 200px;
		width: 500px;
		border: 1px solid black;
		margin-top: 10px;
	}
	#chat-input {
		width: 500px;
		margin-top: 10px;
	}
	</style>
</head>

<body>
<h1>Chat</h1>
<button id="chat-subscribe">Subscribe</button>
<div id="chat-window"></div>
<input id="chat-input" type="text"/>

<h1>Notification</h1>
<button id="notification-subscribe">Subscribe</button>
<button id="notification-send">Send</button>
<p id="notification"></p>

<h1>Public</h1>
<p>After clicking the Subscribe and Trigger buttons, the paragraph below should update every 5 seconds for 100 times.</p>
<p>Updated at <span id="public-update"></span>.</p>
<button id="public-subscribe">Subscribe</button>
<button id="public-trigger">Trigger</button>

<h1>Unsubscribe</h1>
<p>Clicking the button below will end all subscriptions.</p>
<button id="unsubscribe">Unsubscribe</button>
</body>

<script type="text/javascript">
	/*
	 The Jabber variable holds all JavaScript code required for communicating with the server.
	 It basically overrides the functions in jquery.atmosphere.js.
	  */
	var Jabber = {
		resource: null,
		socket: null,
		chatSubscription: null,
		notificationSubscription: null,
		publicSubscription: null,

		publishOptions: {
			type: '',
			resource: '',
			message: ''
		},

		subscribe: function (options) {
			var defaults = {
						type: '',
						url: '', // Must start with http:// or https://
						connectTimeout: -1,
						reconnectInterval: 0,
						timeout: 300000, // Same as session timeout?
						method: 'GET',
						headers: {},
						contentType: 'application/json',
						data: '',
						suspend: true,
						maxRequest: 60,
						maxStreamingLength: 10000000,
						logLevel: 'info', // info | debug | error
						transport: 'websocket',
						fallbackTransport: 'long-polling',
						fallbackMethod: 'GET',
						webSocketImpl: null,
						webSocketUrl: null,
						webSocketPathDelimiter: '@@',
						enableXDR: false,
						rewriteURL: false,
						attachHeadersAsQueryString: true,
						dropAtmosphereHeaders: false,
						executeCallbackBeforeReconnect: false,
						withCredentials: false,
						trackMessageLength: false,
						messageDelimiter: '|',
						shared: false
					},
					jabberRequest = $.extend({}, defaults, options);
			Jabber.socket.onOpen = function (response) {
				console.log('jabberOpen transport: ' + response.transport);
			};
			Jabber.socket.onReconnect = function (request, response) {
				console.log("jabberReconnect");
				Jabber.socket.info('Reconnecting');
			};
			Jabber.socket.onMessage = function (response) {
				console.log('jabberMessage: ' + response);
				Jabber.onMessage(response);
			};
			Jabber.socket.onError = function (response) {
				console.log('jabberError: ' + response);
			};
			Jabber.socket.onTransportFailure = function (errorMsg, request) {
				console.log('jabberTransportFailure: ' + errorMsg);
			};
			Jabber.socket.onMessagePublished = function (request, response) {
				console.log('jabberMessagePublished');
			};
			Jabber.socket.onClose = function (response) {
				console.log('jabberClose: ' + response);
			};
			switch (options.type) {
				case 'chat':
					jabberRequest = $.extend({}, defaults, {
						headers: {mapping: '/jabber/chat/12345'},
						url: 'http://localhost:8080/atmosphere2/jabber/chat/12345'
					});
					Jabber.chatSubscription = Jabber.socket.subscribe(jabberRequest);
					break;
				case 'notification':
					jabberRequest = $.extend({}, defaults, {
						headers: {mapping: '/jabber/notification/userName'},
						url: 'http://localhost:8080/atmosphere2/jabber/notification/userName'
					});
					Jabber.notificationSubscription = Jabber.socket.subscribe(jabberRequest);
					break;
				case 'public':
					jabberRequest = $.extend({}, defaults, {
						headers: {mapping: '/jabber/public'},
						url: 'http://localhost:8080/atmosphere2/jabber/public'
					});
					Jabber.publicSubscription = Jabber.socket.subscribe(jabberRequest);
					break;
				default:
					return false;
			}
		},

		unsubscribe: function () {
			Jabber.socket.unsubscribe();
		},

		onMessage: function (response) {
			var data = response.responseBody;
			if ((message == '')) {
				return;
			}
			var message = JSON.parse(data);
			var type = message.type;
			console.log('type: ' + message.type);
			console.log('resource: ' + message.resource);
			console.log('message: ' + message.message);
			if (type == 'chat') {
				var $chat = $('#chat-window');
				$chat.append('message: ' + message.message + '<br/>');
				$chat.scrollTop($chat.height());
			}
			if (type == 'notification') {
				$('#notification').html(message.message);
			}
			if (type == 'public') {
				$('#public-update').html(message.message);
			}
		},

		publish: function (options) {
			var settings = $.extend({}, Jabber.publishOptions, options),
					type = settings.type,
					resource = settings.resource,
					message = settings.message;
			if ((type == '') || (message == '')) {
				return false;
			}
			var data = {
				type: type,
				resource: resource,
				message: message
			};
			switch (type) {
				case 'chat':
					Jabber.chatSubscription.push(jQuery.stringifyJSON(data));
					break;
				case 'notification':
					Jabber.notificationSubscription.push(jQuery.stringifyJSON(data));
					break;
				default:
					return false;
			}
		}
	};

	$(document).ready(function () {
		Jabber.socket = $.atmosphere;

		$('#chat-subscribe').on('click', function () {
			Jabber.subscribe({type: 'chat'});
			$(this).attr('disabled', 'disabled');
		});

		$('#chat-input').keypress(function (event) {
			if (event.which === 13) {
				event.preventDefault();
				var message = $(this).val();
				Jabber.publish({
					type: 'chat',
					resource: '/jabber/chat/12345',
					message: message
				});
				$(this).val('');
			}
		});

		$('#notification-subscribe').on('click', function () {
			Jabber.subscribe({type: 'notification'});
			$(this).attr('disabled', 'disabled');
		});

		$('#notification-send').on('click', function () {
			Jabber.publish({
				type: 'notification',
				resource: '/jabber/notification/userName',
				message: 'This is a notification message sent at ' + new Date() + '.'
			});
		});

		$('#public-subscribe').on('click', function () {
			Jabber.subscribe({type: 'public'});
			$(this).attr('disabled', 'disabled');
		});

		$('#public-trigger').on('click', function () {
			$.ajax('http://localhost:8080/atmosphere2/atmosphereTest/triggerPublic');
			$(this).attr('disabled', 'disabled');
		});

		$('#unsubscribe').on('click', function () {
			Jabber.unsubscribe();
			$('#chat-window').html('');
			$('#notification').html('');
			$('#public-update').html('');
			$('button').each(function() {
				$(this).removeAttr('disabled');
			})
		});
	});
</script>
</html>