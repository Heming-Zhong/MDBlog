<%@ page contentType="text/html;charset=utf-8"%>
<% 
    // just for test
    String content = "'# Header1 Header2'"; 
    String Title = "'Admin'";

    // TODO: complete the interaction with backend here
%>

<!DOCTYPE HTML>
<html>

<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no" />
    <!-- ⚠️生产环境请指定版本号，如 https://cdn.jsdelivr.net/npm/vditor@x.x.x/dist... -->
    <link rel="stylesheet" href="/src/purple.css" />
    <link rel="stylesheet" href="/dist/index.css" />
    <link rel="stylesheet" href="/src/purple.user.css" />
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
        <div id="preview"></div>
    </div>
    <script src="/dist/method.min.js">
    </script>
    
    <script>
        Vditor.preview(document.getElementById("preview"), <%=content%>)
        
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

        SetContent = function(content) {
            Vditor.preview(document.getElementById("preview"), content)
        }

        // TODO: Preview模式下的亮暗主题切换有bug
        function handleChange(mediaQueryListEvent) {
            if (mediaQueryListEvent.matches) {
                // 用户切换到了暗色(dark)主题
                console.log("change to dark theme")
                Vditor.setContentTheme("dark", "/dist/css/content-theme")
            } else {
                // 用户切换到了亮色(light)主题
                console.log("change to light theme")
                Vditor.setContentTheme("light", "/dist/css/content-theme")
            }
        }

        const mediaQueryListDark = window.matchMedia('(prefers-color-scheme: dark)');
        if (mediaQueryListDark.matches) {
            console.log('test')
            Vditor.setContentTheme("dark", "/dist/css/content-theme")
        }
        // 添加主题变动监控事件
        mediaQueryListDark.addListener(handleChange);
    </script>
</body>

</html>
