const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser')();
const logger = require('morgan');
const rfs = require('rotating-file-stream');
const renderer = require('./_backend/util/renderer');

//global.grpc = require('./grpcclient');

const app = express();

app.set('views', path.join(__dirname, '_frontend'));
app.set('view engine', 'ejs');

app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(express.static(path.join(__dirname, '_assets')));

// create a rotating write stream
var accessLogStream = rfs('access.log', {
    interval: '1d', // rotate daily
    path: path.join(__dirname, 'log')
});
app.use(logger(':req[x-forwarded-for] | [:date[iso]] | :method :url :status | :referrer | :user-agent', { stream: accessLogStream }));
app.use(logger(':req[x-forwarded-for] | [:date[iso]] | :method :url :status', {
    skip: function (req, res) { return req.originalUrl.indexOf("/api") > -1 }
}));

app.use(cookieParser);

app.use('/', require('./_backend/index'));
app.use('/event', require('./_backend/event'));
app.use('/streamer', require('./_backend/streamer'));
app.use('/buy', require('./_backend/buy'));
app.use('/payed', require('./_backend/payed'));
app.use('/widget', require('./_backend/widget'));
app.use('/passed_event', require('./_backend/passed_event'));
app.use('/api', require('./_backend/api'));

app.use(function(req, res) {
    renderer.render(req, res, '404', {
        title: 'Winner Takes All - 404'
    });
});

app.use(function(err, req, res, next) {
    renderer.render(req, res, '500', {
        title: 'Winner Takes All - 500'
    });
});

module.exports = app;