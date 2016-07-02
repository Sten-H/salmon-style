var main = {}; // namespace
(function(context) {
	/**
	 * Returns a div with html for an alert, the alert type is set
	 * with type (such as 'alert-success').
	 * @param   {String} type    Bootstrap alert class such as 'alert-success'
	 * @param   {String} message Message to be displayed in alert
	 * @returns {string}   HTML div string with the alert
	 */
	var getAlertHTML = function(type, message) {
		return '<div class="alert '+ type +'"><a class="close" data-dismiss="alert">×</a><span>' +
					message + '</span></div>';
	};
	/**
	 * Specific function to create error alerts for invalid image
	 * selected for uploading.
	 * @param {message} message Message to be displayed in danger alert.
	 */
	var createInvalidImageAlert = function(message) {
		$('#alert-placeholder').html(''); // In case there's an alert here already, replace it.
		$('#alert-placeholder').html(getAlertHTML('alert-danger', '<strong>Warning: </strong>' + message));
	}
	$(document).ready(function() {
		"use strict";
		var file;
		//Add listener to file picker in image upload
		$('#image-input').on('change', function(e) {
			// FIXME this could be a bit dicey, I'm not sure how the support between browser on this is.
			file = e.currentTarget.files[0];
			var filesize = ((file.size/1024)/1024).toFixed(4); //file size in mb
			console.log(filesize);
			if(filesize > 1.0) {
				$('#upload-button').prop('disabled', true);
				$('#invalid-image-icon').show();
				$('#accepted-image-icon').hide();
				
				// Create an alert telling user that filesize is too big.
				createInvalidImageAlert('File size is too big. 1 MB maximum.');
			} else {
				$('#upload-button').prop('disabled', false);
				$('#invalid-image-icon').hide();
				$('#accepted-image-icon').show();
				$('#alert-placeholder').html('');
			}
		});
		/* activate plugin that makes file input forms, feel more bootstrap-y. Maybe only do this in home view? */
		$('input[type=file]').bootstrapFileInput();
		$('.file-inputs').bootstrapFileInput();
		// Make all alerts dismissable by clicking anywhere on them
		$(".alert").click(function() {
			$(".alert").alert("close");
		});
		$('#upload-button').on('click', function(e) {
			if(typeof file == "undefined"){
				createInvalidImageAlert('No image has been selected, please pick one!');
				e.preventDefault(); // FIXME This is to stop the button, feels like the wrong way to do it, look it up.
			}	
		});
	});
}).apply(main);