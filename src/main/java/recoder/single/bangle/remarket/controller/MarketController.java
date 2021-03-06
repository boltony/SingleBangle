package recoder.single.bangle.remarket.controller;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import recoder.single.bangle.member.DTO.MemberDTO;
import recoder.single.bangle.remarket.DAO.MarketDAO;
import recoder.single.bangle.remarket.DAO.MarketFileDAO;
import recoder.single.bangle.remarket.DAO.MarketReplyDAO;
import recoder.single.bangle.remarket.DTO.MarketDTO;
import recoder.single.bangle.remarket.DTO.MarketFileDTO;
import recoder.single.bangle.remarket.DTO.MarketReplyDTO;
import recoder.single.bangle.remarket.service.MarketReplyService;
import recoder.single.bangle.remarket.service.MarketService;
import recoder.single.bangle.tipBoard.DTO.FileDTO;
import recoder.single.bangle.tipBoard.DTO.ReportDTO;
import utils.Configuration;
import utils.XSSprotect;

@Controller
@RequestMapping("/market")
public class MarketController {

	@Autowired
	private MarketReplyDAO rdao;

	@Autowired
	private MarketDAO dao;

	@Autowired
	private MarketService service;

	@Autowired
	private MarketReplyService reService;

	@Autowired
	private MarketFileDAO file_dao;

	@Autowired
	private HttpSession session;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpServletResponse response;

	@RequestMapping("/boardList.do") //게시글 전체 리스트
	public String board(Model model, MarketDTO dto) {
		try {
			String navi = dao.getPageNavi(1);
			int cpage=1;
			String page = request.getParameter("cpage");
			if(page != null) {
				cpage = Integer.parseInt(page);
			}
			int start = cpage * Configuration.recordCountPerPage - (Configuration.recordCountPerPage -1 );
			int end = cpage * Configuration.recordCountPerPage;
			List<MarketDTO> navilist = dao.selectByPage(start, end);
			System.out.println(start + " : " + end);
			System.out.println("navilist.size() : " + navilist.size());
			model.addAttribute("navilist", navilist);
			model.addAttribute("navi", navi);

			List<MarketFileDTO> fileList = file_dao.selectByPage(start, end);
			List<MarketDTO> list = service.board();
			model.addAttribute("fileList", fileList);
			model.addAttribute("list", list);
			return "market/marketList";

		}catch(Exception e) {
			e.printStackTrace();
			return "redirect:/error";
		}
	}

	@RequestMapping("/delete.do")
	public String delete() {
		try {
			int seq = Integer.parseInt(request.getParameter("seq"));
			System.out.println("삭제시퀀스 : " + seq);
			service.delete(seq);
			//댓글삭제
			int board_seq = seq;
			file_dao.delete(board_seq); //board삭제할때 file에서도 삭제
			int boardSeq = board_seq;
			reService.deleteUseBoardSeq(boardSeq);
			return "redirect:/market/boardList.do";
		}catch(Exception e) {
			e.printStackTrace();
			return "redirect:/error";
		}
	}

	@RequestMapping("/update.do")
	public String update(Model model) {
		try {
			int seq = Integer.parseInt(request.getParameter("seq"));
			System.out.println("수정할 글 번호 : " + seq); //ok
			MarketDTO dto = service.writedetail(seq);
			model.addAttribute("dto", dto);
			return "market/updatemarket";
		}catch(Exception e) {
			e.printStackTrace();
			return "redirect:/error";
		}
	}

	@RequestMapping("/updateProc.do")//게시글 업데이트
	public String updateProc(MarketDTO dto, String path) {
		try {
			System.out.println("업데이트프록 컨트롤러");
			System.out.println(dto.getSeq());
			MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");
			String writer = loginInfo.getId();
			path = session.getServletContext().getRealPath("files");
			System.out.println(dto.getContent());
			String content = dto.getContent();
			content.replaceAll("<script", "&lt;script");
			System.out.println("content : " + content);
			service.updateProc(dto, content, writer, path);
			return "redirect:/market/boardList.do";
		}catch(Exception e) {
			e.printStackTrace();
			return "redirect:/error";
		}
	}

	@RequestMapping("/writeboard.do") //게시판에서 글쓰기버튼 클릭
	public String writeBoard() {
		return "market/writemarket";
	}

	@RequestMapping("/updateSellDone")//판매완료누르기
	@ResponseBody
	public String updateDone() {
		try {
			int seq = Integer.parseInt(request.getParameter("seq"));
			System.out.println("판매완료 보드 : " + seq);
			MarketDTO dto = service.updateSellDone(seq);
			Gson g = new Gson();
			String json = g.toJson(dto);
			System.out.println(json);
			return "json";
		}catch (Exception e) {
			e.printStackTrace();
			return "redirect:/error";
		}
	}

	@RequestMapping("/report.do")//신고하기 누르기
	public String report(Model model) {
		try {
			MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");
			String id = loginInfo.getId();
			int seq = Integer.parseInt(request.getParameter("seq"));
			String reportedUrl = request.getParameter("url");
			System.out.println(reportedUrl);
			model.addAttribute("id", id);
			model.addAttribute("seq", seq);
			model.addAttribute("reportedUrl", reportedUrl);
			return "market/reportPage";
		}catch(Exception e) {
			e.printStackTrace();
			return "redirect:/error";
		}
	}

	@RequestMapping("/reportProc.do")//신고사유 받아오기
	public String reportProc(Model model, ReportDTO dto) {
		try {
			MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");
			String id = loginInfo.getId();
			String reason = request.getParameter("reason");
			String reportedUrl = request.getParameter("reportedUrl");
			service.reportProc(id, dto);
			return "redirect:/";
		}catch(Exception e) {
			e.printStackTrace();
			return "redirect:/error";
		}
	}

	@RequestMapping("/write.do") //writeboard에서 글쓰기 버튼 클릭
	public String write(Model model, MarketDTO dto) {
		try {
			MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");
			String writer = loginInfo.getId();
			String gender = loginInfo.getGender();
			String realPlace = loginInfo.getAddress1();
			String[] placeSplit = realPlace.split(" ");
			String place = placeSplit[0]+ " " + placeSplit[1] + " " + placeSplit[2];
			String path = session.getServletContext().getRealPath("files");
			String content = dto.getContent();			
			content.replaceAll("<script", "&lt;script");
			System.out.println("content : " + content.length());
			service.write(dto, content, writer, place, gender, path);
			List<MarketDTO> list = service.board();
			model.addAttribute("list", list);
			return "redirect:/market/boardList.do";

		}catch(Exception e) {
			e.printStackTrace();
			return "redirect:/error";
		}
	};


	@RequestMapping("/marketDetail.do") //게시글상세
	public String writedetail(Model model, @RequestParam(value="cpage", required=false)String cpage_) {
		int cpage = 0;
		System.out.println("도착");
		try {
			if(cpage_ == null) {
				cpage = 1;
			}else {
				cpage = Integer.parseInt(cpage_);
			}
			System.out.println("cpage : " + cpage);
			int seq = Integer.parseInt(request.getParameter("seq"));
			MarketDTO dto = service.writedetail(seq); //seq값 넘기기
			model.addAttribute("dto", dto);
			StringBuffer url = request.getRequestURL();
			String realUrl = url + "?seq=" + seq;
			model.addAttribute("realUrl", realUrl);
			int boardSeq = seq;

			//댓글
			List<MarketReplyDTO> list = reService.list(boardSeq);
			model.addAttribute("list", list);
			System.out.println("이 글의 댓글 갯수 : " + list.size());
			String navi = rdao.getPageNavi(1, boardSeq);

			int start = cpage * Configuration.recordCountPerPage - (Configuration.recordCountPerPage -1);
			int end = cpage * Configuration.recordCountPerPage;
			List<MarketReplyDTO> renavilist = rdao.selectByPage(start, end, boardSeq);
			System.out.println(start + " : " + end);
			System.out.println("renavilist" + renavilist.size());
			model.addAttribute("renavilist", renavilist);
			model.addAttribute("navi", navi);
			return "market/marketdetail";

		}catch(Exception e) {
			e.printStackTrace();
			return "redirect:/error";
		}
	}

	@RequestMapping("/search.do")//게시글 검색하기
	public String search(Model model) {
		String category = (String) request.getParameter("category");
		String title = (String) request.getParameter("title");
		System.out.println("검색시작 : " + category + " : " + title);
		try {
			//제목으로만 검색하기
			if(category.contentEquals("전체")) { 
				System.out.println("전체&제목 검색 : " + title + category);
				List<MarketDTO> list = service.searchNoCategory(title);
				System.out.println("제목검색 갯수 : " + list.size());//size ok
				model.addAttribute("list", list);
				String navi = dao.getPageNaviUseTitle(1, title);
				System.out.println("navi : " + navi);
				model.addAttribute("navi", navi);
				int cpage=1;
				String page = request.getParameter("cpage");
				if(page != null) {
					cpage = Integer.parseInt(page);
				}
				int start = cpage * Configuration.recordCountPerPage - (Configuration.recordCountPerPage -1 );
				int end = cpage * Configuration.recordCountPerPage;
				System.out.println(start + " :: " + end + " ::" + title);
				List<MarketDTO> navilist = dao.selectByPageUseTitle(title, start, end);
				System.out.println("검색된 갯수 : " + navilist.size());
				model.addAttribute("navilist", navilist);
				List<MarketFileDTO> fileList = new ArrayList<>();

				for(int i = 0; i < navilist.size(); i++) {
					//				System.out.println(navilist.get(i).getSeq());
					int board_seq = navilist.get(i).getSeq();
					System.out.println("검색된시퀀스 : " + board_seq);
					fileList.add(file_dao.selectByPageUseTitle(board_seq, start, end));
				}
				model.addAttribute("fileList", fileList);

				return "market/marketList";
			}else if(title == "" || title == null) { //카테고리로 검색하기
				System.out.println("카테고리로 검색하기");
				List<MarketDTO> list = service.searchNoTitle(category);
				System.out.println("타이틀검색 갯수 : " + list.size());
				model.addAttribute("list", list);
				System.out.println("카테고리 검색 뭐라고됨? : " + category);
				String navi = dao.getPageNaviUseCategory(1, category);
				System.out.println("navi : " + navi); //출력ok
				model.addAttribute("navi", navi);
				int cpage=1;
				String page = request.getParameter("cpage");
				if(page != null) {
					cpage = Integer.parseInt(page);
				}
				int start = cpage * Configuration.recordCountPerPage - (Configuration.recordCountPerPage -1 );
				int end = cpage * Configuration.recordCountPerPage;
				List<MarketDTO> navilist = dao.selectByPageUseCategory(category, start, end);
				model.addAttribute("navilist", navilist);
				System.out.println("네비리스트사이즈 : " + navilist.size());
				List<MarketFileDTO> fileList = new ArrayList<>();
				for(int i = 0; i < navilist.size(); i++) {
					System.out.println(navilist.get(i).getSeq());
					System.out.println(start + " : " + end);
					int board_seq = navilist.get(i).getSeq();
					fileList.add(file_dao.selectByPageUseTitle(board_seq, start, end));
				}

				model.addAttribute("fileList", fileList);

				return "market/marketList";
			}
			else {//제목카테고리 같이 검색하기
				System.out.println("카테고리 제목 전부 선택했음ㅇㅇ");

				List<MarketDTO> list = service.search(title, category);
				System.out.println("타이틀, 카테고리 동시 검색 갯수 : " + list.size());
				model.addAttribute("list", list);
				String navi = dao.getPageNaviUseCaTi(1, category, title);
				System.out.println("navi : " + navi); //출력ok
				model.addAttribute("navi", navi);
				int cpage=1;
				String page = request.getParameter("cpage");
				if(page != null) {
					cpage = Integer.parseInt(page);
				}
				int start = cpage * Configuration.recordCountPerPage - (Configuration.recordCountPerPage -1 );
				int end = cpage * Configuration.recordCountPerPage;
				List<MarketDTO> navilist = dao.selectByPageUseCaTi(category, title, start, end);
				model.addAttribute("navilist", navilist);
				System.out.println("네비리스트사이즈 : " + navilist.size());
				List<MarketFileDTO> fileList = new ArrayList<>();
				for(int i = 0; i < navilist.size(); i++) {
					System.out.println(navilist.get(i).getSeq());
					System.out.println(start + " : " + end);
					int board_seq = navilist.get(i).getSeq();
					fileList.add(file_dao.selectByPageUseTitle(board_seq, start, end));
				}
				model.addAttribute("fileList", fileList);
				return "market/marketList";
			}

		}catch(Exception e) {
			e.printStackTrace();
			return "redirect:/error";
		}


	}


}
