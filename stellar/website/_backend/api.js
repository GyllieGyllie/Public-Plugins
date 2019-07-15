const router = require('express').Router();
const renderer = require('./util/renderer');

router.get('/live', (req, res) => {
    renderer.renderJson(res, {live: global.live_members});
});

module.exports = router;