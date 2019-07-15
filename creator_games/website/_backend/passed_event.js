const router = require('express').Router();
const renderer = require('./util/renderer');

router.get('/:season', (req, res) => {

    grpc.wtaservice.getStreamers({
        season: req.params.season
    }, (err, response) => {
        if (err || !response.success) {
            grpc.checkError(err);
            renderer.render(req, res, '500', {
                title: 'Winner Takes All - 500'
            });
        } else {
            if (global.gConfig.seasons['s' + req.params.season]) {
                renderer.render(req, res, 'passed_event', {
                    title: 'Winner Takes All - Season ' + req.params.season,
                    season: req.params.season,
                    name: global.gConfig.seasons['s' + req.params.season].name,
                    date: global.gConfig.seasons['s' + req.params.season].date,
                    streamers: response.streamers
                });
            } else {
                renderer.render(req, res, '404', {
                    title: 'Winner Takes All - 404'
                });
            }
        }
    });
});

module.exports = router;