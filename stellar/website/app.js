const express = require('express');
const path = require('path');
const logger = require('morgan');
const rfs = require('rotating-file-stream');
const mysql = require('mysql');

const twitchThread = require('./_backend/threads/twitchThread');
const youtubeThread = require('./_backend/threads/youtubeThread');
const renderer = require('./_backend/util/renderer');

let pool = mysql.createPool({
    connectionLimit : 20,
    host            : global.gConfig.sql.host,
    port            : global.gConfig.sql.port,
    user            : global.gConfig.sql.user,
    password        : global.gConfig.sql.pass,
    database        : global.gConfig.sql.database
});
global.sql = pool;

const app = express();

// Setup express
app.set('views', path.join(__dirname, '_frontend'));
app.set('view engine', 'ejs');
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(express.static(path.join(__dirname, '_assets')));

// create a rotating write stream
const accessLogStream = rfs('access.log', {
    interval: '1d', // rotate daily
    path: path.join(__dirname, 'log')
});
app.use(logger(':req[x-forwarded-for] | [:date[iso]] | :method :url :status | :referrer | :user-agent', { stream: accessLogStream }));
app.use(logger(':req[x-forwarded-for] | [:date[iso]] | :method :url :status'));

app.use('/', require('./_backend/index'));
app.use('/members', require('./_backend/members'));
app.use('/map', require('./_backend/map'));
app.use('/api', require('./_backend/api'));

app.use(function(req, res) {
    if (req.method === "GET") {
        renderer.render(req, res, '404', {
            title: 'Stellar SMP - 404'
        });
    } else {
        res.writeHead(404);
        res.end();
    }
});

app.use(function(err, req, res, next){
    console.error(err);

    if (req.method === "GET") {
        renderer.renderError(req, res, 'Internal Web Error');
    } else {
        res.status(500);
        res.render('Internal Error');
    }
});

module.exports = app;

twitchThread.start();
youtubeThread.start();

process.on('SIGINT', () => {
    console.info('Stopping webserver.');
    global.sql.end();
    process.exit(0);
});