package hr.kosani.archunit.persistence.comment;

import hr.kosani.archunit.model.Comment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class CommentMapper {

    static List<Comment> toComments(ResultSet resultSet) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        while (resultSet.next()) {
            comments.add(toComment(resultSet));
        }
        return comments;
    }

    static Comment toComment(ResultSet resultSet) throws SQLException {
        Comment comment = new Comment();
        comment.setId(resultSet.getLong("id"));
        comment.setPostId(resultSet.getLong("post_id"));
        comment.setMessage(resultSet.getString("message"));
        comment.setPostedOn(resultSet.getObject("posted_on", LocalDateTime.class));
        comment.setUsersEMail(resultSet.getString("user_email"));
        return comment;
    }
}