package hr.kosani.archunit.presentation;

import hr.kosani.archunit.Controller;
import hr.kosani.archunit.model.Post;
import hr.kosani.archunit.persistence.comment.CommentRepository;
import hr.kosani.archunit.domain.BloggingService;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.sql.SQLException;

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
        try {
            return service.findPostWithCommentsById(id);
        } catch (SQLException e) { // TODO Remark 2: persistence-specific exceptions shouldn't leak to presentation layer.
            // TODO Remark 7: App shouldn't write to standard streams.
            e.printStackTrace();
            return null;
        }
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
        try {
            // TODO Remark 1: direct access to repositories, services are skipped.
            commentRepository.deleteById(commentId);
        } catch (SQLException e) { // TODO Remark 2: persistence-specific exceptions shouldn't leak to presentation layer.
            // TODO Remark 7: App shouldn't write to standard streams.
            e.printStackTrace();
        }
    }
}
