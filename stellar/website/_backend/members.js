const router = require('express').Router();
const renderer = require('./util/renderer');

router.get('/', (req, res) => {

    global.sql.query('SELECT * FROM website_members ORDER BY name;', function(error, results) {
        if (error) {
            console.log(error);
            return renderer.renderError(req, res, 'Failed to load members from database');
        }

        let newMembers = [];
        let members = [];

        for (let i = 0; i < results.length; i++) {
            if (results[i].new_member) {
                newMembers.push(results[i]);
            } else {
                members.push(results[i]);
            }
        }

        renderer.render(req, res, 'members', {
            title: 'Stellar SMP - Members',
            new_members: newMembers,
            members: members
        });
    });

});

module.exports = router;