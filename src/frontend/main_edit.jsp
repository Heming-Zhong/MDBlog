<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import="middleground.*"%>
<% 
    // just for test
    request.setCharacterEncoding("utf-8"); 
    String token = request.getParameter("token");

    boolean authorized = true;
    if (token == null) {
        authorized = false;
    }
    String txtMsg = request.getParameter("save");
    String filename = request.getParameter("pid");

    // TODO: complete the interaction with backend here
    // List<String> M_list = Arrays.asList("name1", "name2", "name3");
    DBHandle handler;
    if (token != null) {
        handler=new DBHandle(token);
    }
    else handler=new DBHandle();
    List<String> M_list=handler.filemenu();
    if(filename==null && M_list != null && !M_list.isEmpty())
        filename=M_list.get(0);
    
    String content = handler.get_document_content(filename);
    if (txtMsg != null) {
        content = txtMsg;
    }

    // 新建文件
    String newfilename = request.getParameter("newfile");
    // 若newfile_result=0，则为默认值，不新建文件；
    // 若newfile_result=1，则新建失败，提示错误信息；
    // 若newfile_result=2，则新建成功；
    int newfile_result=0;   
    if(newfilename!=null)
    {
        if(handler.newfile(newfilename))
            newfile_result=2;
        else
            newfile_result=1;
    }

    // 删除文件
    String deletefilename = request.getParameter("deletefile");
    // 若deletefile_result=0，则为默认值，不删除文件；
    // 若deletefile_result=1，则删除失败，提示错误信息；
    // 若deletefile_result=2，则删除成功；
    int deletefile_result=0;   
    if(deletefilename!=null)
    {
        if(handler.delfile(deletefilename))
            deletefile_result=2;
        else
            deletefile_result=1;
    }

    // 重命名文件
    String refilename = request.getParameter("renamefile");
    String oldfilename = request.getParameter("oldfile");
    // 若refile_result=0，则为默认值，不重命名文件；
    // 若refile_result=1，则重命名失败，提示错误信息；
    // 若refile_result=2，则重命名成功；
    int refile_result=0;   
    if(refilename!=null)
    {
        if(handler.rename(oldfilename, refilename))
            refile_result=2;
        else
            refile_result=1;
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
    String Title = "Admin";

    StringBuilder menulist=new StringBuilder("");
    if (M_list != null && !M_list.isEmpty()) {
        for(String str: M_list)
        {
            String url1 = "main_edit.jsp?pid=" + str;
            menulist.append("<div><a class='" + "nav-list" + "'href='" + url1 + "'>" + str + "</a><button onclick='select(" + "'" + str + "'" + ") + '>del</button> </div>");
        }
    }
%>

<!DOCTYPE HTML>
<html>

<head>
    <meta charset="utf-8" />
    <%-- <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no" /> --%>
    <!-- ⚠️生产环境请指定版本号，如 https://cdn.jsdelivr.net/npm/vditor@x.x.x/dist... -->
    <link rel="stylesheet" href="/css/purple.css" />
    <link rel="stylesheet" href="/dist/index.css" />
    <link rel="stylesheet" href="/css/main.css" />
    <link rel="stylesheet" href="/css/purple.user.css" />
    <title><%=Title%></title>
</head>
<style>
    body {
        min-width: 100%;
        overflow: hidden;
        -webkit-text-size-adjust: none;
    }
    .vditor-reset {
        font-size: 16pt;
        -webkit-font-smoothing: antialiased;
        font-family: "SFMono-Medium", "FiraCode-Retina", -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
        padding: 10px 37px !important;
    }
    .vditor {
        --textarea-background-color: #fff;
        border-radius: 0px;
        width: 100% !important;
    }
    .vditor--dark {
        --panel-background-color: #121213;
        --textarea-background-color: #121213;
        --toolbar-background-color: #171a1d;
    }
    .vditor-outline {
        width: 27%;
    }
    .vditor--fullscreen {
        border: 0;
    }
    .vditor-reset h1 {
        border-bottom: 0px !important;
    }
    .vditor-reset ol, .vditor-reset ul {
        padding-left: 2.4em; 
    }
    .vditor-ir .vditor-reset>h2:before {
        content: "H2";
        margin-left: -48px;
    }
    .hljs::-webkit-scrollbar {
        display: none;
    }
</style>

<body>
    <div id="wrapper" style="display: inline-flex;">
        <div id="nav" style="min-width: 15em; display: block;" >
            <div id="list-item">
                <%out.print(menulist);%>
                <!-- <a class="nav-list" href="main_edit.jsp?save=hello">item1</a>
                <a class="nav-list" href="">item2</a>
                <a class="nav-list" href="">item3</a>
                <a class="nav-list" href="">item4</a> -->
                <form  method="post" action="main_edit.jsp" id ="passForm">  
                    <input class="nav-list" name="newfile" id="newfile" type="hidden" placeholder="newname.md">
                    <input type="hidden" name="oldfile" id="oldfile">
                </form>
            </div>
            <div id="tool-bar">    
                <button onclick="newfile()">新建</button>
                <button onclick="renamefile()">重命名</button>
                <button onclick="delfile()">删除</button>
                <button onclick="save()">保存</button>
            </div>
        </div>
        <!--  vditor--fullscreen -->
        <div  id="vditor" class="vditor "></div>
        <!-- <button onclick="= GetContent()">get content</button> -->
    </div>
    <form style="max-width=0px; height=0px; visibility: hidden;" method="post" action="main_edit.jsp" id ="passForm">  
        <input type="hidden" id = 'save' name="save" value="">  
        <input type="hidden" id="savefilename" name="savefilename">
    </form>  
    <script src="/dist/index.min.js">
    </script>
    
    <script>
        var authorization = "<%out.print(authorized);%>"
        if (authorization == "false") {
            alert("尚未登录!");
            window.location = "index.jsp";
        }
        alert("<%out.print(newfile_result);%>")
        var names_selected = null
        var edited = false
        var editor = new Vditor('vditor', {
            toolbarConfig: {
                pin: true
            },
            mode: 'wysiwyg',
            counter: {
                enable: true
            },
            height: window.innerHeight,
            tab: '    ',
            icon: 'ant',
            outline: {
                position: 'right'
            },
            preview: {
                mode: 'editor',
                hljs: {
                    style: 'vs'
                },
                theme: {
                    current: 'light'
                },
                markdown: {
                    toc: true,
                    mark: true
                },
                math: {
                    engine: 'MathJax'
                }
            },
            theme: 'classic',
            value: "",
            input: function(text, e) {
                console.log(this)
                if (edited == false) {
                    edited = true
                }
            },
            after: function() {
                // TODO: assign the content of first doc got from java to the editor
                var test = "<%out.print(content);%>";
                console.log(test)
                // var test = "${content}";
                // test = getStringFromHex(test);
                SetContent(test);
            }
        })
        
        function filename (name) {
            console.log("test")
            console.log(name)
            return name.replace(/[^(a-zA-Z0-9\u4e00-\u9fa5\.)]/g, '').
            replace(/[\?\\/:|<>\*\[\]\(\)\$%\{\}@~]/g, '').
            replace('/\\s/g', '')
        }
        

        // debug editor keyboard shortcut
        window.onkeydown=function(ev) {
            console.log(ev.key)
        }
        
        save = function() {
            document.getElementById('save').value = GetContent()
            alert(GetContent())
            var formObj = document.getElementById('passForm'); 
            formObj.submit(); 
        }

        newfile = function() {
            document.getElementById('newfile').type = 'text';
        }
        
        renamefile = function() {
            document.getElementById('newfile').type = 'text';
            document.getElementById('oldfile').value = names_selected;
        }

        delfile = function() {
            document.getElementById('newfile').type = 'text';
            document.getElementById('oldfile').value = names_selected;
        }

        select = function(str) {
            names_selected = str
        }

        GetContent = function() {
            return editor.getValue()
        }

        SetContent = function(content) {
            editor.setValue(content)
            // 当且仅当以View的数据为准时会SetContent，所以需要把edited设为false
            edited = false
        }

        function handleChange(mediaQueryListEvent) {
            if (mediaQueryListEvent.matches) {
                // 用户切换到了暗色(dark)主题
                console.log("change to dark theme")
                editor.setTheme("dark", "dark", "monokai")
            } else {
                // 用户切换到了亮色(light)主题
                console.log("change to light theme")
                editor.setTheme("classic", "light", "vs")
            }
        }

        const mediaQueryListDark = window.matchMedia('(prefers-color-scheme: dark)');
        if (mediaQueryListDark.matches) {
            editor.setTheme("dark", "dark", "monokai")
        }
        // 添加主题变动监控事件
        mediaQueryListDark.addListener(handleChange);
    </script>
</body>

</html>
