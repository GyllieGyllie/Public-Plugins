const router = require('express').Router();
const renderer = require('./util/renderer');

router.get('/', (req, res) => {
    renderer.render(req, res, 'map', {
        title: 'Stellar SMP - Map'
    });
});

module.exports = router;