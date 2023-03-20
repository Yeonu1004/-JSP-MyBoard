package sec03.brdLast;

import java.sql.Date;

public class ArticleVO {
    private int level, articleNO, parentNO, reply;
    private String title, content, imageFileName, id;
    private Date writeDate;
    
    public ArticleVO() {}
    
    public ArticleVO(int level, int articleNO, int parentNO, int reply, String title, String content, String imageFileName, String id, Date writeDate) {
        this.level = level;
        this.articleNO = articleNO;
        this.parentNO = parentNO;
        this.reply = reply;
        this.title = title;
        this.content = content;
        this.imageFileName = imageFileName;
        this.id = id;
        this.writeDate = writeDate;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getArticleNO() {
        return articleNO;
    }

    public void setArticleNO(int articleNO) {
        this.articleNO = articleNO;
    }

    public int getParentNO() {
        return parentNO;
    }

    public void setParentNO(int parentNO) {
        this.parentNO = parentNO;
    }

    public int getReply() {
        return reply;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(Date writeDate) {
        this.writeDate = writeDate;
    }
    
    
}
