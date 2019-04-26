package hr.kosani.archunit.domain;

import hr.kosani.archunit.Service;
import hr.kosani.archunit.model.Comment;
import hr.kosani.archunit.model.Post;
import hr.kosani.archunit.persistence.comment.CommentRepository;
import hr.kosani.archunit.persistence.post.PostRepository;

import javax.inject.Inject;
import java.sql.SQLException;

@Service
public class BloggingServiceImpl implements BloggingService {

    private final PostRepository postRepository;
    // TODO Remark 3: Should depend on interface rather than implementation
    private final CommentRepository commentRepository;

    @Inject
    public BloggingServiceImpl(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Post findPostById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Long savePost(Post post) {
        return postRepository.save(post).getId();
    }

    @Override
    public Post findPostWithCommentsById(Long id) throws SQLException {
        Post post = postRepository.findById(id);
        post.setComments(commentRepository.getAllByPostId(id));
        return post;
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public Long saveComment(Comment comment) throws SQLException {
        return commentRepository.save(comment).getId();
    }

    @Override
    public void deleteComment(Long id) throws SQLException {
        commentRepository.deleteById(id);
    }
}