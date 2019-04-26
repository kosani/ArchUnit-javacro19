package hr.kosani.archunit.model;

import java.util.Date;

public class Comment {
    private Long id;
    private Long postId;
    private String usersEMail;
    private String message;
    // TODO Remark ? prefer LocalDateTime to Date
    private Date postedOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsersEMail() {
        return usersEMail;
    }

    public void setUsersEMail(String usersEMail) {
        this.usersEMail = usersEMail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(Date postedOn) {
        this.postedOn = postedOn;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
