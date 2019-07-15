const router = require('express').Router();
const renderer = require('./util/renderer');
const moment = require('moment');

router.get('/', (req, res) => {
    renderer.render(req, res, 'event', {
        title: 'Winner Takes All - Event',
        trailer_released: moment(global.gConfig.trailer_time).isBefore(moment()) || req.query.preview,
        season: global.gConfig.season
    });
});

module.exports = router;