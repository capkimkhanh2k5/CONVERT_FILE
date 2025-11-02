package com.convertfile.service;

import com.convertfile.dao.TaskQueueDAO;
import com.convertfile.model.TaskJob;
import com.convertfile.model.EnumStatus.TaskStatus;
import com.convertfile.model.EnumStatus.TaskType;

public class TaskQueueService {
    private final TaskQueueDAO taskDAO = new TaskQueueDAO();

    public void addNewTask(String fileID, String jobType){
        TaskJob job = new TaskJob();
        job.setFileId(fileID);
        job.setTask_type(TaskType.valueOf(jobType));
        job.setStatus(TaskStatus.WAITING);
        job.setMessage("");
        job.setWorker_id("");
        job.setAttempt_count(0);

        taskDAO.insertTask(job);
    }
}
