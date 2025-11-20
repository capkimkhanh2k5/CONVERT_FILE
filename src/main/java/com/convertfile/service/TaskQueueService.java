package com.convertfile.service;

import com.convertfile.model.bean.Tasks;
import com.convertfile.model.bean.EnumStatus.TaskStatus;
import com.convertfile.model.bean.EnumStatus.TaskType;
import com.convertfile.model.dao.TaskDAO;

public class TaskQueueService {
    private final TaskDAO taskDAO = new TaskDAO();

    public void addNewTask(String fileID, String jobType) {
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
