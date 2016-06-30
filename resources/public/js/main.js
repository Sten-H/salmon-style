var main = {};
(function(context) {
	$(document).ready(function() {
		"use strict";
		/* activate plugin that makes file input forms, feel more bootstrap-y. Maybe only do this in home view? */
		$('input[type=file]').bootstrapFileInput();
		$('.file-inputs').bootstrapFileInput();
		// Make all alerts dismissable by clicking anywhere on them
		$(".alert").click(function() {
			$(".alert").alert("close");
		});
	});
}).apply(main);