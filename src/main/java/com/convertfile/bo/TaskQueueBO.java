package com.convertfile.bo;

import com.convertfile.dao.TaskQueueDAO;
import com.convertfile.model.TaskJob;
import com.convertfile.model.EnumStatus.TaskStatus;

public class TaskQueueBO {
    private final TaskQueueDAO taskQueueDAO = new TaskQueueDAO();

    public boolean insertTask(TaskJob job) {
        return taskQueueDAO.insertTask(job);
    }

    public TaskJob getNextWaitingTask() {
        return taskQueueDAO.getNextWaitingTask();
    }
    
    public void updateStatus(long taskID, TaskStatus status, String message) {
        taskQueueDAO.updateStatus(taskID, status, message);
    }

    public void markTaskProcessing(long taskID, String workerId) {
        taskQueueDAO.markTaskProcessing(taskID, workerId);
    }

}
