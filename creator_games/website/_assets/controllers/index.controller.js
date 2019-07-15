$.when( $.ready ).then(function() {
    // Initial load
    getDonationInfo();

    // Update every minute
    setInterval(getDonationInfo, 30 * 1000);
});

function getDonationInfo() {
    $.ajax({
        url: "/api/donationinfo",
        type: 'GET',
        responseType: 'application/json',
        success: function (result) {

            result = JSON.parse(result);
            console.log(result);

            if (!result.success) {
                return;
            }

            // Set donation stats
            $("#donation_count").html(result.donations);
            $("#donation_amount").html("$" + result.raised);
            $("#progressbar").width(((result.raised / 10000) * 100) + "%");

            var latest = $("#latest_donation");
            var highest = $("#highest_donation");

            if (result.latest !== null && result.latest.amount > 0) {
                // Set latest donation
                var donator_name = result.latest.name;
                var donator_amount = result.latest.amount;
                var donator_message = result.latest.comment === "" ? "(no message)" : result.latest.comment;
                var donator_item = result.latest.item;
                var donator_streamer = result.latest.streamer;

                var strong = $("<strong>").html("$" + donator_amount);
                var header = $("<h4>").addClass("media-heading").html(" ").append(" ").append(strong);
                var header2 = $("<h4>").addClass("media-heading").html("<b>" + donator_name + "</b> bought <b>" + donator_item + "</b> for <b>" + donator_streamer + "</b>");
                var p = $("<p>").html(donator_message);

                latest.html("");
                latest.append(header).append(header2).append(p);
            } else {
                latest.html("No donations yet");
            }

            if (result.highest !== null && result.highest.amount > 0) {
                // Set highest donation
                var h_donator_name = result.highest.name;
                var h_donator_amount = result.highest.amount;
                var h_donator_message = result.highest.comment === "" ? "(no message)" : result.highest.comment;
                var h_donator_item = result.highest.item;
                var h_donator_streamer = result.highest.streamer;

                var h_strong = $("<strong>").html("$" + h_donator_amount);
                var h_header = $("<h4>").addClass("media-heading").html(" ").append(h_strong);
                var h_header2 = $("<h4>").addClass("media-heading").html("<b>" + h_donator_name + "</b> bought <b>" + h_donator_item + "</b> for <b>" + h_donator_streamer + "</b>");
                var h_p = $("<p>").html(h_donator_message);

                highest.html("");
                highest.append(h_header).append(h_header2).append(h_p);
            } else {
                highest.html("No donations yet");
            }

        }
    });
}