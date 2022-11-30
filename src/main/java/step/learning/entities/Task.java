package step.learning.entities;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Task {
    private String id;

    private String name;

    private String description;

    private boolean isDone;

    public Task() {

    }

    public Task(ResultSet res) throws SQLException {
        id = res.getString("id");
        name = res.getString("name");
        description = res.getString("description");
        isDone = res.getBoolean("is_done");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
