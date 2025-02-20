package com.thstudio.project.domainModel.State;

import java.time.LocalDateTime;

public class Booked extends State {
    private final LocalDateTime bookedTime;
    public Booked(LocalDateTime bookedTime){
        this.state = "Booked";
        this.bookedTime = bookedTime;
    }

    @Override
    public String getExtraInfo(){
        return this.bookedTime.toString();
    }

}
