<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>리빙포인트 게시판</title>
<script src="https://code.jquery.com/jquery-3.4.1.js"></script>
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
<link rel="stylesheet" href="/css/nav.css">
<link rel="stylesheet" href="/css/footer.css">
<link rel="stylesheet" href="/css/application.css">
<style>
 /* 메뉴 폰트 */
        @font-face { font-family: 'MapoPeacefull'; src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2001@1.1/MapoPeacefullA.woff') format('woff'); font-weight: normal; font-style: normal; }
        html, body { margin: 0px; padding: 0px;}
        * {
             box-sizing: border-box; 
        }
        #board {
            margin: 65px 110px 0 110px;
        }
        #bHeader {
            background-color: #0085cb;
            border-radius: 10px 10px 0 0;
        }
        .bRow {
            display: flex;
            justify-content: flex-start;
            align-items: center;
        }
        .bRow * {
            line-height: 50px;
            text-align: center;
        }
        .bRow>span:first-child {
            flex-basis: 10%;
        }
        .bRow>span:nth-child(2) {
            flex-basis: 50%;
        }
        .bRow>span:nth-child(3) {
            flex-basis: 10%;
            text-align: left;
        }
        .bRow a {
            text-decoration: none;
        }
        .bRow>span:nth-child(4) {
            flex-basis: 20%;
        }
        .bRow>span:last-child {
            flex-basis: 10%;
        }
        .bRow:last-child {
            border-radius: 0 0 10px 10px;
            background-color: #dce3e8;
        }
        #btns {
            margin: 20px 110px 0 110px;
            display: flex;
            justify-content: space-between;
        }
        #btns>button:first-child, #btns>div>button {
            border: none;
            width: 50px;
            height: 30px;
            border-radius: 10px;
        }
        #btns>div>input {
            width: 300px;
            height: 30px;
            border-radius: 10px;
            border: 1px solid gray;
        }
        #btns>button:last-child {
            border: none;
            width: 80px;
            height: 30px;
            border-radius: 10px;
        }
        @media ( max-width: 600px ) {
            #board {
                margin: 65px 0 0 0;
            }
            #bHeader {
                border-radius: 0;
            }
            .bRow>span:first-child {
                display: none;
            }
            #btns {
                margin: 20px 0 0 0;
            }
        }
        .pageNavi{
        	text-decoration: none;
        }
        .pageNavi:hover {
        	color: pink;
        }
        .titleList:hover{
        	color: #de8c98;
        }
</style>
</head>
<body>
<jsp:include page="/resources/jsp/nav.jsp"/>
<div id="mainWrapper">
<br><br>
	<h1 style="text-align:center;">Living Point</h1>
	<br>
	<h4 style="text-align:center;">나만의 싱글생활 꿀팁을 공유하세요!</h4>
	<div id="board">
        <div id="bHeader" class="bRow">
            <span>카테고리</span>
            <span>제목</span>
            <span>작성자</span>
            <span>작성일</span>
            <span>조회수</span>
        </div>
     <c:forEach items="${list}" var="dto">
        <div class="bRow">
				<span> 
				<c:choose>
						<c:when test="${dto.category == 1}">요리</c:when>
						<c:when test="${dto.category == 2}">청소</c:when>
						<c:when test="${dto.category == 3}">건강</c:when>
						<c:when test="${dto.category == 4}">동식물</c:when>
						<c:otherwise>기타</c:otherwise>
				</c:choose>
				</span> 
			<span><a class="titleList" href="${pageContext.request.contextPath}/board/detailView.bo?seq=${dto.seq}">${dto.title}</a> [${dto.countSeq }]</span>
            <span>${dto.writer}</span>
            <span>${dto.getFormedDate()}</span>
            <span>${dto.viewCount}</span>
        </div>
     </c:forEach>   
        <div class="bRow" style="height:20px"></div>
   </div>
<form action="tipSearch.bo" method="get">
   <div id="btns">
      <button type="button" id="btnToBack">메인</button>
      <div>
      <select id="searchType" name="tipCategory">
         <option value="title">제목</option>
         <option value="contents">본문</option>
         <option value="both">제목+본문</option>
      </select> 
         <input type="text" placeholder="  search"  name="searchInput">
         <button type="submit">검색</button>
      </div>
      <button type="button" id="btnToWrite">글쓰기</button>
   </div>
</form>
<br>
<div id="paging" style="text-align:center;">${getNavi} </div>   
</div>
<br><br>

	<script>
		$("#btnToWrite").on("click",function() {
			location.href = "${pageContext.request.contextPath}/board/toBoardWrite.bo";
		})
		$("#btnToBack").on("click", function() {
			location.href = "${pageContext.request.contextPath}/";
		})
	</script>
	<jsp:include page="/resources/jsp/footer.jsp"></jsp:include>
</body>
</html>