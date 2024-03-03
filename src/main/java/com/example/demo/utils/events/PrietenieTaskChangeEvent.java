package com.example.demo.utils.events;

import com.example.demo.domain.Prietenie;

public class PrietenieTaskChangeEvent extends UserTaskChangeEvent<Prietenie> {
    private ChangeEventType type;
    private Prietenie data, oldData;

    public PrietenieTaskChangeEvent(ChangeEventType type, Prietenie data) {
        super(type, data);
    }
    public PrietenieTaskChangeEvent(ChangeEventType type, Prietenie data, Prietenie oldData) {
        super(type, data, oldData);
    }
}