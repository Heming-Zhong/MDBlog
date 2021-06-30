<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="middleground.*"%>
<% 
    // 获取用户名和密码
    request.setCharacterEncoding("utf-8"); 
    String username = request.getParameter("user");
    if (username == null) {
        username = "admin";
    }
    String passwd = request.getParameter("passwd");
    if (passwd == null) {
        passwd = "123";
    }
    String Title = "Login";

    // TODO: 验证用户内容并跳转
    DBHandle handler=new DBHandle();
    String location = "index.jsp";
    if(handler.validate(username,passwd))
        location = "main_edit.jsp";
    else 
        location = "main_preview.jsp";
        //alert("用户不存在或密码错误！");
%>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>登录</title>
    <meta http-equiv="Content-Security-Policy" content="script-src 'self' 'unsafe-inline';" />
    <link rel="stylesheet" type="text/css" href="/css/index.css">
</head>

<body>
    <h1 class="title">登录</h1>
    <form class="input" method="POST" action="index.jsp" id="passForm">
        <div class="inputfield">用户名:</div>
        <input type="text" class="inputfield" id="user" name="user" placeholder="User name" />
        <div class="inputfield">密码:</div>
        <input type="password" class="inputfield" id="passwd" name="passwd" placeholder="Password" />
    </form>
    <button class="inputfield btn" id="submitacount" onclick="handle()" onkeydown="javascript:if(event.keyCode==13) handle()">
        登录
    </button>
    <script>
        handle = function() {
            var formObj = document.getElementById('passForm'); 
            formObj.submit(); 
        }
        var redirect = "<%out.print(location);%>"
        if (redirect == "main_preview.jsp" || redirect == "main_edit.jsp") {
            window.location = redirect + "?user=" + "<%out.print(username);%>;" + "passwd=" + "<%out.print(passwd);%>;"
        }
        // debug to see the submission of user info
        alert("<%out.print(username);%>");
        alert("<%out.print(passwd);%>");
    </script>
</body>

</html>