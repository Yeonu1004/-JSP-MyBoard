package sec03.brdLast;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

@WebServlet("/board/*")
public class BoardController extends HttpServlet {
	private static String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";
	BoardService boardService;
	ArticleVO articleVO;

	public void init(ServletConfig config) throws ServletException {
		boardService = new BoardService();
		articleVO = new ArticleVO();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doHandle(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doHandle(request, response);
	}

	private void doHandle(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String nextPage = "";
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		HttpSession session;
		String action = request.getPathInfo();
		System.out.println("action:" + action);

		// Check if the user is logged in before showing articles
		if (action == null || action.equals("/listArticles.do")) {
			session = request.getSession(false);
			if (session == null || session.getAttribute("id") == null) {
				// User is not logged in, redirect to login page
				nextPage = "/board07/login.jsp";
				response.sendRedirect(request.getContextPath() + nextPage);
				return;
			}
		}

		try {
			List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
			if (action == null) {
				String _section = request.getParameter("section");
				String _pageNum = request.getParameter("pageNum");
				int section = Integer.parseInt(((_section == null) ? "1" : _section));
				int pageNum = Integer.parseInt(((_pageNum == null) ? "1" : _pageNum));
				Map<String, Integer> pagingMap = new HashMap<String, Integer>();
				pagingMap.put("section", section);
				pagingMap.put("pageNum", pageNum);
				nextPage = "/board07/listArticles.jsp";
			} else if (action.equals("/listArticles.do")) {
				// Code already modified to check if the user is logged in
				String _section = request.getParameter("section");
				String _pageNum = request.getParameter("pageNum");
				int section = Integer.parseInt(((_section == null) ? "1" : _section));
				int pageNum = Integer.parseInt(((_pageNum == null) ? "1" : _pageNum));
				Map<String, Integer> pagingMap = new HashMap<String, Integer>();
				pagingMap.put("section", section);
				pagingMap.put("pageNum", pageNum);
				Map articlesMap = boardService.listArticles(pagingMap);
				articlesMap.put("section", section);
				articlesMap.put("pageNum", pageNum);
				request.setAttribute("articlesMap", articlesMap);
				nextPage = "/board07/listArticles.jsp";
			} else if (action.equals("/articleForm.do")) {
				// Check if the user is logged in before showing article form
				session = request.getSession(false);
				if (session == null || session.getAttribute("id") == null) {
					// User is not logged in, redirect to login page
					nextPage = "/board07/login.jsp";
					response.sendRedirect(request.getContextPath() + nextPage);
					return;
				}
				nextPage = "/board07/articleForm.jsp";
			} else if (action.equals("/login.do")) {
				nextPage = "/board07/login.jsp";
			} else if (action.equals("/userlogin.do")) {
				String id = request.getParameter("user_id");
				String password = request.getParameter("user_pw");
				MemberService memberService = new MemberService();

				if (memberService.checkMember(id, password)) {
					// log-in succeed
					session = request.getSession();
					session.setAttribute("id", id);
					nextPage = "/board/listArticles.do";
				} else {
					// login failed
					String redirectUrl = request.getContextPath() + "/board07/login.jsp";
					response.sendRedirect(redirectUrl + "?message=loginFailed");
					return;
				}
			} else if (action.equals("/addArticle.do")) {
				int articleNO = 0;
				Map<String, String> articleMap = upload(request, response);
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");

				articleVO.setParentNO(0);
				HttpSession httpSession = request.getSession();
				String id = (String) httpSession.getAttribute("id");
				articleVO.setId(id);
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				articleNO = boardService.addArticle(articleVO);
				if (imageFileName != null && imageFileName.length() != 0) {
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('새글을 추가했습니다.');" + " location.href='" + request.getContextPath()
						+ "/board/listArticles.do';" + "</script>");

				return;
			} // View an article
			else if (action.equals("/viewArticle.do")) {
				String articleNO = request.getParameter("articleNO");
				articleVO = boardService.viewArticle(Integer.parseInt(articleNO));
				request.setAttribute("article", articleVO);
				RequestDispatcher dispatcher = request.getRequestDispatcher("/board07/viewArticle.jsp");
				dispatcher.forward(request, response);
			} // Modify an article
			else if (action.equals("/modArticle.do")) {
				Map<String, String> articleMap = upload(request, response);
				int articleNO = Integer.parseInt(articleMap.get("articleNO"));
				articleVO.setArticleNO(articleNO);
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				articleVO.setParentNO(0);
				HttpSession httpSession = request.getSession();
				String id = (String) httpSession.getAttribute("id");

				// Check if the user editing the post is the author
				ArticleVO originalArticle = boardService.viewArticle(articleNO);
				if (!originalArticle.getId().equals(id)) {
					PrintWriter pw = response.getWriter();
					pw.print("<script>alert('게시글을 수정하였습니다.')</script>");
					return;
				}

				articleVO.setId(id);
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				boardService.modArticle(articleVO);

				if (imageFileName != null && imageFileName.length() != 0) {
					String originalFileName = articleMap.get("originalFileName");
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);

					File oldFile = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + originalFileName);
					oldFile.delete();
				}

				PrintWriter pw = response.getWriter();
				pw.print("<script>alert('Your post has been edited.');location.href='" + request.getContextPath()
						+ "/board/viewArticle.do?articleNO=" + articleNO + "';</script>");
				return;

			} else if (action.equals("/removeArticle.do")) {
				int articleNO = Integer.parseInt(request.getParameter("articleNO"));
				HttpSession httpSession = request.getSession();
				String id = (String) httpSession.getAttribute("id");
				ArticleVO articleVO = boardService.viewArticle(articleNO);
				String originalFileName = articleVO.getImageFileName();

				if (articleVO.getId().equals(id)) {
					List<Integer> articleNOList = boardService.removeArticle(articleNO);
					for (int _articleNO : articleNOList) {
						File imgDir = new File(ARTICLE_IMAGE_REPO + "\\" + _articleNO);
						if (imgDir.exists()) {
							FileUtils.deleteDirectory(imgDir);
						}
					}

					if (originalFileName != null && originalFileName.length() != 0) {
						File oldFile = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + originalFileName);
						oldFile.delete();
					}

					// 게시글이 삭제되었다는 알림창을 띄우고 목록 페이지로 이동
					response.setContentType("text/html;charset=utf-8");
					PrintWriter pw = response.getWriter();
					pw.println("<script>");
					pw.println("alert('게시글이 삭제되었습니다.');");
					pw.println("location.href='" + request.getContextPath() + "/board/listArticles.do';");
					pw.println("</script>");
					return;
				} else {
					PrintWriter pw = response.getWriter();
					pw.print("<script>" + " alert('본인이 작성한 게시글만 삭제 가능합니다');" + " location.href='"
							+ request.getContextPath() + "/board/viewArticle.do?articleNO=" + articleNO + "';"
							+ "</script>");
					return;
				}
				
			} else if (action.equals("/replyForm.do")) {
				int parentNO = Integer.parseInt(request.getParameter("parentNO"));
				session = request.getSession();
				session.setAttribute("parentNO", parentNO);
				nextPage = "/board07/replyForm.jsp";
			} else if (action.equals("/addReply.do")) {
				session = request.getSession();
				Integer parentNO = (Integer) session.getAttribute("parentNO");
				if (parentNO == null) {
					// 오류 처리
					response.sendRedirect(request.getContextPath() + "/error.jsp");

				} else {
					// 이후 처리
					Map<String, String> articleMap = upload(request, response);
					String title = articleMap.get("title");
					String content = articleMap.get("content");
					String imageFileName = articleMap.get("imageFileName");
					ArticleVO articleVO = new ArticleVO(); // articleVO 변수 초기화
					articleVO.setParentNO(parentNO);
					HttpSession httpSession = request.getSession();
					String id = (String) httpSession.getAttribute("id");
					articleVO.setId(id);
					articleVO.setTitle(title);
					articleVO.setContent(content);
					articleVO.setImageFileName(imageFileName);
					int replyNO = boardService.addReply(articleVO); // replyNO 변수명 변경
					if (imageFileName != null && imageFileName.length() != 0) {
						File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
						File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + replyNO); // articleNO 변수명 변경
						destDir.mkdirs();
						FileUtils.moveFileToDirectory(srcFile, destDir, true);
					}
					PrintWriter pw = response.getWriter();
					pw.print("<script>" + "  alert('답글을 추가했습니다.');" + " location.href='" + request.getContextPath()
							+ "/board/viewArticle.do?articleNO=" + parentNO + "';" + "</script>");
					return;
				}
				session.removeAttribute("parentNO");

			}

			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String, String> upload(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, String> articleMap = new HashMap<String, String>();
		String encoding = "utf-8";
		File currentDirPath = new File(ARTICLE_IMAGE_REPO);
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(currentDirPath);
		factory.setSizeThreshold(1024 * 1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem fileItem = (FileItem) items.get(i);
				if (fileItem.isFormField()) {
					System.out.println(fileItem.getFieldName() + "=" + fileItem.getString(encoding));
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
				} else {
					System.out.println("파라미터명:" + fileItem.getFieldName());
					// System.out.println("파일명:" + fileItem.getName());
					System.out.println("파일크기:" + fileItem.getSize() + "bytes");
					// articleMap.put(fileItem.getFieldName(), fileItem.getName());
					if (fileItem.getSize() > 0) {
						int idx = fileItem.getName().lastIndexOf("\\");
						if (idx == -1) {
							idx = fileItem.getName().lastIndexOf("/");
						}

						String fileName = fileItem.getName().substring(idx + 1);
						System.out.println("파일명:" + fileName);
						articleMap.put(fileItem.getFieldName(), fileName); // 익스플로러에서 업로드 파일의 경로 제거 후 map에 파일명 저장);
						File uploadFile = new File(currentDirPath + "\\temp\\" + fileName);
						fileItem.write(uploadFile);

					} // end if
				} // end if
			} // end for
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articleMap;
	}

}
