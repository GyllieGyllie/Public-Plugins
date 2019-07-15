const fetch = require('node-fetch');

module.exports = {
    start: function() {
        update();
        setInterval(update, 5 * 60 * 1000);
    }
};

function update() {

    global.sql.query("SELECT * FROM website_members;", async function(error, results) {
        if (error) {
            console.log("Failed to fetch members for Youtube thread", error);
            return;
        }

        let recent_vids = [];

        for (let i = 0; i < results.length; i++) {

            if (results[i].youtube_id === "") {
                continue;
            }

            const response = await fetch(`https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=` + results[i].youtube_id + `&key=` + global.gConfig.youtube.client_id, {
                method: 'GET'
            });

            const json = await response.json();

            if (json.items) {

                let video;

                for (let j = 0; j < json.items.length; j++) {
                    let v = json.items[j];

                    if (!video && v.snippet && v.snippet.title && v.snippet.title.toLowerCase().indexOf("stellar") > -1) {
                        video = v;
                        break;
                    }

                }

                if (video) {
                    results[i].video = video.snippet.resourceId.videoId;
                    recent_vids.push(results[i]);
                }
            }
        }

        global.recent_vids = recent_vids;
    });
}