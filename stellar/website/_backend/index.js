const router = require('express').Router();
const renderer = require('./util/renderer');

router.get('/', (req, res) => {
    renderer.render(req, res, 'index', {
        title: 'Stellar SMP - Home',
        live_members: global.live_members,
        recent_vids: global.recent_vids
    });
});

module.exports = router;