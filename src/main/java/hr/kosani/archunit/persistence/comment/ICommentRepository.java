package hr.kosani.archunit.persistence.comment;

import hr.kosani.archunit.model.Comment;

import java.sql.SQLException;
import java.util.List;

// TODO Remark ?: Repositories should not have 'I' prefix
public interface ICommentRepository {
    // TODO read methods should be called by find
    List<Comment> getAllByPostId(Long postId) throws SQLException;

    void deleteById(Long id) throws SQLException;

    Comment save(Comment comment) throws SQLException;
}
