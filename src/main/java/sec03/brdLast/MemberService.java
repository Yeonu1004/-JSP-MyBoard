package sec03.brdLast;

import sec03.brdLast.MemberDAO;
import sec03.brdLast.MemberVO;



import java.sql.SQLException;

public class MemberService {
    private MemberDAO memberDAO;

    public MemberService() {
        memberDAO = new MemberDAO();
    }

    public boolean checkMember(String id, String pwd) {
        boolean result = false;
        try {
            result = memberDAO.checkMember(id, pwd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}