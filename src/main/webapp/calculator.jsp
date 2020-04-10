<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import = "java.util.Locale" %>
<%@ page import="java.util.ResourceBundle" %>
<% ResourceBundle bundle = ResourceBundle.getBundle("config", new Locale("en", "US"));
    request.setAttribute("enable_division", bundle.getString("enable_division").equals("true"));
    request.setAttribute("enable_multiplication", bundle.getString("enable_multiplication").equals("true"));
    request.setAttribute("enable_subtraction", bundle.getString("enable_subtraction").equals("true"));
    request.setAttribute("enable_summary", bundle.getString("enable_summary").equals("true"));
%>
<html>
<head>
    <title>DevOps Calculator</title>
    <link rel="stylesheet" href="index.css">
</head>

<body>
<h1>DevOps Calculator</h1>
<div class="outer">
    <div class="container">

        <form method="GET" action="calculate.html">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td colspan="6">
                        <input class="display" type="text" id="display" value="<c:out value="${displayAmount}"/>"
                               readonly/>
                    </td>
                </tr>
                <tr>
                    <td><input class="btn" type="submit" name="button" id="btn-7" value="7"/></td>
                    <td><input class="btn" type="submit" name="button" id="btn-8" value="8"/></td>
                    <td><input class="btn" type="submit" name="button" id="btn-9" value="9"/></td>
                    <c:choose>
                        <c:when test='${enable_division}'>
                            <td><input class="btn" type="submit" name="button" id="btn-/" value="/"/></td>
                        </c:when>
                        <c:otherwise>
                            <td class="empty">
                        </c:otherwise>
                    </c:choose>
                    <td class="empty"></td>
                    <td><input class="btn" type="submit" name="button" id="btn-C" value="C"/></td>
                </tr>
                <tr>
                    <td><input class="btn" type="submit" name="button" id="btn-4" value="4"/></td>
                    <td><input class="btn" type="submit" name="button" id="btn-5" value="5"/></td>
                    <td><input class="btn" type="submit" name="button" id="btn-6" value="6"/></td>
                    <c:choose>
                        <c:when test='${enable_multiplication}'>
                            <td><input class="btn" type="submit" name="button" id="btn-*" value="*"/></td>
                        </c:when>
                        <c:otherwise>
                            <td class="empty">
                        </c:otherwise>
                    </c:choose>
                </tr>
                <tr>
                    <td><input class="btn" type="submit" name="button" id="btn-1" value="1"/></td>
                    <td><input class="btn" type="submit" name="button" id="btn-2" value="2"/></td>
                    <td><input class="btn" type="submit" name="button" id="btn-3" value="3"/></td>
                    <c:choose>
                        <c:when test='${enable_subtraction}'>
                            <td><input class="btn" type="submit" name="button" id="btn--" value="-"/></td>
                        </c:when>
                        <c:otherwise>
                            <td class="empty">
                        </c:otherwise>
                    </c:choose>
                    <td class="empty"></td>
                    <td rowspan="2"><input class="btn double_height" type="submit" name="button" id="btn-=" value="="/>
                </tr>
                <tr>
                    <td colspan="2"><input class="btn double_width" type="submit" name="button" id="btn-0" value="0"/></td>
                    <td><input class="btn" type="button" name="button" id="btn-." value="."/></td>
                    <c:choose>
                        <c:when test='${enable_summary}'>
                            <td><input class="btn" type="submit" name="button" id="btn-+" value="+"/></td>
                        </c:when>
                        <c:otherwise>
                            <td class="empty">
                        </c:otherwise>
                    </c:choose>
                </tr>
            </table>
        </form>
    </div>
    <div class="container">
        <h3>History</h3>
        ${records}
    </div>
</div>
</div>
</body>
</html>


