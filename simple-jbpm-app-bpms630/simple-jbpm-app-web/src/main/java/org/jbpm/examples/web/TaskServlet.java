package org.jbpm.examples.web;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.jbpm.services.ejb.api.UserTaskServiceEJBLocal;
import org.kie.api.task.model.TaskSummary;

public class TaskServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB
    private UserTaskServiceEJBLocal userTaskService;

    @EJB
    private RuntimeDataServiceEJBLocal runtimeDataService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String cmd = req.getParameter("cmd");
        String user = req.getParameter("user");

        if (cmd.equals("list")) {

            List<TaskSummary> taskList;
            try {
                taskList = runtimeDataService.getTasksAssignedAsPotentialOwner(user, null);
            } catch (Exception e) {
                throw new ServletException(e);
            }
            req.setAttribute("taskList", taskList);
            ServletContext context = this.getServletContext();
            RequestDispatcher dispatcher = context.getRequestDispatcher("/task.jsp");
            dispatcher.forward(req, res);

        } else if (cmd.equals("approve")) {

            String message = "";
            long taskId = Long.parseLong(req.getParameter("taskId"));
            try {
                userTaskService.start(taskId, user);
                userTaskService.complete(taskId, user, null);
                message = "Task (id = " + taskId + ") has been completed by " + user;
            } catch (Exception e) {
                throw new ServletException(e);
            }
            req.setAttribute("message", message);
            ServletContext context = this.getServletContext();
            RequestDispatcher dispatcher = context.getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, res);
        }
    }
}