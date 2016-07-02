var inbox = {}; // namespace
(function(context) {
	"use strict";
	// Function that polls backend to delete image with uri (user has to be logged in as uploader of image)
	var deleteInboxItem = function(uri) {
		$.ajax({
			url: "/inbox/delete/" + uri,
			type: "GET",
			success: function() {
			},
			dataType: "json",
			timeout: 2000
		});
	};
	// Polls backend for all inbox images every five seconds and upon success compares to inbox on file load,
	// reloads page if there is a difference between lists, indicates change (addition, update) in inbox.
	var checkInboxUpdated =	function(inbox) {
		setTimeout(function() {
			$.ajax({
				url: "/inbox/get",
				type: "GET",
				success: function(newItems) {
					// Try to avoid the loop if possible
					if(newItems.length !== inbox.length)
						location.reload();
					// Check if newly gotten inbox differs
					for (var i = 0; i < newItems.length; i++) {
						if(newItems[i].uri !== inbox[i].uri)
							location.reload();
						if(newItems[i].altered !== inbox[i].altered)
							location.reload(); // Reload if original inbox and new inbox differ.
					}
				},
				dataType: "json",
				complete: checkInboxUpdated(inbox),
				timeout: 2000
			});
		}, 5000); // End poll
	};
	$(document).ready(function() {
		// Gets a list of inbox items on load, then starts calls checkInboxUpdated when done to start long poll.
		$.ajax({
			url: "/inbox/get", // FIXME this isn't user specific yet.
			type: "GET",
			success: function(data) {
				var inboxItems = data; // Inbox items on load
				checkInboxUpdated(inboxItems); // Starts a long poll looking for changes in inbox.
			},
			dataType: "json",
			timeout: 2000
		});

		// Sort of a hack to get the whole list-group-item in inbox to be a link to the image, but still be able to have a clickable button on top.
		// from: http://stackoverflow.com/questions/19888431/link-within-a-list-group-item-class-itsself-a-link-in-twitter-bootstrap
		$('.list-group-item-linkable').on('click', function() {
			window.location.href = $(this).data('link');
		});
		// Make sure that clicks on links and buttons do not propagate in list-group-item-linkable.
		$('.list-group-item-linkable a, .list-group-item-linkable button')
			.on('click', function(e) {
			e.stopPropagation();
		});
		// Remove inbox item listener
		$('.list-group-item-linkable .remove-inbox-item').on('click', function() {
			var uri = $(this).data('uri');
			var itemHeight = $(this).parent().clientHeight;
			var listGroup = $(this).parent();
			$(this).parent().fadeOut( "slow", function() {
				// ajax call to remove uri from images table in db
				deleteInboxItem(uri);
				$(this).remove(); // This isn't needed really, it doesn't make any difference if it's hidden or removed?
			});
		});
	});
}).apply(inbox);

