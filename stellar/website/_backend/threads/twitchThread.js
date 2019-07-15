const fetch = require('node-fetch');

module.exports = {
    start: function() {
        update();
        setInterval(update, 60 * 1000);
    }
};

function update() {

    global.sql.query("SELECT * FROM website_members;", async function(error, results) {
        if (error) {
            console.log("Failed to fetch members for Twitch thread", error);
            return;
        }

        let live_members = [];

        for (let i = 0; i < results.length; i++) {

            if (results[i].twitch_id === -1) {
                continue;
            }

            const response = await fetch(`https://api.twitch.tv/kraken/streams/` + results[i].twitch_id, {
                method: 'GET',
                headers: {
                    'Client-ID': `${global.gConfig.twitch.client_id}`,
                    'Accept': `application/vnd.twitchtv.v5+json`
                }
            });

            const json = await response.json();

            if (json.stream) {
                results[i].viewers = json.stream.viewers;
                live_members.push(results[i]);
            }
        }

        global.live_members = live_members;
    });
}