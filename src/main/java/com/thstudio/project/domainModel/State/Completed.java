package com.thstudio.project.domainModel.State;

import java.time.LocalDateTime;

public class Completed extends State {
    private final LocalDateTime completedTime;

    public Completed(LocalDateTime ldt){
        this.state = "Completed";
        this.completedTime = ldt;
    }
    @Override
    public String getExtraInfo(){
        return this.completedTime.toString();
    }
}
