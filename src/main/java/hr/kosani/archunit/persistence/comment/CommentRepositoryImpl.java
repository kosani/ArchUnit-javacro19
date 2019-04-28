package hr.kosani.archunit.persistence.comment;

import hr.kosani.archunit.Repository;
import hr.kosani.archunit.model.Comment;
import hr.kosani.archunit.persistence.DataAccessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    // TODO Remark 5: Logger API should be preferred to logger implementation.
    // TODO Remark 6: Logger should be final, static and named with upper_case LOG.
    private static Logger log = LogManager.getLogger(CommentRepositoryImpl.class);

    private static final String SELECT_ALL_BY_POST_ID = "SELECT ID, USER_EMAIL, MESSAGE, POSTED_ON from COMMENT WHERE POST_ID = ?";
    private static final String INSERT = "INSERT INTO COMMENT (POST_ID, USER_EMAIL, MESSAGE, POSTED_ON) VALUES ( ?, ?, ?, ? )";
    private static final String DELETE = "DELETE FROM COMMENT WHERE ID = ?";

    @Resource(name = "java:comp/jdbc/myJdbc")
    private DataSource dataSource;

    @Override
    public List<Comment> getAllByPostId(Long postId) throws SQLException {
        log.info("Fetching comments by post id {}", postId);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_BY_POST_ID)) {

            statement.setLong(1, postId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return CommentMapper.toComments(resultSet);
            }

        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting comment of id {}", id);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setLong(1, id);
            statement.execute();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

    }

    @Override
    public Comment save(Comment comment) {
        log.info("Saving comment {}", comment);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setLong(1, comment.getPostId());
            statement.setString(2, comment.getUsersEMail());
            statement.setString(3, comment.getMessage());
            statement.setObject(4, LocalDateTime.now());
            statement.execute();
            comment.setId(getId(statement));

            return comment;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private Long getId(PreparedStatement statement) throws SQLException {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            return generatedKeys.getLong(1);
        }
    }
}
