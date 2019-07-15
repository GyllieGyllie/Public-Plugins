const router = require('express').Router();
const moment = require('moment');
const renderer = require('./util/renderer');

/**
 * Mainpage info
 */
var donationInfo = null;
var latestDonationInfo = -1;

router.get('/donationinfo', (req, res) => {

    if (donationInfo != null && moment().isBefore(moment(latestDonationInfo))) {
        renderer.renderJson(res, donationInfo);
    } else {
        grpc.wtaservice.getGlobalInfo({
            season: global.gConfig.season
        }, (err, response) => {
            if (err) {
                grpc.checkError(err);
                renderer.renderJson(res, { success: false });
            } else {

                donationInfo = response;
                latestDonationInfo = moment().add(30, 's');
                renderer.renderJson(res, response);
            }
        });
    }

});

/**
 * Streamers overview
 */
router.get('/streamers/:season', (req, res) => {

    grpc.wtaservice.getStreamers({
        season: req.params.season
    }, (err, response) => {
        if (err) {
            grpc.checkError(err);
            renderer.renderJson(res, { success: false });
        } else {
            renderer.renderJson(res, response);
        }
    });

});

router.get('/streamer/:season/:name', (req, res) => {

    grpc.wtaservice.getStreamer({
        name: req.params.name,
        season: req.params.season
    }, (err, response) => {

        if (err || !response.success) {
            grpc.checkError(err);
            return renderer.renderJson(res, { success: false });
        }

        grpc.wtaservice.getItems({
            season: req.params.season
        }, (err1, response1) => {
            if (err1 || !response1.success) {
                grpc.checkError(err1);
                response.streamer.items = [];
                renderer.renderJson(res, response.streamer);
            } else {
                response.streamer.items = response1.items;
                response.streamer.success = true;
                renderer.renderJson(res, response.streamer);
            }
        });
    });
});

router.get('/streamer_small/:name', (req, res) => {

    grpc.wtaservice.getStreamer({
        name: req.params.name,
        season: global.gConfig.season
    }, (err, response) => {

        if (err || !response.success) {
            grpc.checkError(err);
            return renderer.renderJson(res, { success: false });
        }

        grpc.wtaservice.getGlobalInfo({
            season: global.gConfig.season
        }, (err, response1) => {
            if (err) {
                grpc.checkError(err);
                renderer.renderJson(res, {success: false});
            } else {
                response.streamer.success = true;
                response.streamer.global = response1.raised;
                renderer.renderJson(res, response.streamer);
            }
        });
    });

});

module.exports = router;