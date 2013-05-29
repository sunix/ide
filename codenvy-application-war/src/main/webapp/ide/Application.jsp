<!--

    Copyright (C) 2012 eXo Platform SAS.

    This is free software; you can redistribute it and/or modify it
    under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation; either version 2.1 of
    the License, or (at your option) any later version.

    This software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this software; if not, write to the Free
    Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
    02110-1301 USA, or see the FSF site: http://www.fsf.org.

-->
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>IDE</title>

    <script type="text/javascript" language="javascript">
        var appConfig = {
            "context": "/w/rest/"
        }
        var hiddenFiles = ".*";
        var ws = "<%= request.getAttribute("ws")%>";
        var project = "<%= request.getAttribute("project")%>";
        var path = "<%= request.getAttribute("path")%>";
        var authorizationContext = "/w/rest";
        var authorizationPageURL = "/w/ide/" + ws;
        var authorizationErrorPageURL = "/w/ide/error_oauth.html";
        var securityCheckURL = "/w/j_security_check";
    </script>

    <script type="text/javascript" language="javascript" src='<%= com.codenvy.servlet.DispatcherServlet.genStaticResourceUrl(request, "ide.nocache.js")%>'></script>
    <link type="text/css" rel="stylesheet" href='<%= com.codenvy.servlet.DispatcherServlet.genStaticResourceUrl(request, "top-menu.css")%>' media="all">
</head>

<body>

<script type="text/javascript" language="javascript" src='<%= com.codenvy.servlet.DispatcherServlet.genStaticResourceUrl(request, "browserNotSupported.js")%>'></script>
<script type="text/javascript" language="javascript" src='<%= com.codenvy.servlet.DispatcherServlet.genStaticResourceUrl(request, "cloud_menu.js")%>'></script>

<div id="ide-menu-additions" align="right" class="ideMenuAdditions">
    <table cellspacing="0" cellpadding="0" border="0"
           class="ideMenuAdditionsTable">
        <tr id="ide-menu-additions-rows">
        </tr>
    </table>
</div>

<script type="text/javascript">
    function addMenuAddition(html) {
        var tr = document.getElementById("ide-menu-additions-rows");
        var td = document.createElement("td");
        td.innerHTML = html;
        tr.appendChild(td);
    }
</script>

<!-- LOGOUT, SHELL links -->
<script type="text/javascript">
    var htmlShell = "<a id=\"shell-link\" href=\"/shell/Shell.html\" target=\"_blank\">Shell</a>";
    addMenuAddition(htmlShell);

    var htmlLogout = "<span id=\"logoutButton\" onClick=\"window.location = '/site/logout.jsp';\">Logout</span>";
    addMenuAddition(htmlLogout);
</script>
<script type="text/javascript">
    var uvOptions = {};
    (function () {
        var uv = document.createElement('script');
        uv.type = 'text/javascript';
        uv.async = true;
        uv.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'widget.uservoice.com/jWE2fqGrmh1pa5tszJtZQA.js';
        var s = document.getElementsByTagName('script')[0];
        s.parentNode.insertBefore(uv, s);
    })();
</script>
</body>

</html>
