package hr.kosani.archunit.persistence.post;

import hr.kosani.archunit.model.Post;

public interface PostRepository {
    Post findById(Long id);

    Post save(Post post);

    void deleteById(Long id);
}
