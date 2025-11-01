package com.convertfile.worker;

import com.convertfile.dao.FileDAO;
import com.convertfile.dao.TaskQueueDAO;
import com.convertfile.model.TaskJob;

public class fileWorker implements Runnable{
    private final TaskQueueDAO taskDAO = new TaskQueueDAO();
    private final FileDAO fileDAO = new FileDAO();

    @Override
    public void run() {
        while(true){
            try{
                TaskJob job = taskDAO.getNextWaitingTask();
                if(job != null){
                    System.out.println("Processing in file: " + job.getFileId());

                    taskDAO.updateStatus(job.getId(), "PROCESSING", "Processing in file " + job.getFileId());

                    //TODO: Set quá trình chuyển cho phù hợp

                    fileDAO.updateStatus(job.getFileId(), "DONE");
                    taskDAO.updateStatus(job.getId(), "DONE", "Done in file " + job.getFileId());
                } else{
                    //Cứ 10s check 1 lần
                    Thread.sleep(10000);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
