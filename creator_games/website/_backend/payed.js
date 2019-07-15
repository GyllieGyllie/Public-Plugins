const router = require('express').Router();
const renderer = require('./util/renderer');

router.get('/:order_id', (req, res) => {
    global.grpc.wtaservice.getDonation({
        order_id: req.params.order_id
    }, (err, response) => {
       if (err) {
           grpc.checkError(err);
           return renderer.render(req, res, 'payed', {
               title: 'Winner Takes All - Payment Complete',
               success: false,
               order_id: req.params.order_id
           });
       }

        return renderer.render(req, res, 'payed', {
            title: 'Winner Takes All - Payment Complete',
            success: response.success,
            response: response,
            order_id: req.params.order_id
        });
    });


});

module.exports = router;