package hr.kosani.archunit.persistence.post;

import hr.kosani.archunit.Repository;
import hr.kosani.archunit.model.Post;
import hr.kosani.archunit.persistence.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private static final Logger LOG = LoggerFactory.getLogger(PostRepositoryImpl.class);

    private static final String SELECT = "SELECT ID, CREATED_AT, LAST_MODIFIED_AT, TITLE, TEXT from POST WHERE ID = ?";
    private static final String UPDATE = "UPDATE POST SET TITLE = ?, TEXT = ?, LAST_MODIFIED_AT = ?";
    private static final String INSERT = "INSERT INTO POST (TITLE, TEXT, CREATED_AT, LAST_MODIFIED_AT) VALUES ( ?, ?, ?, ? )";
    private static final String DELETE = "DELETE FROM POST WHERE ID = ?";


    @Resource(name = "java:comp/jdbc/myJdbc")
    private DataSource dataSource;

    @Override
    public Post findById(Long id) {
        LOG.info("Fetching post of id {}", id);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return PostMapper.toPost(resultSet);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public Post save(Post post) {
        LOG.info("Saving new post {}", post);
        boolean isNew = post.getId() == null;
        String sql = isNew ? INSERT : UPDATE;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, post.getTitle());
            statement.setString(2, post.getText());
            statement.setObject(3, LocalDateTime.now());
            if (isNew) statement.setObject(4, LocalDateTime.now());
            statement.execute();
            if (isNew) post.setId(getId(statement));
            return post;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private Long getId(PreparedStatement statement) throws SQLException {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            return generatedKeys.getLong(1);
        }
    }

    @Override
    public void deleteById(Long id) {
        LOG.info("Deleting post of id {}", id);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {

            statement.setLong(1, id);
            statement.execute();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
