const _ = require('lodash');

// module variables
const config = require('../config.json');
const defaultConfig = config.development;

process.argv.forEach((val) => {
    if (val.startsWith("environment=")) {
        const environment = val.replace("environment=", "") || 'development';
        const environmentConfig = config[environment];
        global.gConfig = _.merge(defaultConfig, environmentConfig);
        console.log("loaded env " + environment);
    } else {
        global.gConfig = defaultConfig;
    }
});


const app = require('../app');
const http = require('http');

const server = http.createServer(app);

if (global.gConfig.production) {
    console.log(" ");
    console.log(" ");
    console.log("     --------------------------------");
    console.log("     |                              |");
    console.log("     |  RUNNING IN PRODUCTION MODE  |");
    console.log("     |                              |");
    console.log("     --------------------------------");
    console.log(" ");
    console.log(" ");
}

server.listen(global.gConfig.node_port, () => {
    console.log(`Connected on port ${global.gConfig.node_port}.`);
});