const router = require('express').Router();
const renderer = require('./util/renderer');
const moment = require('moment');

router.get('/', (req, res) => {
    renderer.render(req, res, 'index', {
        title: 'Winner Takes All',
        trailer_released: moment(global.gConfig.trailer_time).isBefore(moment())
    });
});

module.exports = router;