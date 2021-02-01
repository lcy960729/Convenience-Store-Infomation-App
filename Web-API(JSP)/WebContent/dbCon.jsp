<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.DriverManager"%>
<%@ page import="java.sql.*"%>

<%
	boolean connection = false;

	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String sql = null;

	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:orcl";

	try {

		Class.forName(driver);

		con = DriverManager.getConnection(url, "c##SE", "1234");

		connection = true;

		//System.out.println("DB 연결 성공");

	} catch (Exception e) {

		connection = false;

		//System.out.println("DB 연결 실패");

		e.printStackTrace();

	}
%>