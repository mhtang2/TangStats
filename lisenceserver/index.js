var express = require('express');
var app = express();
var port = process.env.PORT || 8080;

const low = require('lowdb');
const FileSync = require('lowdb/adapters/FileSync')
const adapter = new FileSync('db.json')
const db = low(adapter);
app.listen(port, () => {
    console.log(port);
})
app.get('/lisence', function(req, res) {
    console.log("bar");
    key = req.header('key');
    if (db.has(key).value()) {
        dat = db.get(key).value()
        console.log(dat)
        if(dat.count<dat.max){
        	db.get(key).update("count",n=>n+1).write()
        	res.send((dat.max-dat.count).toString())
        }else{
        	res.send("-2")
        }
    }else{
    	res.send("-1")
    }
})