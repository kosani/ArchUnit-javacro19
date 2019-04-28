package hr.kosani.archunit.persistence.comment;

import hr.kosani.archunit.model.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> findAllByPostId(Long postId);

    void deleteById(Long id);

    Comment save(Comment comment);
}
