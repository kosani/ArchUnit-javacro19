package hr.kosani.archunit.persistence;

import hr.kosani.archunit.Repository;
import hr.kosani.archunit.model.Comment;
import hr.kosani.archunit.persistence.mapper.PostMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;

@Repository
public class CommentRepository {

    // TODO Remark: logger API should be preferred to logger implantation.
    // TODO Remark: logger should be final, static and named with upper_case LOG.
    private static Logger log = LogManager.getLogger(CommentRepository.class);

    private static final String SELECT_ALL_BY_POST_ID = "SELECT ID, USER_EMAIL, MESSAGE, POSTED_ON from COMMENT WHERE POST_ID = ?";
    private static final String INSERT = "INSERT INTO COMMENT (POST_ID, USER_EMAIL, MESSAGE, POSTED_ON) VALUES ( ?, ?, ?, ? )";
    private static final String DELETE = "DELETE FROM COMMENT WHERE ID = ?";

    // TODO prefer DI to singletons
    private static CommentRepository instance = new CommentRepository();

    public static CommentRepository getInstance() {
        return instance;
    }

    // TODO read methods should be called by find
    public List<Comment> getAllByPostId(Long postId) throws SQLException {
        log.info("Fetching comments by post id {}", postId);
        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(SELECT_ALL_BY_POST_ID);
        statement.setLong(1, postId);
        ResultSet resultSet = statement.executeQuery();
        List<Comment> comments = PostMapper.toComments(resultSet);

        resultSet.close();
        statement.close();
        connection.close();
        return comments;
    }

    public void deleteById(Long id) throws SQLException {
        log.info("Deleting comment of id {}", id);
        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(DELETE);
        statement.setLong(1, id);
        statement.execute();

        statement.close();
        connection.close();
    }

    public Comment save(Comment comment) throws SQLException {
        log.info("Saving comment {}", comment);
        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT);
        statement.setLong(1, comment.getPostId());
        statement.setString(2, comment.getUsersEMail());
        statement.setString(3, comment.getMessage());
        statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
        statement.execute();
        comment.setId(getId(statement));

        statement.close();
        connection.close();
        return comment;
    }

    private Long getId(PreparedStatement statement) throws SQLException {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            return generatedKeys.getLong(1);
        }
    }
}
