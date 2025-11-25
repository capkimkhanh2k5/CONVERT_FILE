package com.convertfile.bo;

import com.convertfile.model.bean.Tasks;
import com.convertfile.model.bean.EnumStatus.TaskStatus;
import com.convertfile.model.dao.TaskDAO;

public class TaskBO {
    private final TaskDAO taskDAO = new TaskDAO();

    public long insertTask(Tasks job) {
        return taskDAO.insertTask(job);
    }

    public Tasks getNextWaitingTask() {
        return taskDAO.getNextWaitingTask();
    }

    public void updateStatus(long taskID, TaskStatus status, int progressPercent, String message) {
        taskDAO.updateStatus(taskID, status, progressPercent, message);
    }

    public void markTaskProcessing(long taskID, String workerId) {
        taskDAO.markTaskProcessing(taskID, workerId);
    }

}
