<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ include file="dbCon.jsp"%>

<%@ page import="java.util.*,java.text.*"%>

<%@ page import="java.sql.*"%>

<%@ page import="java.net.URLEncoder"%>

<%@ page import="org.json.simple.*"%>


<%
	request.setCharacterEncoding("UTF-8");
	String header = request.getParameter("header");

	try {
		if (header.equals("signUp")) {
			sql = "INSERT INTO USERS VALUES(?,?,?,?,?)";
			pstmt = con.prepareStatement(sql);
			String id = request.getParameter("id");
			pstmt.setString(1, id);
			String passwd = request.getParameter("passwd");
			pstmt.setString(2, passwd);
			String name = request.getParameter("name");
			pstmt.setString(3, name);
			String cellphone = request.getParameter("cellphone");
			pstmt.setString(4, cellphone);
			String email = request.getParameter("email");
			pstmt.setString(5, email);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");
			} else {
				out.print("false");
			}

		} else if (header.equals("getUser")) { //로그인
			sql = "select NAME,CELLPHONE,MAIL from users where ID = ?";
			pstmt = con.prepareStatement(sql);

			String search_Id = request.getParameter("id");

			pstmt.setString(1, search_Id);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				JSONObject obj = new JSONObject();

				String name = rs.getString("NAME");
				String cellphone = rs.getString("CELLPHONE");
				String mail = rs.getString("MAIL");
				
				obj.put("name", name);
				obj.put("cellphone", cellphone);
				obj.put("mail", mail);

				out.print(obj);
			} else
				out.print("false"); //존재하지 않는 정보입니다.
		} else if (header.equals("login")) { //로그인
			sql = "select passwd from users where ID = ?";
			pstmt = con.prepareStatement(sql);

			String search_Id = request.getParameter("id");
			String search_pwd = request.getParameter("pwd");

			pstmt.setString(1, search_Id);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				if (rs.getString(1).equals(search_pwd))
					out.print("true");
				else
					out.print("false"); //비밀번호가 일치하지 않습니다.
			} else
				out.print("false"); //존재하지 않는 정보입니다.
		} else if (header.equals("findIdPw")) { //로그인
			sql = "select id, passwd from users where cellphone = ?";
			pstmt = con.prepareStatement(sql);

			String search_phonenum = request.getParameter("phonenum");

			pstmt.setString(1, search_phonenum);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				out.println("회원님의 아이디는 '" + rs.getString("id") + "'");
				out.println("비밀번호는 '" + rs.getString("passwd") + "'입니다");
			} else
				out.print("존재하지 않는 정보 입니다"); //존재하지 않는 정보입니다.

		} else if (header.equals("getGoodsList")) { //편의점별 상품 가져오기
			String setConv = request.getParameter("conv");
			String setType = request.getParameter("type");
			String setSort = request.getParameter("sort");

			if (setConv.equals("전체"))
				setConv = "";
			else
				setConv = "WHERE CNAME = '" + setConv + "'";

			// 상품 분류
			if (setType.equals("상품 분류 - 전체"))
				setType = "";
			else
				setType = "TYPE = '" + setType + "'";

			// 문법 맞추주기
			if (setConv.equals("") && !setType.equals(""))
				setType = "WHERE " + setType;
			else if (!setConv.equals("") && !setType.equals(""))
				setType = "AND " + setType;

			// 정렬 방법
			if (setSort.equals("정렬 - 상품명"))
				setSort = "GNAME";
			else if (setSort.equals("낮은 가격순"))
				setSort = "PRICE DESC";
			else if (setSort.equals("높은 가격순"))
				setSort = "PRICE ";
			else if (setSort.equals("인기순"))
				setSort = "LOVE DESC";

			//sql = "SELECT bid, author ,title, content, to_char(wdate,'YYYY-MM-DD') WDATE, bhate, blike  FROM (SELECT ROWNUM num, bid, author ,title, content, wdate, bhate, blike FROM BOARD order by bid DESC) WHERE num BETWEEN ? and ?";
			sql = "SELECT PNO,GNAME,CNAME,IMAGEURL,PRICE,TYPE,EVENT,HATE,LOVE,to_char(UPDATES,'YYYY-MM-DD') UPDATES "+
				  "FROM (SELECT ROWNUM num, PNO, GNAME, PRICE, CNAME, TYPE, IMAGEURL,EVENT,LOVE,HATE,UPDATES FROM GOODS "
					+ setConv + setType + ") WHERE num BETWEEN ? AND ? ORDER BY " + setSort;

			pstmt = con.prepareStatement(sql);

			int page1 = Integer.parseInt(request.getParameter("page"));
			pstmt.setString(1, String.valueOf(((page1 - 1) * 10) + 1));
			pstmt.setString(2, String.valueOf(page1 * 10));

			rs = pstmt.executeQuery();
			JSONObject obj;
			JSONArray arr = new JSONArray();

			while (rs.next()) {
				obj = new JSONObject();

				String pno = rs.getString("PNO");
				String gname = rs.getString("GNAME");
				String cname = rs.getString("CNAME");
				String imageurl = rs.getString("IMAGEURL");
				String price = rs.getString("PRICE");
				String type = rs.getString("TYPE");
				String event = rs.getString("EVENT");
				String hate = rs.getString("HATE");
				String like = rs.getString("LOVE");
				String date = rs.getString("UPDATES");

				obj.put("pno", pno);
				obj.put("gname", gname);
				obj.put("cname", cname);
				obj.put("imageurl", imageurl);
				obj.put("price", price);
				obj.put("gtype", type);
				obj.put("event", event);
				obj.put("ghate", hate);
				obj.put("glike", like);
				obj.put("date", date);

				arr.add(obj);
			}
			out.println(arr);
		} else if (header.equals("getBoardList")) { //게시판 탭의 게시판리스트들'

			String setSort = request.getParameter("setSort");
			if (setSort.equals("BLIKE"))
				sql = "SELECT name, bid, author ,title, content, to_char(wdate,'YYYY-MM-DD') WDATE, bhate, BLIKE " + 
						  "FROM (SELECT ROWNUM num,name, bid, author, title, content, wdate, bhate, blike " +
								"FROM (SELECT name, bid, author ,title, content, wdate, bhate, blike " +
									  "FROM BOARD f, USERS u " +
						              "WHERE u.id = f.author order by blike desc)" +
						  		")" +
						  "Where num BETWEEN ? and ?";
			else
				sql = "SELECT name, bid, author ,title, content, to_char(wdate,'YYYY-MM-DD') WDATE, bhate, BLIKE " + 
					  "FROM (SELECT ROWNUM num,name, bid, author, title, content, wdate, bhate, blike " +
							"FROM (SELECT name, bid, author ,title, content, wdate, bhate, blike " +
								  "FROM BOARD f, USERS u " +
					              "WHERE u.id = f.author order by bid desc)" +
					  		")" +
					  "Where num BETWEEN ? and ?";

			pstmt = con.prepareStatement(sql);

			int page1 = Integer.parseInt(request.getParameter("page"));
			pstmt.setString(1, String.valueOf(((page1 - 1) * 10) + 1));
			pstmt.setString(2, String.valueOf(page1 * 10));
			rs = pstmt.executeQuery();

			JSONObject obj;
			JSONArray arr = new JSONArray();

			while (rs.next()) {
				obj = new JSONObject();
				String bid = rs.getString("bid");
				String title = rs.getString("TITLE");
				String author = rs.getString("AUTHOR");
				String name = rs.getString("name");
				String content = rs.getString("CONTENT");
				String wdate = rs.getString("WDATE");
				String bhate = rs.getString("BHATE");
				String blike = rs.getString("BLIKE");

				obj.put("bid", bid);
				obj.put("title", title);
				obj.put("id", author);				
				obj.put("writer", name);
				obj.put("content", content);
				obj.put("wdate", wdate);
				obj.put("bhate", bhate);
				obj.put("blike", blike);

				arr.add(obj);
			}
			out.println(arr);
		}

		else if (header.equals("getFriendsList")) { //친구목록
			sql = "select fid from friend where userid = ? order by fid DESC";
			pstmt = con.prepareStatement(sql);
			String choice_userid = request.getParameter("userid");
			pstmt.setString(1, choice_userid);
			rs = pstmt.executeQuery();

			JSONObject obj = new JSONObject();
			JSONArray arr = new JSONArray();

			if (rs.next()) {
				if (rs.getString(1).equals(choice_userid)) {
					String FID = rs.getString("FID");

					obj.put("fid", FID);

					arr.add(obj);

					out.println(obj);
				} else
					out.println("false");
			} else
				out.println("false");

		} else if (header.equals("favorgoods")) {//관심상품 목록가져오기
			sql = "SELECT PNO,GNAME,CNAME,IMAGEURL,PRICE,TYPE,EVENT,HATE,LOVE,to_char(UPDATES,'YYYY-MM-DD') UPDATES " +
				  "FROM FAVORGOODS f, GOODS g  WHERE f.pid = g.gname AND USERID = ?";
			pstmt = con.prepareStatement(sql);
			String userid = request.getParameter("userid");
			pstmt.setString(1, userid);
			rs = pstmt.executeQuery();

			JSONObject obj;
			JSONArray arr = new JSONArray();

			while (rs.next()) {
				obj = new JSONObject();

				String pno = rs.getString("PNO");
				String gname = rs.getString("GNAME");
				String cname = rs.getString("CNAME");
				String imageurl = rs.getString("IMAGEURL");
				String price = rs.getString("PRICE");
				String type = rs.getString("TYPE");
				String event = rs.getString("EVENT");
				String hate = rs.getString("HATE");
				String like = rs.getString("LOVE");
				String date = rs.getString("UPDATES");

				obj.put("pno", pno);
				obj.put("gname", gname);
				obj.put("cname", cname);
				obj.put("imageurl", imageurl);
				obj.put("price", price);
				obj.put("gtype", type);
				obj.put("event", event);
				obj.put("ghate", hate);
				obj.put("glike", like);
				obj.put("date", date);

				arr.add(obj);
			}
			out.println(arr);
		} else if (header.equals("writePost")) {
			sql = "INSERT INTO BOARD VALUES(index_num.nextval,?,?,?,SYSDATE,0,0)";
			pstmt = con.prepareStatement(sql);
			String writer = request.getParameter("writer");
			pstmt.setString(1, writer);
			String title = request.getParameter("title");
			pstmt.setString(2, title);
			String content = request.getParameter("content");
			pstmt.setString(3, content);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");
			} else {
				out.print("false");
			}
		} else if (header.equals("updatePost")) {
			sql = "UPDATE BOARD SET title=?, content=? where bid = ?";
			pstmt = con.prepareStatement(sql);
			String title = request.getParameter("title");
			pstmt.setString(1, title);
			String content = request.getParameter("content");
			pstmt.setString(2, content);
			String bid = request.getParameter("bid");
			pstmt.setString(3, bid);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");
			} else {
				out.print("false");
			}
		} else if (header.equals("deletePost")) {
			sql = "DELETE FROM BOARD WHERE bid = ? AND AUTHOR = ?";

			pstmt = con.prepareStatement(sql);
			String bid = request.getParameter("bid");
			pstmt.setString(1, bid);
			String writer = request.getParameter("writer");
			pstmt.setString(2, writer);

			if (pstmt.executeUpdate() == 1) {
				sql = "DELETE FROM COMMENTS WHERE bid = ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, bid);
				pstmt.executeUpdate();

				sql = "DELETE FROM PLOVEHATE WHERE bid = ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, bid);
				pstmt.executeUpdate();

				out.print("true");
			} else {
				out.print("false");
			}

		} else if (header.equals("inset_favoriteProduct")) {
			sql = "INSERT INTO FAVORGOODS VALUES(?,?)";
			pstmt = con.prepareStatement(sql);
			String id = request.getParameter("userid");
			pstmt.setString(1, id);
			String pid = request.getParameter("pid");
			pstmt.setString(2, pid);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");
			} else {
				out.print("false");
			}
		} else if (header.equals("delete_favoriteProduct")) {
			sql = "DELETE FROM FAVORGOODS WHERE USERID = ? AND PID = ?";
			pstmt = con.prepareStatement(sql);
			String id = request.getParameter("userid");
			pstmt.setString(1, id);
			String pid = request.getParameter("pid");
			pstmt.setString(2, pid);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");
			} else {
				out.print("false");
			}
		} else if (header.equals("insert_friends")) { //친구 추가
			sql = "insert into FRIEND values(?,?)";
			pstmt = con.prepareStatement(sql);
			String uid = request.getParameter("userid");
			String fid = request.getParameter("fid");
			pstmt.setString(1, uid);
			pstmt.setString(2, fid);
			if (pstmt.executeUpdate() == 1) {
				out.print("true");
			} else {
				out.print("false");
			}
		} else if (header.equals("delete_friend")) {
			sql = "DELETE FROM FRIEND WHERE USERID = ? AND FID = ?";
			pstmt = con.prepareStatement(sql);
			String id = request.getParameter("userid");
			pstmt.setString(1, id);
			String fid = request.getParameter("fid");
			pstmt.setString(2, fid);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");
			} else {
				out.print("false");
			}
		} else if (header.equals("find_friends")) { //친구  찾기  
			sql = "select id, name, mail from user where name = ?";
			pstmt = con.prepareStatement(sql);
			String fname = request.getParameter("name");
			pstmt.setString(1, fname);
			rs = pstmt.executeQuery();

			JSONObject obj;
			JSONArray arr = new JSONArray();

			while (rs.next()) {
				obj = new JSONObject();
				String id = rs.getString("id");
				String name = rs.getString("name");
				String mail = rs.getString("mail");
				obj.put("id", id);
				obj.put("name", name);
				obj.put("mail", mail);

				arr.add(obj);
			}
			out.println(arr);
		} else if (header.equals("get_friends")) { //친구 목록 불러오기  
			sql = "select id from FRIEND f, USERS u where u.name = f.fid AND f.userid = ?";
			pstmt = con.prepareStatement(sql);
			String userid = request.getParameter("userid");

			pstmt.setString(1, userid);
			rs = pstmt.executeQuery();

			JSONObject obj;
			JSONArray arr = new JSONArray();

			while (rs.next()) {
				obj = new JSONObject();
				String id = rs.getString("id");

				obj.put("id", id);

				arr.add(obj);
			}
			out.println(arr);

		} else if (header.equals("delete_board")) {
			sql = "DELETE FROM BOARD WHERE BID = ?";
			pstmt = con.prepareStatement(sql);
			String id = request.getParameter("bid");
			pstmt.setString(1, id);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");
			} else {
				out.print("false");
			}
		} else if (header.equals("get_comments")) { //특정 게시판의 댓글 전부 불러오는 것   
			sql = "select bid, contents, id, name, to_char(cdate,'YYYY-MM-DD HH:MI') cdate from COMMENTS g , USERS u where g.cwriter = u.id AND bid = ? ORDER BY CDATE DESC";
			pstmt = con.prepareStatement(sql);
			String bid = request.getParameter("bid");
			pstmt.setString(1, bid);
			rs = pstmt.executeQuery();

			JSONObject obj;
			JSONArray arr = new JSONArray();

			while (rs.next()) {
				obj = new JSONObject();
				String cbid = rs.getString("BID");
				String id = rs.getString("ID");
				String cwriter = rs.getString("NAME");
				;
				String contents = rs.getString("CONTENTS");
				String cdate = rs.getString("CDATE");

				obj.put("id", id);
				obj.put("bid", cbid);
				obj.put("cwriter", cwriter);
				obj.put("contents", contents);
				obj.put("cdate", cdate);

				arr.add(obj);
			}
			out.println(arr);
		} else if (header.equals("get_pcomments")) { //특정 게시판의 댓글 전부 불러오는 것   
			sql = "select pno,contents,id,name, to_char(cdate,'YYYY-MM-DD HH:MI') cdate from GCOMMENTS g , USERS u where g.cwriter = u.id AND pno = ? ORDER BY CDATE DESC";
			pstmt = con.prepareStatement(sql);
			String bid = request.getParameter("pno");
			pstmt.setString(1, bid);
			rs = pstmt.executeQuery();

			JSONObject obj;
			JSONArray arr = new JSONArray();

			while (rs.next()) {
				obj = new JSONObject();
				String cbid = rs.getString("PNO");
				String cwriter = rs.getString("name");
				String contents = rs.getString("CONTENTS");
				String cdate = rs.getString("CDATE");
				String id = rs.getString("ID");

				obj.put("id", id);
				obj.put("pno", cbid);
				obj.put("cwriter", cwriter);
				obj.put("contents", contents);
				obj.put("cdate", cdate);

				arr.add(obj);
			}
			out.println(arr);
		} else if (header.equals("insert_comment")) { //특정 게시판에 댓글을 추가 하는 것 
			sql = "insert into COMMENTS values(?,?,?,sysdate)";
			pstmt = con.prepareStatement(sql);
			String bid = request.getParameter("bid");
			String cwriter = request.getParameter("cwriter");
			String contents = request.getParameter("contents");
			pstmt.setString(1, bid);
			pstmt.setString(2, cwriter);
			pstmt.setString(3, contents);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");
			} else {
				out.print("false");
			}
		} else if (header.equals("insert_pcomment")) { //특정 게시판에 댓글을 추가 하는 것 
			sql = "insert into GCOMMENTS values(?,?,?,sysdate)";
			pstmt = con.prepareStatement(sql);
			String bid = request.getParameter("pno");
			String cwriter = request.getParameter("cwriter");
			String contents = request.getParameter("contents");
			pstmt.setString(1, bid);
			pstmt.setString(2, cwriter);
			pstmt.setString(3, contents);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");
			} else {
				out.print("false");
			}
		} else if (header.equals("delete_comment")) { //특정 게시판에 댓글을 추가 하는 것 
			sql = "delete from COMMENTS Where bid = ? AND cwriter = ? AND contents = ?";
			pstmt = con.prepareStatement(sql);
			String bid = request.getParameter("bid");
			String cwriter = request.getParameter("cwriter");
			String contents = request.getParameter("contents");
			pstmt.setString(1, bid);
			pstmt.setString(2, cwriter);
			pstmt.setString(3, contents);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");
			} else {
				out.print("false");
			}
		} else if (header.equals("delete_pcomment")) { //특정 게시판에 댓글을 추가 하는 것 
			sql = "delete from GCOMMENTS Where pno = ? AND cwriter = ? AND contents = ?";
			pstmt = con.prepareStatement(sql);
			String bid = request.getParameter("pno");
			String cwriter = request.getParameter("cwriter");
			String contents = request.getParameter("contents");
			pstmt.setString(1, bid);
			pstmt.setString(2, cwriter);
			pstmt.setString(3, contents);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");
			} else {
				out.print("false");
			}
		} else if (header.equals("pLove")) {
			String type = request.getParameter("type");
			String sql2;
			if (type.equals("u")) {
				sql = "UPDATE PLOVEHATE SET LOVE='true', HATE='false' WHERE userid = ? AND bid = ?";
				sql2 = "UPDATE BOARD SET BLIKE=BLIKE+1, BHATE=BHATE-1 WHERE bid = ?";
			} else {
				sql = "INSERT INTO PLOVEHATE VALUES(?, ?, 'true', 'false')";
				sql2 = "UPDATE BOARD SET BLIKE=BLIKE+1 WHERE bid = ?";
			}

			//SQL1 실행
			pstmt = con.prepareStatement(sql);
			String userid = request.getParameter("userid");
			pstmt.setString(1, userid);
			String bid = request.getParameter("bid");
			pstmt.setString(2, bid);
			pstmt.executeUpdate();
			pstmt.close();

			//SQL2 실행
			pstmt = con.prepareStatement(sql2);
			pstmt.setString(1, bid);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");

			} else {
				out.print("false");
			}
		} else if (header.equals("pHate")) {
			String type = request.getParameter("type");
			String sql2;
			if (type.equals("u")) {
				sql = "UPDATE PLOVEHATE SET LOVE='false', HATE='true' WHERE userid = ? AND bid = ?";
				sql2 = "UPDATE BOARD SET BLIKE=BLIKE-1, BHATE=BHATE+1 WHERE bid = ?";
			} else {
				sql = "INSERT INTO PLOVEHATE VALUES(?, ?, 'false', 'true')";
				sql2 = "UPDATE BOARD SET BHATE=BHATE+1 WHERE bid = ?";
			}

			//SQL1 실행
			pstmt = con.prepareStatement(sql);
			String userid = request.getParameter("userid");
			pstmt.setString(1, userid);
			String bid = request.getParameter("bid");
			pstmt.setString(2, bid);
			pstmt.executeUpdate();
			pstmt.close();

			//SQL2 실행
			pstmt = con.prepareStatement(sql2);
			pstmt.setString(1, bid);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");
			} else {
				out.print("false");
			}
		} else if (header.equals("dLoveHate")) {
			String type = request.getParameter("type");

			String sql2 = null;
			sql = "DELETE FROM PLOVEHATE WHERE userid = ? AND bid = ?";

			if (type.equals("love"))
				sql2 = "UPDATE BOARD SET BLIKE=BLIKE-1 WHERE bid = ?";
			else
				sql2 = "UPDATE BOARD SET BHATE=BHATE-1 WHERE bid = ?";

			//SQL1 실행
			pstmt = con.prepareStatement(sql);
			String userid = request.getParameter("userid");
			pstmt.setString(1, userid);
			String bid = request.getParameter("bid");
			pstmt.setString(2, bid);

			pstmt.executeUpdate();
			pstmt.close();

			//SQL2 실행
			pstmt = con.prepareStatement(sql2);
			pstmt.setString(1, bid);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");
				pstmt.executeUpdate();
			} else {
				out.print("false");
			}
		} else if (header.equals("gLove")) {
			String type = request.getParameter("type");
			String sql2;
			if (type.equals("u")) {
				sql = "UPDATE GLOVEHATE SET LOVE='true', HATE='false' WHERE userid = ? AND pno = ?";
				sql2 = "UPDATE GOODS SET LOVE=LOVE+1, HATE=HATE-1 WHERE pno = ?";
			} else {
				sql = "INSERT INTO GLOVEHATE VALUES(?, ?, 'true', 'false')";
				sql2 = "UPDATE GOODS SET LOVE=LOVE+1 WHERE pno = ?";
			}

			//SQL1 실행
			pstmt = con.prepareStatement(sql);
			String userid = request.getParameter("userid");
			pstmt.setString(1, userid);
			String pno = request.getParameter("pno");
			pstmt.setString(2, pno);
			pstmt.executeUpdate();
			pstmt.close();

			//SQL2 실행
			pstmt = con.prepareStatement(sql2);
			pstmt.setString(1, pno);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");

			} else {
				out.print("false");
			}
		} else if (header.equals("gHate")) {
			String type = request.getParameter("type");
			String sql2;
			if (type.equals("u")) {
				sql = "UPDATE GLOVEHATE SET LOVE='false', HATE='true' WHERE userid = ? AND pno = ?";
				sql2 = "UPDATE GOODS SET LOVE=LOVE-1, HATE=HATE+1 WHERE pno = ?";
			} else {
				sql = "INSERT INTO GLOVEHATE VALUES(?, ?, 'false', 'true')";
				sql2 = "UPDATE GOODS SET HATE=HATE+1 WHERE pno = ?";
			}

			//SQL1 실행
			pstmt = con.prepareStatement(sql);
			String userid = request.getParameter("userid");
			pstmt.setString(1, userid);
			String pno = request.getParameter("pno");
			pstmt.setString(2, pno);
			pstmt.executeUpdate();
			pstmt.close();

			//SQL2 실행
			pstmt = con.prepareStatement(sql2);
			pstmt.setString(1, pno);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");
			} else {
				out.print("false");
			}
		} else if (header.equals("gLoveHate")) {
			String type = request.getParameter("type");

			String sql2 = null;
			sql = "DELETE FROM GLOVEHATE WHERE userid = ? AND pno = ?";

			if (type.equals("love"))
				sql2 = "UPDATE GOODS SET LOVE=LOVE-1 WHERE pno = ?";
			else
				sql2 = "UPDATE GOODS SET HATE=HATE-1 WHERE pno = ?";


			//SQL1 실행
			pstmt = con.prepareStatement(sql);
			String userid = request.getParameter("userid");
			pstmt.setString(1, userid);
			String pno = request.getParameter("pno");
			pstmt.setString(2, pno);

			pstmt.executeUpdate();
			pstmt.close();

			//SQL2 실행
			pstmt = con.prepareStatement(sql2);
			pstmt.setString(1, pno);

			if (pstmt.executeUpdate() == 1) {
				out.print("true");
				pstmt.executeUpdate();
			} else {
				out.print("false");
			}
		} else if (header.equals("get_GLOVEHATE")) { //친구 목록 불러오기  
			sql = "select love,hate from GLOVEHATE where userid = ? AND pno = ?";
			pstmt = con.prepareStatement(sql);
			String userid = request.getParameter("userid");
			String pno = request.getParameter("pno");

			pstmt.setString(1, userid);
			pstmt.setString(2, pno);
			rs = pstmt.executeQuery();

			JSONObject obj = new JSONObject();

			if (rs.next()) {
				String love = rs.getString("LOVE");
				String hate = rs.getString("HATE");

				obj.put("love", love);
				obj.put("hate", hate);
			} else {
				obj.put("love", "false");
				obj.put("hate", "false");
			}
			//존재하지 않는 정보입니다.
			out.print(obj);

		} else if (header.equals("get_PLOVEHATE")) { //친구 목록 불러오기  
			sql = "select love,hate from PLOVEHATE where userid = ? AND bid = ?";
			pstmt = con.prepareStatement(sql);
			String userid = request.getParameter("userid");
			String bid = request.getParameter("bid");

			pstmt.setString(1, userid);
			pstmt.setString(2, bid);
			rs = pstmt.executeQuery();

			JSONObject obj = new JSONObject();

			if (rs.next()) {
				String love = rs.getString("LOVE");
				String hate = rs.getString("HATE");

				obj.put("love", love);
				obj.put("hate", hate);

			} else {
				obj.put("love", "false");
				obj.put("hate", "false");
			}
			//존재하지 않는 정보입니다.
			out.print(obj);
		} else if (header.equals("deleteUser")) {
			String userid = request.getParameter("userid");
			
			sql = "DELETE FROM USERS WHERE id = ?";
			//SQL1 실행
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userid);
			pstmt.executeUpdate();
			pstmt.close();

			sql = "DELETE FROM COMMENTS WHERE cwriter = ?";
			//SQL1 실행
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userid);
			pstmt.executeUpdate();
			pstmt.close();

			sql = "DELETE FROM GCOMMENTS WHERE cwriter = ?";
			//SQL1 실행
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userid);
			pstmt.executeUpdate();
			pstmt.close();

			sql = "DELETE FROM BOARD WHERE author = ?";
			//SQL1 실행
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userid);
			pstmt.executeUpdate();
			pstmt.close();

			sql = "DELETE FROM FRIEND WHERE userid = ? OR fid = ?";
			//SQL1 실행
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userid);
			pstmt.setString(2, userid);
			pstmt.executeUpdate();
			pstmt.close();

			sql = "DELETE FROM FAVORGOODS WHERE userid = ?";
			//SQL1 실행
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userid);
			pstmt.executeUpdate();
			pstmt.close();

			out.print("true");
		}
	} catch (SQLException se) {
		out.println(se.getMessage());
	} finally {
		if (rs != null)
			rs.close();

		if (pstmt != null)
			pstmt.close();

		if (con != null)
			con.close();
	}
%>