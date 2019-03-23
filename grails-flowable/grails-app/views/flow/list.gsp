<!doctype html>
<!--[if lt IE 7]>
<html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>
<html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>
<html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js"><!--<![endif]-->
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>流程列表</title>
    <meta name="description" content="">
    <meta name="viewport" content="initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no, width=device-width">

    <asset:stylesheet src="layui/css/layui.css"/>
    <script>
        %{-- 定义全局变量，供JS中调用--}%
        const basePath = "${request.contextPath}";
        const baseUrl = "${request.contextPath}/assets/flow";
    </script>
</head>

<body>

<table class="layui-hide" id="test" lay-filter="test"></table>

<script type="text/html" id="barDemo">
<a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
<a class="layui-btn layui-btn-xs" lay-event="deploy">发布</a>
<a class="layui-btn layui-btn-xs layui-btn-danger" lay-event="start">启动</a>
</script>

<script type="text/html" id="toolbarDemo">
<div class="layui-btn-container">
    <button class="layui-btn layui-btn-sm" lay-event="create">新建流程</button>
</div>
</script>

<asset:javascript src="layui/layui.js"/>

<script>
    layui.use(['jquery','table','layer'], function () {
        var $ = layui.$,table = layui.table,layer = layui.layer;
        var baseAddr = "${request.contextPath}";
        var url = baseAddr+"/flow/modelList";
        table.render({
            elem: '#test'
            , url: url
            , toolbar: '#toolbarDemo'
            , title: '所有流程列表'
            , cols: [[
                {type: 'checkbox', fixed: 'left'}
                , {field: 'id', title: 'ID', fixed: 'left', unresize: true, sort: true}
                , {field: 'name', title: '流程名称'}
                , {field: 'description', title: '备注', templet: '<div>{{JSON.parse(d.metaInfo).description}}</div>'}
                , {field: 'createTime', title: '创建时间'}
                , {field: 'lastUpdateTime', title: '修改时间'}
                , {field: 'key', title: '流程key', width: 200}
                , {fixed: 'right', title: '操作', toolbar: '#barDemo'}
            ]]
            , page: true
        });

        //监听行工具事件
        table.on('tool(test)', function (obj) {
            var data = obj.data;
            switch (obj.event) {
                case "edit":
                    window.location.href = baseAddr + "/flow/index?modelId=" + data.id;
                    break;
                case "deploy":
                    deal("deploy",data.id);
                    break;
                case "start":
                    deal("start",data.key);
                    break;
            }
        });

        //头工具栏事件
        table.on('toolbar(test)', function(obj){
            var checkStatus = table.checkStatus(obj.config.id); //获取选中行状态
            switch(obj.event){
                case 'create':
                    create();
                    break;
            };
        });

        function create() {
            $.post(baseAddr + "/flow/create",function (res) {
                console.log(res);
                window.location.href = baseAddr + "/flow/index?modelId=" + res.id;
            });
        }

        function deal(op,id) {
            if(op == "start") {
                $.get(baseAddr + "/flow/start",{keyName: id},function (res) {
                    console.log(res);
                    if(res.result === true) {
                        layer.msg(id + "启动成功");
                        setTimeout(function() {
                            window.location.reload();
                        },2000)
                    } else {
                        layer.msg(id + "启动失败");
                    }
                });
            } else {
                $.get(baseAddr + "/flow/deploy",{id: id},function (res) {
                    if(res.result === true) {
                        layer.msg(id + "发布成功");
                        setTimeout(function() {
                            window.location.reload();
                        },2000)
                    } else {
                        layer.msg(id + "发布失败");
                    }
                });
            }
        }
    });
</script>
</body>
</html>