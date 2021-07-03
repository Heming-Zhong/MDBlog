<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import="middleground.*"%>
<% 
    // just for test
    request.setCharacterEncoding("utf-8"); 
    String token = request.getParameter("token");
    

    boolean authorized = true;
    String txtMsg = request.getParameter("save");
    String filename = request.getParameter("pid");

    // TODO: complete the interaction with backend here
    // List<String> M_list = Arrays.asList("name1", "name2", "name3");
    DBHandle handler;
    if (token != null) {
        handler=new DBHandle(token);
    }
    else handler=new DBHandle();
    authorized = handler.getAuthority();
    List<String> M_list=handler.filemenu();
    if(filename == null && M_list != null && !M_list.isEmpty())
        filename = M_list.get(0);
    
    // 更新文件
    boolean updatefile_result = false;
    if (txtMsg != null && filename != null && !filename.isEmpty()) {
        updatefile_result = handler.update_file(filename, txtMsg);
    }
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
    if(newfilename!=null && !newfilename.isEmpty())
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
    if(deletefilename!=null && !deletefilename.isEmpty())
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
    if(refilename!=null && !refilename.isEmpty())
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
            //String url1 = "main_edit.jsp?pid=" + str + ";?token=" + token;
            //String url1 = "javascript:open(event)";
            String url1 = "open(&apos;"+ str +"&apos;);";
            String temp = "<a oncontextmenu='showMenu(event)' class='nav-list' href='javascript:" + url1 + "' >" + str + "</a>";
            //char[] tempchararr = temp.toCharArray();
            //for (char c : tempchararr) {
            //    String charToHex = "\\u" + String.format("%04X", new Integer(c));
            //    menulist.append(charToHex);
            //}
            menulist.append(temp);
            //menulist.append("<div class='nav-list'><a href='" + url1 + "'>" + str + "</a><button onclick='select(\'" + str + "\')'>del</button> </div>");
        }
    }
    String list = menulist.toString();
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
<body>
    <div class="contextmenu" id="context">
        <ul>
            <li><a href="javascript:renamefile();">重命名</a> </li>
            <li><a href="javascript:delfile();">删除</a></li>
        </ul>
    </div>
    <div id="wrapper" style="display: inline-flex; width: 100%;">
        <div id="nav" style="min-width: 15em; display: block;" >
            <div id="list-item">
                <%out.print(list);%>
                <%-- <a class="nav-list" href="main_edit.jsp?save=hello">item1</a> --%>
                <form  method="get" action="main_edit.jsp" id ="passForm">  
                    <input class="nav-list" name="newfile" id="newfile" type="hidden" placeholder="newname.md" >
                    <input class="nav-list" name="renamefile" id="renamefile" type="hidden" placeholder="newname.md">
                    <input type="hidden" name="deletefile" id="deletefile">
                    <input type="hidden" name="oldfile" id="oldfile">
                    <input type="hidden" name="token" id="token"> 
                    <input type="hidden" id = 'save' name="save">  
                    <input type="hidden" id = 'pid' name="pid">  
                    <input type="hidden" id="savefilename" name="savefilename">
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
    <script src="/dist/index.min.js">
    </script>
    
    <script>
        var templist = "<%out.print(list);%>"
        var token = "<%out.print(token);%>"
        var newfile_test = "<%out.print(filename);%>"
        alert(token)
        alert("<%out.print(list);%>")
        alert(newfile_test)
        var authorization = "<%out.print(authorized);%>"
        if (authorization == "false") {
            alert("尚未登录!");
            window.location = "index.jsp";
        }
        alert("<%out.print(updatefile_result);%>")
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
            document.getElementById('pid').value = "<%out.print(filename);%>"
            document.getElementById('token').value = "<%out.print(token);%>"
            alert(GetContent())
            var formObj = document.getElementById('passForm'); 
            formObj.submit(); 
        }

        open = function(filename) {
            alert(filename)
            document.getElementById('token').value = "<%out.print(token);%>"
            document.getElementById('pid').value = filename
            var formObj = document.getElementById('passForm');
            alert(document.getElementById("newfile").value) 
            formObj.submit(); 
        }

        newfile = function() {
            document.getElementById('newfile').type = 'text';
            document.getElementById('token').value = "<%out.print(token);%>"
        }
        
        renamefile = function() {
            document.getElementById('renamefile').type = 'text';
            document.getElementById('oldfile').value = names_selected;
            document.getElementById('token').value = "<%out.print(token);%>"
        }

        delfile = function() {
            document.getElementById('deletefile').value = names_selected;
            document.getElementById('token').value = "<%out.print(token);%>"
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

        function showMenu(env) {
            env.preventDefault();
            //env 表示event事件
            // 兼容event事件写法
            var e = env || window.event;
            names_selected = env.target.innerHTML
            alert(names_selected)

            // 获取菜单，让菜单显示出来
            var context = document.getElementById("context");
            context.style.display = "block";

            //  让菜单随着鼠标的移动而移动
            //  获取鼠标的坐标
            var x = e.clientX;
            var y = e.clientY;

            //  调整宽度和高度
            context.style.left = x - 200 + "px" //Math.min(w-202,x)+"px";
            context.style.top = y + "px" //Math.min(h-230,y)+"px";

            // return false可以关闭系统默认菜单
            return false;
        };
        // 当鼠标点击后关闭右键菜单
        document.onclick = function() {
            closeMenu()

        };

        function closeMenu() {
            var contextmenu = document.getElementById("context");
            contextmenu.style.display = "none";
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
