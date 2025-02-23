package com.thstudio.project.domainModel.State;

import java.time.LocalDateTime;

public class Deleted extends State {

    private final LocalDateTime cancelledTime;

    public Deleted(LocalDateTime ldt) {
        this.state = "Cancelled";
        this.cancelledTime = ldt;
    }

    @Override
    public String getExtraInfo() {
        return this.cancelledTime.toString();
    }
}
