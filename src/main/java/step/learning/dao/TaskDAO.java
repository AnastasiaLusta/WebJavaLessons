package step.learning.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.entities.Task;
import step.learning.services.DataService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Singleton
public class TaskDAO {
    private final Connection connection;

    @Inject
    public TaskDAO(DataService dataService) {
        this.connection = dataService.getConnection();
    }

    public String addTask(Task task) {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO Tasks (id, name, description, is_done) VALUES (?, ?, ?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, task.getName());
            statement.setString(3, task.getDescription());
            statement.setBoolean(4, task.isDone());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Error while adding task: " + sql);
            return null;
        }

        return id;
    }

    public boolean deleteTask(String id) {
        String sql = "DELETE FROM Tasks WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Error while deleting task: " + sql);
            return false;
        }

        return true;
    }

    public boolean updateTask(Task task) {
        if (task.getId() == null || task == null) return false;

        Map<String, String> data = new HashMap<>();

        if (task.getName() != null) data.put("name", task.getName());
        if (task.getDescription() != null) data.put("description", task.getDescription());

        StringBuilder sql = new StringBuilder("UPDATE Tasks t SET ");
        boolean needComma = false;
        for (String fieldName : data.keySet()) {
            sql.append(String.format("%c t.`%s` = ?", (needComma ? ',' : ' '), fieldName));
            needComma = true;
        }
        sql.append(" WHERE t.`id` = ?");
        if (!needComma) return false;
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int i = 1;
            for (String value : data.values()) {
                statement.setString(i++, value);
            }
            statement.setString(i, task.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Error while updating task: " + sql);
            return false;
        }
        return true;
    }

    public boolean markTaskAsDone(String id) {
        String sql = "UPDATE Tasks SET is_done = 1 WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Error while marking task as done: " + sql);
            return false;
        }

        return true;
    }

    public boolean markTaskAsNotDone(String id) {
        String sql = "UPDATE Tasks SET is_done = 0 WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Error while marking task as not done: " + sql);
            return false;
        }

        return true;
    }

    public Task getTaskById(String id) {
        String sql = "SELECT * FROM Tasks t WHERE t.`id` = ? ";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, id);
            ResultSet res = prep.executeQuery();
            if (res.next()) {
                return new Task(res);
            }
        } catch (Exception ex) {
            System.out.println("TaskDAO::getTaskById " + ex.getMessage() + "\n" + sql + " -- " + id);
        }
        return null;
    }

    public List<Task> getAllTasks() {
        String sql = "SELECT * FROM Tasks";
        List<Task> tasks = new ArrayList<>();
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            ResultSet res = prep.executeQuery();
            while (res.next()) {
                tasks.add(new Task(res));
            }
        } catch (Exception ex) {
            System.out.println("TaskDAO::getAllTasks " + ex.getMessage() + "\n" + sql);
        }
        return tasks;
    }
}
