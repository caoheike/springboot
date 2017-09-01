var express = require('express')
var app = express();
var path = require('path')
app.use(express.static(path.join(__dirname,'scripts')));
app.get('/',function(req,res){
    res.sendFile(path.join(__dirname,'index.html'))
})
app.get('/getstatus',function(req,res){
    var a = Math.random();
    if(a>=0.5){
        res.send('true')
    }else{
        res.send('false');
    }
})
app.get('/sendemail',function(req,res){
    setTimeout(function(){
        res.send({data:'sssssss'})
    },3000)
})
app.listen(8000);

console.log('started')