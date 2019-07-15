var initial = true;
var streamer = "";

$.when( $.ready ).then(function() {

    streamer = window.location.href.substr(window.location.href.lastIndexOf('/') + 1);

    // Initial load
    getStreamer();

    // Update every minute
    setInterval(getStreamer, 30 * 1000);
});

function getStreamer() {

    if (!streamer || streamer === "") {
        window.location = "/";
    }

    var season = $("#season-id")[0].value;

    $.ajax({
        url: "/api/streamer/" + season + "/" + streamer,
        type: 'GET',
        responseType: 'application/json',
        success: function (result) {

            result = JSON.parse(result);
            console.log(result);

            if (!result.success) {
                return;
            }

            $("#streamer_name").html('<img class="head-title" src="https://cravatar.eu/helmavatar/' + result.name + '"/>' + result.name);
            $("#streamer_raised").html("$" + result.raised);
            $("#streamer_team").html(result.team);

            if (initial) {
                var link = result.link;
                var stream = $("#stream");

                var width = $( document ).width() - 20;

                if (width > 854) {
                    width = 854;
                }

                var height = width * 0.56206088992974238875878220140515;

                if (link.indexOf("twitch") !== -1) {

                    var split = link.split("/");
                    var channel = split[split.length - 1];

                    stream
                        .append("<div id=\"{PLAYER_DIV_ID}\"></div>")
                        .append("<script type=\"text/javascript\"> " +
                            "var player = new Twitch.Player(\"{PLAYER_DIV_ID}\", { width: " + width + ", height: " + height + ", channel: \"" + channel + "\"}); " +
                            "player.setVolume(0.5); " +
                            "</script>");

                } else {

                    var frame = $("<iframe>").attr("width", width).attr("height", height).attr("src", link).attr("frameborder", 0).attr("allow", "autoplay; encrypted-media");

                    stream.append(frame);
                }

                initial = false;
            }

            var genn = $("#general");
            var poss = $("#positive");
            //var negg = $("#negative");

            if (!result.dead && !result.caster) {

                $("#s_positive").show();
                //$("#s_negative").show();
                $("#s_general").hide();

                if (poss.is(":hidden") /*&& negg.is(":hidden")*/) {
                    poss.show();
                    genn.hide();
                }


                var positives = [];
                //var negatives = [];

                for (var i = 0; i < result.items.length; i++) {
                    var item = result.items[i];

                    if (item.dead) {
                        continue;
                    }

                    var column = $("<div>").addClass("col-md-4");
                    var panel = $("<div>").addClass("panel").addClass("panel-default");
                    var header = $("<div>").addClass("panel-heading");
                    var title = $("<h4>").addClass("panel-title").append(item.name);
                    var body = $("<div>").addClass("panel-body").append(item.description).append("<br/>");
                    var strong = $("<strong>").append("$" + item.price).append("<br/>").append("<br/>");
                    //var round = $("<span>").append("Disabled at final " + item.last_round);
                    var a = $("<a>").attr("href", "/buy/" + streamer + "/" + season + "/" + item.id);
                    var button = $("<button>").addClass("btn-donate").attr("type", "button").append("Donate Now!");

                    a.append(button);
                    body.append(strong)/*.append(round)*/;
                    header.append(title);

                    panel.append(header).append(body).append(a);
                    column.append(panel);

                    if (item.positive) {
                        positives.push(column);
                    } else {
                        //negatives.push(column);
                    }
                }

                var pos = $("#positive_items");
                pos.html("");
                var row = null;

                for (var j = 0; j < positives.length; j++) {
                    if (j % 3 === 0) {

                        if (row !== null) {
                            pos.append(row);
                        }

                        row = $("<div>").addClass("row");
                    }

                    row.append(positives[j]);
                }

                if (row !== null) {
                    pos.append(row);
                }

                /*
                var neg = $("#negative_items");
                neg.html("");
                row = null;

                for (var k = 0; k < negatives.length; k++) {
                    if (k % 3 === 0) {

                        if (row !== null) {
                            neg.append(row);
                        }

                        row = $("<div>").addClass("row");
                    }

                    row.append(negatives[k]);
                }

                if (row !== null) {
                    neg.append(row);
                }
                */
            } else {

                $("#s_positive").hide();
                //$("#s_negative").hide();
                $("#s_general").show();

                if (genn.is(":hidden")) {
                    genn.show();
                    poss.hide();
                    //negg.hide();
                }

                if (result.dead) {
                    $("#general_desc").html("This streamer is dead. But no worries, you can still donate free amounts to them.");
                } else {
                    $("#general_desc").html("This streamer is shoutcasting the event. But no worries, you can still donate free amounts to them.");
                }

                var row = null;

                for (var i = 0; i < result.items.length; i++) {
                    var item = result.items[i];

                    if (!item.dead) {
                        continue;
                    }

                    var column = $("<div>").addClass("col-md-3");
                    var panel = $("<div>").addClass("panel").addClass("panel-default");
                    var header = $("<div>").addClass("panel-heading");
                    var title = $("<h4>").addClass("panel-title").append(item.name);
                    var body = $("<div>").addClass("panel-body").append(item.description).append("<br/>");
                    var a = $("<a>").attr("href", "/buy/" + streamer + "/" + season + "/" + item.id);
                    var button = $("<button>").addClass("btn-donate").attr("type", "button").append("Donate Now!");

                    a.append(button);
                    header.append(title);

                    panel.append(header).append(body).append(a);
                    column.append(panel);

                    row = $("<div>").addClass("row");
                    row.append(column);
                }

                var gen = $("#general_items");

                gen.html("");
                gen.append(row);

            }
        },
        error: function(err) {
            if (err.status == 404) {
                window.location = "/event";
            }
        }
    });

}