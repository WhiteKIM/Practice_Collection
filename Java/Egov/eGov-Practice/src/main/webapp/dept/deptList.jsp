<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>부서 리스트</title>
</head>
<style>
	table {
		width : 400px;
		border-collapse: collapse;/* 셀 사이 간격 제거 */
	}
	th, td {
		border : 1px solid #ccc;
		padding : 5px;
	}
</style>
<body>
	<table>
		<caption>부서 목록</caption>
		<tr>
			<th>부서번호</th>
			<th>부서명</th>
			<th>위치</th>
		</tr>
		<c:forEach var="dept" items="${deptlist}">
			<tr>
				<td>${dept.deptno}</td>
				<td><a href="/detail.do?deptno=${dept.deptno}">${dept.dname}</a></td>
				<td>${dept.loc}</td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>