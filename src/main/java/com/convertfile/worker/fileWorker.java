package com.convertfile.worker;

import com.convertfile.dao.FileDAO;
import com.convertfile.dao.TaskQueueDAO;
import com.convertfile.model.EnumStatus.TaskStatus;
import com.convertfile.service.FileService;
import com.convertfile.model.TaskJob;

public class fileWorker implements Runnable{
    private final TaskQueueDAO taskDAO = new TaskQueueDAO();
    private final FileDAO fileDAO = new FileDAO();
    private final int MAX_ATTEMPTS = 3; //Số lần thử chuyển đổi lại tối đa
    private final FileService fileService = new FileService();
    private final String worker_id;


    public fileWorker(int worker_id){
        this.worker_id = "Worker-" + worker_id;
    }   

    @Override
    public void run() {
        System.out.println(this.worker_id + " is running...");

        while(true){
            try{
                TaskJob job = taskDAO.getNextWaitingTask();
                if(job != null){
                    System.out.println("Processing in file: " + job.getFileId());

                    taskDAO.markTaskProcessing(job.getTask_id(), worker_id);

                    //Bắt đầu xử lí
                    //TODO: Điền manager sau khi code fileConvertManager()
                    boolean success = true;

                    if (success) {
                        fileDAO.updateStatus(job.getFileId(), "CONVERTED");
                        taskDAO.updateStatus(job.getTask_id(), TaskStatus.COMPLETED,
                                "Successfully converted file: " + job.getFileId());
                        System.out.println(this.worker_id + " completed job: " + job.getFileId());
                    } else {
                        taskDAO.updateStatus(job.getTask_id(), TaskStatus.FAILED,
                                "Failed to convert file: " + job.getFileId());
                        System.err.println(this.worker_id + " failed job: " + job.getFileId());
                    }

                } else{
                    //Cứ 10s check 1 lần
                    Thread.sleep(10000);
                }
            } catch(Exception e){
                e.printStackTrace();
                try {
                    Thread.sleep(30000); // nghỉ ngắn khi gặp lỗi
                } catch (InterruptedException ignored) {}
            }
        }
    }
}
