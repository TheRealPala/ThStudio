package com.thstudio.project.domainModel.State;

import java.util.Objects;

public abstract class State {
    protected String state;

    public String getState(){
        return this.state;
    }

    public abstract String getExtraInfo();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State that = (State) o;
        return Objects.equals(getState(), that.getState()) && Objects.equals(getExtraInfo(), that.getExtraInfo());
    }
}
