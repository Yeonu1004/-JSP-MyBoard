package sec03.brdLast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.SQLException;




public class BoardService {
	BoardDAO boardDAO;
	
	// 데이터베이스 연결을 위한 getConnection 메서드 정의
    private Connection getConnection() throws SQLException {
        String jdbcDriver = "jdbc:apache:commons:dbcp:pool";
        String dbUrl = "jdbc:oracle:thin:@localhost:1521:orcl";
        String dbUser = "ezen";
        String dbPass = "12345";
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }

    // getArticle 메서드 정의
    public ArticleVO getArticle(int articleNO) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArticleVO article = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement("select * from t_board where articleNO = ?");
            pstmt.setInt(1, articleNO);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                article = new ArticleVO();
                article.setArticleNO(rs.getInt("articleNO"));
                article.setParentNO(rs.getInt("parentNO"));
                article.setId(rs.getString("id"));
                article.setTitle(rs.getString("title"));
                article.setContent(rs.getString("content"));
                article.setWriteDate(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) try {rs.close();} catch(SQLException e) {}
            if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
            if (conn != null) try {conn.close();} catch(SQLException e) {}
        }
        return article;
    }
	 


	public BoardService() {
		boardDAO = new BoardDAO();
	}

	public Map listArticles(Map<String, Integer> pagingMap) {
		Map articlesMap = new HashMap();
		List<ArticleVO> articlesList = boardDAO.selectAllArticles(pagingMap);
		int totArticles = boardDAO.selectTotArticles();
		articlesMap.put("articlesList", articlesList);
		articlesMap.put("totArticles", totArticles);
		//articlesMap.put("totArticles", 170);
		return articlesMap;
	}

	public List<ArticleVO> listArticles() {
		List<ArticleVO> articlesList = boardDAO.selectAllArticles();
		return articlesList;
	}

	public int addArticle(ArticleVO article) {
		return boardDAO.insertNewArticle(article);
	}

	public ArticleVO viewArticle(int articleNO) {
		ArticleVO article = null;
		article = boardDAO.selectArticle(articleNO);
		return article;
	}

	public void modArticle(ArticleVO article) {
		boardDAO.updateArticle(article);
	}

	public List<Integer> removeArticle(int articleNO) {
		List<Integer> articleNOList = boardDAO.selectRemovedArticles(articleNO);
		boardDAO.deleteArticle(articleNO);
		return articleNOList;
	}

	public int addReply(ArticleVO article) {
	    return boardDAO.insertReply(article);
	}
	
	
	
	

}
