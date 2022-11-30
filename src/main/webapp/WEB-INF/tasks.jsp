<%@ page import="step.learning.entities.Task" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    List<Task> tasks = (List<Task>) request.getAttribute("tasks");
%>
<div class="container">
    <div class="row justify-content-center">
        <h1>Tasks</h1>
        <form action="" method="post">
            <span style="margin: 10px">Name:</span>
            <input style="margin: 10px" type="text" name="taskName" placeholder="Input the name of your task here...">
            <span style="margin: 10px">Description:</span>
            <input style="margin: 10px" width="max-width" type="text" name="taskDescription"
                   placeholder="Input the description of your task here...">
            <input style="margin: 10px" type="submit" value="Add task">
        </form>
        <%
            if (errorMessage != null) { %>
        <strong>Oh snap!</strong> <%= errorMessage %> <%
        }
    %>

        <table>
            <thead>
            <tr>
                <th style="display: none">Task Id</th>
                <th>Task name</th>
                <th>Task description</th>
                <th>Task status</th>
            </tr>
            </thead>
            <tbody>
            <% for (Task task : tasks) { %>
            <tr>
                <td style="display: none"><span name="taskId"><%= task.getId()%></span></td>
                <td><span name="taskName"><%= task.getName() %></span>
                </td>
                <td><span name="taskDescription"><%= task.getDescription() %></span>
                </td>
                <td>
                                        <span name="taskIsDone"
                                              class="task-<%= task.isDone() ? "success" : "fail" %>"><%= task.isDone() ? "Done" : "Not done" %></span>
                    <%--                    <input name="taskIsDone" type="text" <%= task.isDone() ? "success" : "fail" %> value="<%= task.isDone()? "Done": "Not done"%>">--%>
                </td>
                <td>
                    <input id="change-status-button" type="button" value="Change">
                </td>
                <td>
                    <input id="delete-button" type="button" value="Delete">
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</div>
<script>
    // fetch put request to change task status
    document.addEventListener("DOMContentLoaded", () => {
        const changeStatusButtons = document.querySelectorAll("#change-status-button");
        const deleteButtons = document.querySelectorAll("#delete-button");
        const taskNames = document.querySelectorAll("span[name='taskName']");
        const taskDescriptions = document.querySelectorAll("span[name='taskDescription']");
        if (!changeStatusButtons) {
            throw '#change-status-button not found';
        }
        if (!deleteButtons) {
            throw '#delete-button not found';
        }
        for (let element of changeStatusButtons) {
            element.addEventListener("click", changeStatusClick)
        }
        for (let element of deleteButtons) {
            element.addEventListener("click", deleteTaskClick)
        }
        for (let element of taskNames) {
            element.addEventListener("click", editTaskNameClick)
            element.addEventListener("blur", editTaskNameBlur)
            element.addEventListener("keydown", editTaskNameKeyDown)
        }
        for (let element of taskDescriptions) {
            element.addEventListener("click", editTaskDescriptionClick)
            element.addEventListener("blur", editTaskDescriptionBlur)
            element.addEventListener("keydown", editTaskDescriptionKeyDown)
        }
    })

    function changeStatusClick(e) {
        const taskId = e.target.parentElement.parentElement.querySelector("span[name='taskId']").innerText;
        const taskIsDone = e.target.parentElement.parentElement.querySelector("span[name='taskIsDone']").innerText;
        const newStatus = taskIsDone === "Done" ? 1 : 0;
        const url = "/tasks/?taskId=" + taskId + "&taskIsDone=" + newStatus;
        fetch(url, {
            method: 'PUT',
            headers: {},
            body: ""
        }).then(r => r.text())
            .then(t => {
                console.log(t);
                location.reload();
            });
    }

    // fetch delete request to delete task
    function deleteTaskClick(e) {
        const taskId = e.target.parentElement.parentElement.querySelector("span[name='taskId']").innerText;
        const url = "/tasks/?taskId=" + taskId;
        fetch(url, {
            method: 'DELETE',
            headers: {},
            body: ""
        }).then(r => r.text())
            .then(t => {
                console.log(t);
                location.reload();
            });
    }

    function editTaskNameClick(e) {
        e.target.setAttribute("contenteditable", "true");
        e.target.focus();
        e.target.savedText = e.target.innerText;
    }

    function editTaskNameKeyDown(e) {
        if (e.key === "Enter" || e.key === 13) {
            e.preventDefault();
            e.target.blur();
            return false;
        }
    }

    function editTaskNameBlur(e) {
        e.target.removeAttribute("contenteditable");
        const taskId = e.target.parentElement.parentElement.querySelector("span[name='taskId']").innerText;
        const taskDescription = e.target.parentElement.parentElement.querySelector("span[name='taskDescription']").innerText;
        const url = "/tasks/?taskId=" + taskId + "&taskName=" + e.target.innerText + "&taskDescription=" + taskDescription;
        fetch(url, {
            method: 'PUT',
            headers: {},
            body: ""
        }).then(r => r.text())
            .then(t => {
                if (t === "OK") {
                    e.target.innerText = e.target.savedText;
                } else {
                    e.target.savedText = e.target.innerText;
                }
            });
    }

    function editTaskDescriptionClick(e) {
        e.target.setAttribute("contenteditable", "true");
        e.target.focus();
        e.target.savedText = e.target.innerText;
    }

    function editTaskDescriptionKeyDown(e) {
        if (e.key === "Enter" || e.key === 13) {
            e.preventDefault();
            e.target.blur();
            return false;
        }
    }

    function editTaskDescriptionBlur(e) {
        e.target.removeAttribute("contenteditable");
        const taskId = e.target.parentElement.parentElement.querySelector("span[name='taskId']").innerText;
        const taskName = e.target.parentElement.parentElement.querySelector("span[name='taskName']").innerText;
        const url = "/tasks/?taskId=" + taskId + "&taskName=" + taskName + "&taskDescription=" + e.target.innerText;
        fetch(url, {
            method: 'PUT',
            headers: {},
            body: ""
        }).then(r => r.text())
            .then(t => {
                if (t === "OK") {
                    location = location;
                    e.target.innerText = e.target.savedText;
                } else {
                    e.target.savedText = e.target.innerText;
                }
            });
    }
</script>