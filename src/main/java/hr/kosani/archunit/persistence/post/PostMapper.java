package hr.kosani.archunit.persistence.post;

import hr.kosani.archunit.model.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class PostMapper {

    static List<Post> toPosts(ResultSet resultSet) throws SQLException {
        List<Post> posts = new ArrayList<>();
        while (resultSet.next()) {
            posts.add(toPost(resultSet));
        }
        return posts;
    }

    static Post toPost(ResultSet resultSet) throws SQLException {
        Post post = new Post();
        post.setId(resultSet.getLong("id"));
        post.setCreatedAt(resultSet.getObject("createdAt", LocalDateTime.class));
        post.setLastModifiedAt(resultSet.getObject("last_modified_at", LocalDateTime.class));
        post.setTitle(resultSet.getString("title"));
        post.setText(resultSet.getString("text"));
        return post;
    }
}

