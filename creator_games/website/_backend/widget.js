const router = require('express').Router();
const renderer = require('./util/renderer');

router.get('/:streamer', (req, res) => {
    renderer.render(req, res, 'widget', {
        title: 'Winner Takes All',
        streamer: req.params.streamer
    });
});

module.exports = router;