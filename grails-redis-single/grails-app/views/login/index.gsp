<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>css3炫酷登录页</title>
    <meta name="description" content="particles.js is a lightweight JavaScript library for creating particles.">
    <meta name="author" content="Vincent Garreau" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <asset:stylesheet href="login.css" />
</head>
<body>

<div id="particles-js">
    <form>
    <div class="login">
        <div class="login-top">
            登录
        </div>
        <div class="login-center clearfix">
            <div class="login-center-img"><img src="${request.contextPath}/assets/name.png"/></div>
            <div class="login-center-input">
                <input type="text" name="username" value="" placeholder="请输入您的用户名" onfocus="this.placeholder=''" onblur="this.placeholder='请输入您的用户名'"/>
                <div class="login-center-input-text">用户名</div>
            </div>
        </div>
        <div class="login-center clearfix">
            <div class="login-center-img"><img src="${request.contextPath}/assets/password.png"/></div>
            <div class="login-center-input">
                <input type="password" name="password" value="" placeholder="请输入您的密码" onfocus="this.placeholder=''" onblur="this.placeholder='请输入您的密码'"/>
                <div class="login-center-input-text">密码</div>
            </div>
        </div>
        <div class="login-button">
            登陆
        </div>
    </div>
    </form>
    <div class="sk-rotating-plane"></div>
</div>

<!-- scripts -->
<asset:javascript src="jquery-3.3.1.min.js"/>
<asset:javascript src="login/particles.min.js" />
<asset:javascript src="login/app.js" />
<script type="text/javascript">
    function hasClass(elem, cls) {
        cls = cls || '';
        if (cls.replace(/\s/g, '').length == 0) return false; //当cls没有参数时，返回false
        return new RegExp(' ' + cls + ' ').test(' ' + elem.className + ' ');
    }

    function addClass(ele, cls) {
        if (!hasClass(ele, cls)) {
            ele.className = ele.className == '' ? cls : ele.className + ' ' + cls;
        }
    }

    function removeClass(ele, cls) {
        if (hasClass(ele, cls)) {
            var newClass = ' ' + ele.className.replace(/[\t\r\n]/g, '') + ' ';
            while (newClass.indexOf(' ' + cls + ' ') >= 0) {
                newClass = newClass.replace(' ' + cls + ' ', ' ');
            }
            ele.className = newClass.replace(/^\s+|\s+$/g, '');
        }
    }
    document.querySelector(".login-button").onclick = function(){
        addClass(document.querySelector(".login"), "active");
        setTimeout(function(){
            addClass(document.querySelector(".sk-rotating-plane"), "active");
            document.querySelector(".login").style.display = "none"
        },800);
        login();
    };

    window.addEventListener("keyup", function (ev) {
        if (ev.keyCode === 13) {
            login();
        }
    });

    function login() {
        $.post("${request.contextPath}/login/auth", $("form").serialize(), function (res) {
            alert(res.msg);
            if (res.status == 200) {
                setTimeout(function () {
                    location.href = "${request.contextPath}/login/main";
                }, 2000)
            } else {
                setTimeout(function () {
                    removeClass(document.querySelector(".login"), "active");
                    removeClass(document.querySelector(".sk-rotating-plane"), "active");
                    document.querySelector(".login").style.display = "block";
                }, 1000);
            }
        });
    }
</script>
</body>
</html>