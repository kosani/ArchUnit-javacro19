package hr.kosani.archunit.domain;

import hr.kosani.archunit.model.Comment;
import hr.kosani.archunit.model.Post;

public interface BloggingService {
    Post findPostById(Long id);

    Long savePost(Post post);

    Post findPostWithCommentsById(Long id);

    void deletePost(Long id);

    Long saveComment(Comment comment);

    void deleteComment(Long id);
}
