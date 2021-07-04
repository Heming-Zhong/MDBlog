<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import="middleground.*"%>
<%         
    // just for test
    request.setCharacterEncoding("utf-8"); 
    String token = request.getParameter("token");
    
    boolean authorized = true;
    String filename = request.getParameter("pid");

    // TODO: complete the interaction with backend here
    DBHandle handler;
    if (token != null) {
        handler=new DBHandle(token);
    }
    else handler=new DBHandle();
    authorized = handler.getAuthority();
    List<String> M_list=handler.filemenu();
    if(filename == null && M_list != null && !M_list.isEmpty())
        filename = M_list.get(0);
    String content = "";
    if (filename != null && !filename.isEmpty()) {
        content = handler.get_document_content(filename);
    }
    // encode HEX
    // CAUTION: 
    // Whenever you want to pass a java String to a js String, call this code
    StringBuilder stringBuilder = new StringBuilder();
    char[] charArray = content.toCharArray();
    for (char c : charArray) {
        String charToHex = "\\u" + String.format("%04X", new Integer(c));
        stringBuilder.append(charToHex);
    }

    content = stringBuilder.toString();
    String Title = "Visitor";

    StringBuilder menulist=new StringBuilder("");
    if (M_list != null && !M_list.isEmpty()) {
        for(String str: M_list)
        {
            String url1 = "open(&apos;"+ str +"&apos;);";
            String temp = "<a class='nav-list' href='javascript:" + url1 + "' >" + str + "</a>";
            menulist.append(temp);
        }
    }
    String list = menulist.toString();
%>

<!DOCTYPE HTML>
<html>

<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no" />
    <!-- ⚠️生产环境请指定版本号，如 https://cdn.jsdelivr.net/npm/vditor@x.x.x/dist... -->
    <link rel="stylesheet" href="./css/purple.css" />
    <link rel="stylesheet" href="./dist/index.css" />
    <link rel="stylesheet" href="./css/main.css" />
    <link rel="stylesheet" href="./css/purple.user.css" />
    <title><%=Title%></title>
</head>

<body>
    <div id="wrapper" style="display: inline-flex; width: 100%; height: 100%;">
        <div id="nav" style="min-width: 15em; display: block;" >
            <div id="list-item">
                <%out.print(list);%>
                <%-- <a class="nav-list" href="main_edit.jsp?save=hello">item1</a> --%>
                <form  method="get" action="main_preview.jsp" id ="passForm">  
                    <input type="hidden" name="token" id="token">  
                    <input type="hidden" id = 'pid' name="pid">  
                    <input type="hidden" id="savefilename" name="savefilename">
                </form>
            </div>
        </div>
        <!--  vditor--fullscreen -->
        <div  id="vditor" class="vditor "></div>
        <!-- <button onclick="= GetContent()">get content</button> -->
    </div>
    <script src="/dist/method.min.js">
    </script>
    
    <script>
        Vditor.preview(document.getElementById("preview"), "<%=content%>")
        
        open = function(filename) {
            document.getElementById('token').value = "<%out.print(token);%>"
            document.getElementById('pid').value = filename
            var formObj = document.getElementById('passForm');
            formObj.submit(); 
        }

        SetContent = function(content) {
            Vditor.preview(document.getElementById("preview"), content)
        }

        // TODO: Preview模式下的亮暗主题切换有bug
        function handleChange(mediaQueryListEvent) {
            if (mediaQueryListEvent.matches) {
                // 用户切换到了暗色(dark)主题
                console.log("change to dark theme")
                document.getElementById("vditor").class = "vditor vditor--dark"
            } else {
                // 用户切换到了亮色(light)主题
                console.log("change to light theme")
                document.getElementById("vditor").class = "vditor"
            }
        }

        const mediaQueryListDark = window.matchMedia('(prefers-color-scheme: dark)');
        if (mediaQueryListDark.matches) {
            console.log('test')
            document.getElementById("vditor").class = "vditor vditor--dark"
            Vditor.setContentTheme("dark", "/dist/css/content-theme")
        }
        // 添加主题变动监控事件
        mediaQueryListDark.addListener(handleChange);
    </script>
</body>

</html>
