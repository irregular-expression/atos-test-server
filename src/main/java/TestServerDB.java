import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;
import data.MeetingRoom;
import data.Order;
import data.User;
import responses.AuthorizationResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TestServerDB {

    private static TestServerDB database;

    // we are using small embedded H2 database
    private final static String DATABASE_URL = "jdbc:h2:./test"; //here should be a remote jdbc database connection string, "jdbc:h2:mem:test" - for in-memory database
    private JdbcConnectionSource connectionSource;

    private Dao<User, String> userDao;
    private Dao<MeetingRoom, String> meetingRoomDao;
    private Dao<Order, String> orderDao;

    public static synchronized TestServerDB getInstance() throws SQLException {
        if (database == null) {
            database = new TestServerDB();
            database.init();
        }
        return database;
    }

    public void init() throws SQLException {
        // create our data-source for the database
        connectionSource = new JdbcConnectionSource(DATABASE_URL);
        userDao = DaoManager.createDao(connectionSource, User.class);
        meetingRoomDao = DaoManager.createDao(connectionSource, MeetingRoom.class);
        orderDao = DaoManager.createDao(connectionSource, Order.class);

        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, Order.class);
        TableUtils.createTableIfNotExists(connectionSource, MeetingRoom.class);
    }

    public AuthorizationResponse validateLoginAndPassword(String login, String password) throws SQLException {
        AuthorizationResponse res = new AuthorizationResponse();
        if (userDao.idExists(login)) {
            User user = userDao.queryForId(login);
            if (user.getPassword().equals(password)) {
                res.setName(user.getName());
                res.setSuccess(true);
                return res;
            } else {
                res.setError(102);
                return res;
            }
        } else {
            res.setError(101);
            return res;
        }
    }

    public List<MeetingRoom> getAllRooms() throws SQLException {
        return meetingRoomDao.queryForAll();
    }

    public void createOrUpdateUser(User user) throws SQLException {
        userDao.createOrUpdate(user);
    }

    public void createOrUpdateRoom(MeetingRoom meetingRoom) throws SQLException {
        meetingRoomDao.createOrUpdate(meetingRoom);
    }

    public void createOrUpdateOrder(Order order) throws SQLException {
        QueryBuilder<Order, String> qb = orderDao.queryBuilder();
        Where where = qb.where();
        where.eq(Order.ROOM_NAME, order.getRoomName());
        PreparedQuery<Order> preparedQuery = qb.prepare();
        List<Order> orders = orderDao.query(preparedQuery);
        if (!order.isIntersect(orders)) {
            boolean isEarliest = true;
            for (Order o : orders) {
                if (o.getTimeStart() < order.getTimeStart()) {
                    isEarliest = false;
                    break;
                }
            }
            if (isEarliest) {
                MeetingRoom room = meetingRoomDao.queryForId(order.getRoomName());
                room.setEarliestTime(order.getFullDateStartAsString());
                meetingRoomDao.createOrUpdate(room);
            }
            orderDao.createOrUpdate(order);
        }
    }

    public List<Order> getAllOrdersForRoom(String roomName) throws SQLException {
        QueryBuilder<Order, String> qb = orderDao.queryBuilder();
        Where where = qb.where();
        where.eq(Order.ROOM_NAME, roomName);
        PreparedQuery<Order> preparedQuery = qb.prepare();
        return orderDao.query(preparedQuery);
    }

    public Order getOrderById(String id) throws SQLException {
        return orderDao.queryForId(id);
    }

    public MeetingRoom getRoomById(String id) throws SQLException {
        return meetingRoomDao.queryForId(id);
    }

    public User getUserById(String login) throws SQLException {
        return userDao.queryForId(login);
    }

    public void close() throws IOException {
        if (connectionSource != null) {
            connectionSource.close();
        }
    }

}
