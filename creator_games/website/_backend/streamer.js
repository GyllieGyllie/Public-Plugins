const router = require('express').Router();
const renderer = require('./util/renderer');
const moment = require('moment');

router.get('/:name', (req, res) => {

    if (moment(global.gConfig.trailer_time).isAfter(moment()) && !req.query.preview) {
        return res.redirect("/");
    }

    renderer.render(req, res, 'streamer', {
        title: 'Winner Takes All - Streamer',
        season: global.gConfig.season
    });
});

module.exports = router;