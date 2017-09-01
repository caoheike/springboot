/**
 * @class
 */
(function(){
    var PollRequest = function(opts){
        this.opts=opts;
        this.success =opts.success;
        this.fail = opts.fail;
    }
    
    PollRequest.prototype={
        start:function(){
            var self = this,
                opts = this.opts;
            var timer = this.timer = setInterval(function(){
                self.request();
            }.bind(this),opts.interval)
        },
        stop:function(){
            this.timer &&  clearInterval(this.timer);
        },
        request:function(){
            var self=this,opts= this.opts,success = this.success,fail = this.fail;
            $.ajax({
                url:opts.url,
                type:opts.type||'GET',
                data:opts.params
            })
            .then(function(res){
                var f = eval(res),success = self.success,fail = self.fail;
                if(f){
                    success && success();
                }else{
                    fail && fail();
                }
            }).always(function(){
                self.stop();
            })
        }
    }
    window.PollRequest=PollRequest;
})()
