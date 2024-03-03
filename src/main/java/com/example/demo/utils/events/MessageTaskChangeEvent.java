package com.example.demo.utils.events;

import com.example.demo.domain.FriendRequest;
import com.example.demo.domain.Message;

public class MessageTaskChangeEvent extends UserTaskChangeEvent<Message> {
    private ChangeEventType type;
    private Message data, oldData;

    public MessageTaskChangeEvent(ChangeEventType type, Message data) {
        super(type, data);
    }
    public MessageTaskChangeEvent(ChangeEventType type, Message data, Message oldData) {
        super(type, data, oldData);
    }
}
