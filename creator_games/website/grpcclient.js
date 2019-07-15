const grpc = require('grpc');
const protoLoader = require('@grpc/proto-loader');
const settings = {
    keepCase: true,
    longs: String,
    enums: String,
    defaults: true,
    oneofs: true
};

/* File Definitations */
const WTASERVICE_PROTO = __dirname + '/_grpc/service.proto';

/* UserService */
const wtaservicePackage = protoLoader.loadSync(WTASERVICE_PROTO, settings);
const wtaservice = grpc.loadPackageDefinition(wtaservicePackage);
const wtaservice_client = new wtaservice.WTAService(global.gConfig.grpc.ip + ':' + global.gConfig.grpc.service, grpc.credentials.createInsecure());
console.log("Connected to PaymentService.");

/* Exports */
exports.wtaservice = wtaservice_client;

exports.checkError = function (err) {
    if (err) {
        console.log(err);

        if (err.code && err.code === 14) process.exit(14);
    }
};