var express = require('express');
var app = express();
var port = 8080;

const low = require('lowdb');
const FileSync = require('lowdb/adapters/FileSync')
    const adapter = new FileSync('db.json')
    const db = low(adapter);
app.listen(port)
app.get('/', (req, res) => {
    res.send("HI")
})
app.get('/keybar', function (req, res) {
    key = req.header('key');
    if (db.has(key).value()) {
        dat = db.get(key).value();
        console.log(dat)
        console.log(dat.expired)
    } else {
        res.send("-1")
    }
})
app.get('/lisence', function (req, res) {
    key = req.header('key');
    if (db.has(key).value()) {
        dat = db.get(key).value();
        console.log(dat)
        if (dat.count < dat.max) {
            db.get(key).update("count", n => n + 1).write()
            res.send((dat.max - dat.count).toString())
        } else {
            res.send("-2")
        }
    } else {
        res.send("-1")
    }
})
