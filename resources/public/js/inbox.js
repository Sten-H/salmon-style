$(document).ready(function() {
    var inboxItems;
    $.ajax({
        url: "/api/inbox/asd",
        type: "GET",
        success: function(data) {
            inboxItems = data;
        },
        dataType: "json",
        timeout: 2000
    });
    //var inboxItems = document.getElementsByClassName("inbox-item");
    (function poll() {
        setTimeout(function() {
            $.ajax({
                url: "/api/inbox/asd",
                type: "GET",
                success: function(newItems) {
                    for (var i = 0; i < newItems.length; i++) {
                        if(newItems[i].uri !== inboxItems[i].uri)
                            location.reload();
                        if(newItems[i].altered !== inboxItems[i].altered)
                            location.reload();
                    }
                },
                dataType: "json",
                complete: poll,
                timeout: 2000
            })
        }, 5000);
    })();
});