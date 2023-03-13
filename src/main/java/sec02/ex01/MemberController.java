package sec02.ex01;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// @WebServlet("/member/*")
public class MemberController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MemberDAO memberDAO;

	public void init() throws ServletException {
		memberDAO = new MemberDAO();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doHandle(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doHandle(request, response);
	}

	
	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String nextPage = null;
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		String action = request.getPathInfo(); // URL에서 요청명을 가져옴
		System.out.println("action : " + action);
		
		/* 최초 요청이거나 action 값이 /listMembers.do면 회원 목록을 출력 */
		if (action == null || action.equals("/listMembers.do")) {
			List<MemberVO> membersList = memberDAO.listMembers();
			request.setAttribute("membersList", membersList);
			nextPage = "/test02/listMembers.jsp"; //test02폴더의 listMember.jsp로 포워딩함
			
		} else if (action.equals("/addMember.do")) { // action값이 /addMember.do면 전송된 회원정보를 가져와서 테이블에 추가한다
			String id = request.getParameter("id");
			String pwd = request.getParameter("pwd");
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			MemberVO memberVO = new MemberVO(id, pwd, name, email); // 새로운 memberVO를 생성해서
			memberDAO.addMember(memberVO); // addMember를 실행하면 memberVO를 실행한다
			nextPage = "/member/listMembers.do"; // 회원 등록 후 다시 회원 목록 출력
			
		} else if (action.equals("/memberForm.do")) {
			System.out.println("회원가입을 출력합니다");
			nextPage = "/test02/memberForm.jsp";
			
		} else { // 위 3가지 요청사항에 해당되지 않으면, listmember를 출력합니다
			List<MemberVO> membersList = memberDAO.listMembers();
			System.out.println("요청사항에 해당되지 않음 - listmember를 출력합니다");
			request.setAttribute("membersList", membersList);
			nextPage = "/test02/listMembers.jsp";
		}
		
		RequestDispatcher dispatch = request.getRequestDispatcher(nextPage); // nextPage에 지정한 요청명으로 다시 서블릿에 요청함
		dispatch.forward(request, response); // 컨트롤러에서 표시하고자 하는 jsp로 포워딩함.
		
	}

}
