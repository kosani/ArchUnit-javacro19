package hr.kosani.archunit.domain;

import hr.kosani.archunit.Service;
import hr.kosani.archunit.model.Comment;
import hr.kosani.archunit.model.Post;
import hr.kosani.archunit.persistence.CommentRepository;
import hr.kosani.archunit.persistence.PostRepository;

import javax.inject.Inject;
import java.sql.SQLException;

@Service
public class BloggingService {

    private final PostRepository postRepository;

    @Inject
    public BloggingService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post findPostById(Long id) {
        return postRepository.findById(id);
    }

    public Long savePost(Post post) {
        return postRepository.save(post).getId();
    }

    public Post findPostWithCommentsById(Long id) throws SQLException {
        Post post = postRepository.findById(id);
        post.setComments(CommentRepository.getInstance().getAllByPostId(id));
        return post;
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public Long saveComment(Comment comment) throws SQLException {
        return CommentRepository.getInstance().save(comment).getId();
    }

    public void deleteComment(Long id) throws SQLException {
        CommentRepository.getInstance().deleteById(id);
    }
}
