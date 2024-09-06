<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상세 페이지</title>
</head>
<body>
	<table>
		<tr>
			<th>부서번호</th>
			<td>${dept.deptno}</td>
		</tr>
		<tr>
			<th>부서명</th>
			<td>${dept.dname}</td>
		</tr>
		<tr>
			<th>부서위치</th>
			<td>${dept.loc}</td>
		</tr>
	</table>
</body>
</html>