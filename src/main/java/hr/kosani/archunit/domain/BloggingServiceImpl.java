package hr.kosani.archunit.domain;

import hr.kosani.archunit.Service;
import hr.kosani.archunit.model.Comment;
import hr.kosani.archunit.model.Post;
import hr.kosani.archunit.persistence.comment.CommentRepositoryImpl;
import hr.kosani.archunit.persistence.post.PostRepository;

import javax.inject.Inject;

@Service
public class BloggingServiceImpl implements BloggingService {

    private final PostRepository postRepository;
    // TODO Remark 4: Should depend on interface rather than implementation
    private final CommentRepositoryImpl commentRepository;

    @Inject
    public BloggingServiceImpl(PostRepository postRepository, CommentRepositoryImpl commentRepository) {
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
    public Post findPostWithCommentsById(Long id) {
        Post post = postRepository.findById(id);
        post.setComments(commentRepository.findAllByPostId(id));
        return post;
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public Long saveComment(Comment comment) {
        return commentRepository.save(comment).getId();
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public void deleteById(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}