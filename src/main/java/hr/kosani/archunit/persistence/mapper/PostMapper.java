package hr.kosani.archunit.persistence.mapper;

import hr.kosani.archunit.model.Comment;
import hr.kosani.archunit.model.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostMapper {

    public static List<Post> toPosts(ResultSet resultSet) throws SQLException {
        List<Post> posts = new ArrayList<>();
        while (resultSet.next()) {
            posts.add(toPost(resultSet));
        }
        return posts;
    }

    public static Post toPost(ResultSet resultSet) throws SQLException {
        Post post = new Post();
        post.setId(resultSet.getLong("id"));
        post.setCreatedAt(resultSet.getObject("createdAt", LocalDateTime.class));
        post.setLastModifiedAt(resultSet.getObject("last_modified_at", LocalDateTime.class));
        post.setTitle(resultSet.getString("title"));
        post.setText(resultSet.getString("text"));
        return post;
    }

    public static List<Comment> toComments(ResultSet resultSet) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        while (resultSet.next()) {
            comments.add(toComment(resultSet));
        }
        return comments;
    }

    public static Comment toComment(ResultSet resultSet) throws SQLException {
        Comment comment = new Comment();
        comment.setId(resultSet.getLong("id"));
        comment.setPostId(resultSet.getLong("post_id"));
        comment.setMessage(resultSet.getString("message"));
        comment.setPostedOn(resultSet.getDate("posted_on"));
        comment.setUsersEMail(resultSet.getString("user_email"));
        return comment;
    }
}
