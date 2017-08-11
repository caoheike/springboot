/**
 * @author HotWong 2017-02-14
 */
var info = {
		pageNum:1,//页码，比如你要去第五页，传5进来就可;默认1
		pageSize:10, //当页最大展示数量 
		pageCount:0, //返回数据总页数量
		totalCount:0,//返回数据总和数量
		dataList:null,//存放返回数据集合
		ajaxUrl:null,//本次ajax请求的URL
		pageElement:$(".clearfix"),//分页器元素定位。
		pageSelect:"select",//分页器下拉框元素，可用默认，也可再传参的时候set。此处不是元素参数
		totalCountElement:$(".totalCount"),//总条数元素定位
		formElement:null,//请求表单元素定位，例如：$('#formElement')
		print:null,//打印数据函数，回调执行
		Reconnect:1,//请求失败，重新连接次数。
		data:null,//上次请求数据
		init:function(data){
			$(info.pageElement).off("change");
			if(typeof(data)!='undefined'){
				typeof(data.pageNum)!='undefined'?this.pageNum=data.pageNum:false;
				typeof(data.pageSize)!='undefined'?this.pageSize=data.pageSize:false;
				typeof(data.ajaxUrl)!='undefined'?this.ajaxUrl=data.ajaxUrl:false;
				typeof(data.formElement)!='undefined'?this.formElement=data.formElement:false;
				typeof(data.pageElement)!='undefined'?this.pageElement=data.pageElement:false;
				typeof(data.totalCountElement)!='undefined'?this.totalCountElement=data.totalCountElement:false;
				typeof(data.pageSelect)!='undefined'?this.pageSelect=data.pageSelect:false;
				typeof(data.print)!='undefined'?this.print=data.print:false;
			}
			$(info.pageElement).on("change",info.pageSelect,function(){
				info.init({pageSize:Number($(this).val())});
			});
			loadData(this.print);
		},
		serializeForm:function (dom){
			   var o = {};
			   var a = dom.serializeArray();
			   $.each(a, function() {
			        if (o[this.name]) {
			            if (!o[this.name].push) {
			                o[this.name] = [o[this.name]];
			            }
			           o[this.name].push(this.value);
			       } else {   
			           o[this.name] = this.value;
			       }   
			   });   
			   return o;   
			}
};

/**
 * 发起请求
 */
function loadData(fn){
	if(info.formElement==null || info.ajaxUrl==null){
		return;
	}
	var data=info.serializeForm(info.formElement);
	data['query.pageNum']=info.pageNum;
	data['query.pageSize']=info.pageSize;
	info.data=data;
  	console.log("请求参数:"+JSON.stringify(data));
	$.ajax({
		url:info.ajaxUrl,
		type:"POST",
		dataType:"json",
		data:data,
		success:function(data){
			debugger;
			if(data.flag){
				info.pageNum=data.num;
				info.pageCount=data.pageCount;
				info.totalCount=data.totalCount;
				info.dataList=data.dataList;
				info.totalCountElement.html("共有<span>"+info.totalCount+"</span>条记录");
				//打印数据
				if(typeof(fn)!='undefined' && fn!=null){
					fn(info.dataList);
				}
				//打印底部分页栏
				printPage(info.pageNum);
			}else{
				if(typeof(data.msg)!="undefined"){
					swal("错误!", data.msg, "error");
				}
			}
		},error:function(data){
			debugger;
			if(info.Reconnect>0){
				info.Reconnect--;
				info.init();
			}
		}
	});
}
//打印页面控制链接
function printPage(pageNum){
	debugger;
	var context="";
	context+="<ul class='pagination'>";
	if((pageNum-1)>=1){
		context+="<li><a href='javascript:;' aria-label='Previous' onclick='pagePaging("+(pageNum-1)+")'><span aria-hidden='true'>上一页</span></a></li>";
	}
	if((pageNum-3)>=1){
		context+="<li><a href='javascript:;' onclick='pagePaging("+(pageNum-3)+")'>"+(pageNum-3)+"</a></li>";
	}
	if((pageNum-2)>=1){
		context+="<li><a href='javascript:;' onclick='pagePaging("+(pageNum-2)+")'>"+(pageNum-2)+"</a></li>";
	}
	if((pageNum-1)>=1){
		context+="<li><a href='javascript:;' onclick='pagePaging("+(pageNum-1)+")'>"+(pageNum-1)+"</a></li>";
	}
	context+="<li class='active'><a href='javascript:;'>"+pageNum+"</a></li>";
	if((pageNum+1)<=info.pageCount){
		context+="<li><a href='javascript:;' onclick='pagePaging("+(pageNum+1)+")'>"+(pageNum+1)+"</a></li>";
	}
	if((pageNum+2)<=info.pageCount){
		context+="<li><a href='javascript:;' onclick='pagePaging("+(pageNum+2)+")'>"+(pageNum+2)+"</a></li>";
	}
	if((pageNum+3)<=info.pageCount){
		context+="<li><a href='javascript:;' onclick='pagePaging("+(pageNum+3)+")'>"+(pageNum+3)+"</a></li>";
	}
	if((pageNum+3)<info.pageCount){
		if((info.pageCount-(pageNum+3))>1){
			context+="<span class='dian'>…</span>";
		}
		context+="<li><a href='javascript:;' onclick='pagePaging("+info.pageCount+")'>"+info.pageCount+"</a></li>";
	}
	context+="<li><a href='javascript:;' aria-label='Next'  onclick='pagePaging("+(pageNum+1)+")'><span aria-hidden='true'>下一页</span></a></li>";
	context+="<li><input type='text' maxlength='3' id='pageNum'/><a href='javascript:;' id='btnPage'>GO</a></li>";
	context+="</ul>";
	info.pageElement.html(context);
	$('html, body').animate({scrollTop:0}, 'slow');
}
$(function(){
	$(".pagination").on("click","#btnPage",function(){
		pagePaging($("#pageNum").val());
	});
	$(".pagination").on("blur","#pageNum",function(){
		pagePaging($("#pageNum").val());
	});
});
//分页
function pagePaging(obj){
	(typeof(obj)=="undefined" || obj=="")?obj=1:false;
	obj=Number(obj);
	if(obj>info.pageCount || obj==info.pageNum){
		return;
	}
	if(obj!=0 && obj!=info.pageNum && obj<=info.pageCount){
		//加载数据
		info.init({pageNum:obj});
	}
}
