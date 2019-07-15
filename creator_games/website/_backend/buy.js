const router = require('express').Router();
const renderer = require('./util/renderer');
const moment = require('moment');

router.get('/:name/:season/:id', (req, res) => {

    global.grpc.wtaservice.getStreamer({
        name: req.params.name,
        season: req.params.season
    }, (err, response) => {
        if (err || !response.success) {
            grpc.checkError(err);
            return renderer.render(req, res, 'buy', {
                title: 'Winner Takes All - Purchase Item',
                success: false,
                buyable: false
            });
        }

        global.grpc.wtaservice.getItem({
            id: req.params.id,
            season: req.params.season
        }, (err1, response1) => {
            if (err1 || !response1.success) {
                grpc.checkError(err1);
                return renderer.render(req, res, 'buy', {
                    title: 'Winner Takes All - Purchase Item',
                    success: false,
                    buyable: false
                });
            }

            if (!response1.item.buyable && !req.query.payment_preview) {
                return renderer.render(req, res, 'buy', {
                    title: 'Winner Takes All - Purchase Item',
                    success: true,
                    buyable: false,
                    round: response1.item.last_round
                });
            }

            renderer.render(req, res, 'buy', {
                title: 'Winner Takes All - Purchase Item',
                success: true,
                buyable: true,
                name: response.streamer.name,
                item_name: response1.item.name,
                item_description: response1.item.description,
                item_price: response1.item.price,
                item_dead: response1.item.dead,
                paypal_key: global.gConfig.paypal_key
            });
        })
    });

});

router.post('/:name/:season/:id', (req, res) => {

    let name = req.body.name;
    let message = req.body.message;
    let amount = req.body.amount;

    if (!name || name === "") {
        return renderer.renderJson(res, {
            success: false,
            message: 'Please fill in all fields!'
        });
    }

    global.grpc.wtaservice.createPayment({
        streamer: req.params.name,
        id: req.params.id,
        name: name,
        message: message,
        amount: amount,
        season: req.params.season,
        ip: req.headers['x-forwarded-for'] || req.connection.remoteAddress
    }, (err, response) => {
        if (err) {
            grpc.checkError(err);
            return renderer.renderJson(res, {
                success: false,
                message: 'Internal Error'
            });
        }

        if (!response.success) {
            return renderer.renderJson(res, {
                success: false,
                message: response.message
            });
        }

        return renderer.renderJson(res, {
            success: true,
            orderID: response.order_id
        });
    });
});

router.post('/approve', (req, res) => {

    let orderID = req.body.orderID;

    if (!orderID || orderID === "") {
        return renderer.renderJson(res, {
            success: false
        });
    }

    global.grpc.wtaservice.approvedPayment({
        order_id: orderID
    }, (err, response) => {
        return renderer.renderJson(res, {
            success: true,
            link: '/payment/' + response.hash
        });
    });
});

module.exports = router;