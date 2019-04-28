package hr.kosani.archunit.presentation;

import hr.kosani.archunit.Controller;
import hr.kosani.archunit.domain.BloggingService;
import hr.kosani.archunit.model.Post;
import hr.kosani.archunit.persistence.comment.CommentRepository;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Controller
public class BloggingController {

    private final BloggingService service;
    private final CommentRepository commentRepository;

    @Inject
    public BloggingController(
            BloggingService service,
            CommentRepository commentRepository) {
        this.service = service;
        this.commentRepository = commentRepository;
    }

    @GET
    @Path("/posts/{id}")
    public Post getPost(@PathParam("id") Long id) {
        return service.findPostWithCommentsById(id);
    }

    @GET
    @Path("/posts")
    public Long savePost(Post newPost) {
        return service.savePost(newPost);
    }

    @DELETE
    @Path("/posts/{id}")
    public void deletePost(@PathParam("id") Long id) {
        service.deletePost(id);
    }

    @DELETE
    @Path("/posts/{postId}/comments/{commentId}")
    public void deleteComment(@PathParam("postId") Long postId, @PathParam("commentId") Long commentId) {
        // TODO Remark 1: direct access to repositories, services are skipped.
        commentRepository.deleteById(commentId);
    }
}
