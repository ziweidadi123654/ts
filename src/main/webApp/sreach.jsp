<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>搜索</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

</head>

<body>
<div style="padding-top: 200px;padding-left: 500px" >
        <h1 style="padding-left:50px">欢迎使用唐诗检索系统</h1>
    <form action="${pageContext.request.contextPath}/p/poerty" method="post">
        <input type="text" name="text"  style="width:400px;height:50px;">
        <input type="submit" value="搜索" style="width:50px;height:50px;">
    </form>
</div>
</body>
</html>
