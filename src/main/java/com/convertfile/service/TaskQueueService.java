package com.convertfile.service;

import com.convertfile.model.bean.Tasks;
import com.convertfile.model.bean.EnumStatus.TaskStatus;
import com.convertfile.model.bean.EnumStatus.TaskType;
import com.convertfile.model.dao.TaskQueueDAO;

public class TaskQueueService {
    private final TaskQueueDAO taskDAO = new TaskQueueDAO();

    public void addNewTask(String fileID, String jobType){
        Tasks job = new Tasks();
        job.setFileId(fileID);
        job.setTask_type(TaskType.valueOf(jobType));
        job.setStatus(TaskStatus.WAITING);
        job.setMessage("");
        job.setWorker_id("");
        job.setAttempt_count(0);

        taskDAO.insertTask(job);
    }
}
