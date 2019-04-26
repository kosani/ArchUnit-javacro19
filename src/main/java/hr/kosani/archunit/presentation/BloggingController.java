package hr.kosani.archunit.presentation;

import hr.kosani.archunit.Controller;
import hr.kosani.archunit.model.Post;
import hr.kosani.archunit.persistence.CommentRepository;
import hr.kosani.archunit.domain.BloggingService;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.sql.SQLException;

@Controller
public class BloggingController {

    private final BloggingService service;

    @Inject
    public BloggingController(BloggingService service) {
        this.service = service;
    }

    @GET
    @Path("/posts/{id}")
    public Post getPost(@PathParam("id") Long id) {
        try {
            return service.findPostWithCommentsById(id);
            // TODO handling SQL exceptions in Controller
        } catch (SQLException e) {
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
            // TODO direct access to repositories, services are skipped.
            CommentRepository.getInstance().deleteById(commentId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
