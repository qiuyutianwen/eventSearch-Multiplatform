"use strict"
const express = require('express');
const path = require('path');
const request = require('request');
const http = require('http');
const bodyParser = require('body-parser');
const url = require('url');

const app = express();
const port = process.env.PORT || 8081;

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.all('*', function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Content-Type,Content-Length, Authorization, Accept,X-Requested-With");
    res.header("Access-Control-Allow-Methods","PUT,POST,GET,DELETE,OPTIONS");
    res.header("X-Powered-By",' 3.2.1')
    if(req.method=="OPTIONS") res.sendStatus(200);
    else  next();
});

app.get('/', (req, res) => {
    res.status(200).send('Hello, world!').end();
});

require("./Routes/apiRoutes")(app);

app.listen(port, () => {
    console.log(`App listening on port ${port}`);
    console.log('Press Ctrl+C to quit.');
});

module.exports = app;
