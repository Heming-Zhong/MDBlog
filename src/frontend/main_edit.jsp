<%@ page contentType="text/html;charset=utf-8"%>
<% 
    // just for test
    request.setCharacterEncoding("utf-8"); 
    String txtMsg = request.getParameter("save");
    String content = "# Header1 Header2\njifiejfiefj";
    if (txtMsg != null) {
        content = txtMsg;
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

    // TODO: complete the interaction with backend here
%>

<!DOCTYPE HTML>
<html>

<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no" />
    <!-- ⚠️生产环境请指定版本号，如 https://cdn.jsdelivr.net/npm/vditor@x.x.x/dist... -->
    <link rel="stylesheet" href="/css/purple.css" />
    <link rel="stylesheet" href="/dist/index.css" />
    <link rel="stylesheet" href="/css/purple.user.css" />
    <title><%=Title%></title>
</head>
<style>
    body {
        max-width: 100%;
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
    <div id="wrapper">
        <!--  vditor--fullscreen -->
        <div id="vditor" class="vditor "></div>
    </div>
    <button onclick="save()">submit</button>
    <form  method="post" action="main_edit.jsp" id ="passForm">  
        <input type="hidden" id = 'save' name="save" value="">  
    </form>  
    <script src="/dist/index.min.js">
    </script>
    
    <script>
        var edited = false
        var editor = new Vditor('vditor', {
            toolbarConfig: {
                pin: true
            },
            mode: 'wysiwyg',
            counter: {
                enable: true
            },
            height: window.innerHeight * 0.8,
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
