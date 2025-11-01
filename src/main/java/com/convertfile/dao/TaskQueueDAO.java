package com.convertfile.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.convertfile.model.TaskJob;

public class TaskQueueDAO {
    public boolean insertTask(TaskJob job) {
        String sql = "insert..."; // File ID, job_type, status
        //TODO: Fix phu hop voi DB  

        try (Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, job.getFileId());
            ps.setString(2, job.getJobType());
            ps.setString(3, job.getStatus());

            return ps.executeUpdate() > 0;
        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public TaskJob getNextWaitingTask(){
        String sql = "select ..."; //status waiting đầu tiên
        //TODO: Fix phu hop voi DB 

        try(Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
                
            if(rs.next()){
                TaskJob job = new TaskJob();

                job.setId(rs.getInt("id"));
                job.setFileId(rs.getString("file_id"));
                job.setJobType(rs.getString("job_type"));
                job.setStatus(rs.getString("status"));

                return job;
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public void updateStatus(int jobID, String status, String message){
        String sql = "update...";
        //TODO: Fix phu hop voi DB

        try(Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, status);
            ps.setString(2, message);
            ps.setInt(3, jobID);

            ps.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
