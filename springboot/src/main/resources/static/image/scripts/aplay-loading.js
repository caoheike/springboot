(function(){
    var A_R = 2 * Math.PI;
    /**
     * @class
     */
    
    var cLoading = function (container, opts) {
        this.container = container;
        this.opts = opts;
        this.opts.w = this.container.offsetWidth;
        this.events = this.opts.events;
        console.log(this.opts.w)
        this.initCanvas();
    }
    
    cLoading.prototype = {
        initCanvas: function () {
            var container = this.container,
                opts = this.opts, 
                canvas1,
                canvas2,
                ctx1, 
                lw = this.opts.lw,
                ctx2, 
                w = this.opts.w, 
                h = this.opts.h,
                startImage = this.opts.startImage;
                canvas1 = this._createCanvas(w, w);
                canvas2 = this._createCanvas(w, w);
                canvas1.style.zIndex = 100;
                canvas2.style.zIndex = 200;
                ctx1 = this.ctx1 = canvas1.getContext('2d');
                ctx2 = this.ctx2 = canvas2.getContext('2d');
            $(container).append('<span class="text-box"></span><br>');
            $(container).append('<span class="text-box1"></span><br>');
           // $(container).append('<span class="text-box2"></span><br>');
            var $text = this.$text = $('.text-box');
            var  $text1=this.$text1=$('.text-box1');
          //  var  $text2=this.$text2=$('.text-box2');
            $text.css({
                position:'absolute',
                left:'40%',
                top:'15%',
                fontSize:'1.2rem'
            })
            $text1.css({
                position:'absolute',
                left:'26%',
                top:'42%',
                fontSize:'0.8rem',
                color:'#ccc'
            })
            $text2.css({
                position:'absolute',
                left:'15%',
                top:'60%',
                fontSize:'0.8rem',
                color:'#ccc'
            })
            if(startImage){
                this.setImage(startImage);
            }
        },
        initOrigin:function(){
            var  w = this.opts.w, 
            h = this.opts.h,
            lw = this.opts.lw,
            ctx1 = this.ctx1;
            var c_x = this.c_x = w / 2;
            var c_y = this.c_y = w / 2;
            this._arc((w / 2) - lw/2, ctx1, '#ccc', lw);
        }
,        _arc: function (r, ctx, color, lineWidth, endAngle) {
            ctx.beginPath();
            var x = this.c_x, y = this.c_y;
            ctx.arc(x, y, r, 0, endAngle || A_R, true);
            ctx.strokeStyle = color;
            ctx.lineWidth = lineWidth;
            ctx.closePath();
            ctx.stroke();
        },
        _createCanvas: function (w, h) {
            var canvas = document.createElement('canvas'), container = this.container;
            canvas.width = w;
            canvas.height = h;
            container.appendChild(canvas);
            canvas.style.position = 'absolute';
            canvas.style.top = '0px'; 
            canvas.style.left = '0px';    
            return canvas;
        },
        start: function () {
            this.$text1.text("正在验证");
//            this.$text2.text("153536491..");
            var self=this,distabelTran = this.opts.disabelTran,startImage = this.opts.startImage;
            this.initOrigin();
            this.setScale(1.7,distabelTran)
            this.endAngle = -90;
            this._draw();
        },
        _draw: function () {
            var self = this, end = this.endAngle, ctx2 = this.ctx2, w = this.opts.w, events = this.events,lw = this.opts.lw;
            ctx2.clearRect(0, 0, w, w);
            ctx2.beginPath();
            ctx2.arc(w / 2, w / 2, w / 2 - lw/2, -Math.PI / 2, end * Math.PI / 180, false);
            // ctx2.closePath();
            ctx2.strokeStyle = 'red';
            ctx2.lineWidth = lw;
            ctx2.stroke();
            this._createFont1();
            this.endAngle = this.endAngle + 1;
            this.timer = requestAnimationFrame(function () {
                if (self.endAngle <= 270) {
                    self._draw();
                }
            })
            if (self.endAngle > 270) {
                cancelAnimationFrame(self.timer)
                this.ctx1.clearRect(0,0,this.opts.w,this.opts.w)
                self.setImage(self.opts.defaultSrc);
                self.setScale(1);
                this.$text.text('').hide();
                this.$text1.text('').hide();
                this.$text2.text('').hide();
                if (this.events && this.events.onEnd) {
                    this.events.onEnd();
                }
                if (this.events && this.events.onChange) {
                    this.events.onChange();
                }
            }
        },
        stop: function (val) {
            
            if (!this.timer) {
                return false;
            }
            var timer = this.timer;
            if (window.requestAnimationFrame || window.webkitRequestAnimationFrame) {
                cancelAnimationFrame(timer);
            } else {
                clearTimeout(timer);
            }
            this.ctx1.clearRect(0,0,this.opts.w,this.opts.w)
            this.setImage(val);
            this.setScaleOrigin();
            this.setScale(1)
            this.$text.text('').hide();
            this.$text1.text('').hide();
            this.$text2.text('').hide();
            if (this.events && this.events.onChange) {
                this.events.onChange();
            }
        },
        _getPercent: function (a) {
            return parseInt(((a + 90) / 360) * 100) + '%';
        },
        _createFont: function () {
            var ctx2 = this.ctx2, w = this.opts.w;
            ctx2.save();
            ctx2.font = '1.3rem Helvetica';
            ctx2.fillStyle = '#000';
            var text = this._getPercent(this.endAngle);
            // console.log(text);
            ctx2.fillText(text, 0.8* w / 2, 0.6*w / 2);
           

            ctx2.save();
            ctx2.font="1rem Helvetica";
            ctx2.fillStyle = '#808080';
//            var text = '153536491..';
            // console.log(text);
            ctx2.fillText("正在验证\n",0.6 * w / 2, 1*w / 2);
//            ctx2.fillText(text, 0.3 * w / 2, 1.4*w / 2);
        },
        _createFont1:function(){
            var text = this._getPercent(this.endAngle),$text = this.$text;
            $text.text(text);
        },
        setImage: function (imagesrc,callback) {
            var w = this.opts.w;
            var context = this.ctx2;
            context.clearRect(0, 0, w, w);
            var image = new Image();
            image.src = imagesrc;
            image.onload = function () {
                context.drawImage(image, 0, 0, w, w);
                callback && callback();
            }
        },
        
        setScale: function (scale,disabelTran) {
            var container = this.container;
            if(!disabelTran){
                container.style['transition'] = 'all 0.5s ease';
                
            }
            container.style['transform'] = 'scale('+scale+')';
            this.container.style['transform-origin']='50% 0';
        },
        setScaleOrigin:function(){
            //this.container.style['transform-origin']='50% 0';
        }
    }
    window.cLoading = cLoading;
})()