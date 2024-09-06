<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>테스트</title>
</head>
<body>
	<table>
			<tr>
				<th>부서번호</th>
				<td><input type="text" value="${dept.deptno}"></td>
			</tr>
			<tr>
				<th>부서명</th>
				<td><input type="text" name="dname" value=${dept.dname}></td>
			</tr>
			<tr>
				<th>부서위치</th>
				<td><input type="text" value="${dept.loc}"></td>
			</tr>
		</table>
</body>
</html>