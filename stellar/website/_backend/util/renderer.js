const moment = require('moment-timezone');

module.exports = {
    render: function (req, res, file, data) {
        data.moment = moment;
        data.development = !global.gConfig.production;

        res.render(file, data);
    },

    renderError: function (req, res, error_message) {
        let data = {};
        data.moment = moment;
        data.development = !global.gConfig.production;
        data.error_message = error_message;
        data.title = "Error";

        res.render('_globals/error', data);
    },

    renderJson: function (res, data) {
        res.writeHead(200, { 'Content-Type': 'text/html' });
        res.write(JSON.stringify(data));
        res.end();
    }
};