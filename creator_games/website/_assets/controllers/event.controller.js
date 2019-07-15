$.when( $.ready ).then(function() {
    // Initial load
    getStreamers();

    // Update every minute
    setInterval(getStreamers, 30 * 1000);
});

function getStreamers() {
    var season = $("#season-id")[0].value;

    $.ajax({
        url: "/api/streamers/" + season,
        type: 'GET',
        responseType: 'application/json',
        success: function (result) {

            result = JSON.parse(result);
            console.log(result);

            if (!result.success) {
                return;
            }

            var streamerdiv = $("#streamers");
            streamerdiv.html("");

            if (result.streamers.length === 0) {
                streamerdiv.html("Streamerlist not available yet!");
                return;
            }

            var row = null, column, card, media, user, head, headimage, name, nameh, names, dead, deadimage, buttonrow, buttondiv, buttona, button;

            for (var i = 0; i < result.streamers.length; i++) {
                var streamer = result.streamers[i];

                // First create new row if needed
                if (i % 4 === 0) {
                    if (row !== null) {
                        streamerdiv.append(row);
                    }

                    row = $("<div>").addClass("row").addClass("streamers");
                }

                column = $("<div>").addClass("col-md-3");
                card = $("<div>").addClass("card");
                media = $("<div>").addClass("media");

                user = $("<div>").addClass("row").addClass("padding-top");
                head = $("<div>").addClass("col-md-3").addClass("nobox");
                headimage = $("<img>").addClass("media-object").attr("src", "https://cravatar.eu/helmavatar/" + streamer.name + "?size=80");
                name = $("<div>").addClass("col-md-6").addClass("streamer-name");
                nameh = $("<h4>").addClass("media-heading");
                names = $("<strong>").html(streamer.name).append(" ").append("<br>").append("$").append(streamer.raised);

                nameh.append(names);
                name.append(nameh);

                head.append(headimage);

                user.append(head).append(name);

                if (streamer.winner) {
                    dead = $("<div>").addClass("col-md-3").addClass("nobox").addClass("skull");
                    deadimage = $("<img>").addClass("media-object").attr("src", "./img/winner.png").attr("title", "This streamer won the game.");
                    dead.append(deadimage);
                    user.append(dead);
                } else if (streamer.dead) {
                    dead = $("<div>").addClass("col-md-3").addClass("nobox").addClass("skull");
                    deadimage = $("<img>").addClass("media-object").attr("src", "./img/skull.png").attr("title", "This streamer has died.");
                    dead.append(deadimage);
                    user.append(dead);
                } else if (streamer.caster) {
                    dead = $("<div>").addClass("col-md-3").addClass("nobox").addClass("skull");
                    deadimage = $("<img>").addClass("media-object").attr("src", "./img/mic.png").attr("title", "This streamer is shoutcasting the event.");
                    dead.append(deadimage);
                    user.append(dead);
                }

                buttonrow = $("<div>").addClass("row");
                buttondiv = $("<div>").addClass("col-md-12");
                buttona = $("<a>").attr("href", "/streamer/" + streamer.name);
                button = $("<button>").addClass("btn").addClass("btn-primary").addClass("view").attr("type", "button").html("View Streamer");

                buttona.append(button);
                buttondiv.append(buttona);
                buttonrow.append(buttondiv);

                media.append(user).append(buttonrow);
                card.append(media);
                column.append(card);
                row.append(column);
            }

            streamerdiv.append(row);

        }
    });
}