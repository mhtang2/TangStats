var express = require('express');
var app = express();
var port = 8080;

const low = require('lowdb');
const FileAsync = require('lowdb/adapters/FileAsync')
    app.get('/', (req, res) => {
        res.send("HI")
    })
    const adapter = new FileAsync('db.json')
    low(adapter)
    .then(db => {
        // Routes
        // GET /posts/:id
        app.get('/license', function (req, res) {
            key = req.header('key');
            console.log(key)
            if (db.has(key).value()) {
                dat = db.get(key).value()
                    if (dat.count < dat.max) {
                        db.get(key).update("count", n => n + 1).write()
                        res.send((dat.max - dat.count).toString())
                    } else {
                        res.send("-2")
                    }
            } else {
                res.send("-1")
            }
        });
        app.get('/keybar', function (req, res) {
            key = req.header('key');
            if (db.has(key).value()) {
                dat = db.get(key).value();
                if(dat.expired){
                    res.send("1")
                }else{
                    res.send("0")
                }
            } else {
                res.send("1")
            }
        });
    })
    .then(() => {
        app.listen(port, () => console.log(`listening on port ${port}`))
    })
