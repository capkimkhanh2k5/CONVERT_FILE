package com.convertfile.service;

import com.convertfile.dao.TaskQueueDAO;
import com.convertfile.model.TaskJob;

public class TaskQueueService {
    private final TaskQueueDAO taskDAO = new TaskQueueDAO();

    //TODO: Fix cho phu hop
    public void addNewTask(String fileID, String jobType){
        TaskJob job = new TaskJob();
        job.setFileId(fileID);
        job.setJobType(jobType);
        job.setStatus("WAITING");

        taskDAO.insertTask(job);
    }
}
