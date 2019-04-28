package hr.kosani.archunit.persistence.comment;

import hr.kosani.archunit.model.Comment;

import java.sql.SQLException;
import java.util.List;

public interface CommentRepository {
    // TODO Remark 3: Read methods should be called by find.
    List<Comment> getAllByPostId(Long postId) throws SQLException;

    void deleteById(Long id);

    Comment save(Comment comment);
}
