package hr.kosani.archunit.domain;

import hr.kosani.archunit.model.Comment;
import hr.kosani.archunit.model.Post;

import java.sql.SQLException;

public interface BloggingService {
    Post findPostById(Long id);

    Long savePost(Post post);

    Post findPostWithCommentsById(Long id) throws SQLException;

    void deletePost(Long id);

    Long saveComment(Comment comment) throws SQLException;

    void deleteComment(Long id) throws SQLException;
}
