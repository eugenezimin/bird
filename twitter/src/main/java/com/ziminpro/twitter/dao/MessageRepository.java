package com.ziminpro.twitter.dao;

import java.util.List;
import java.util.UUID;

import com.ziminpro.twitter.dtos.Message;

public interface MessageRepository {
    public Message getMessagebyId(UUID messageId);
    public List<Message> getMessagesForProducerById(UUID producerId);
    public List<Message> getMessagesForSubscriberById(UUID subscriberId);
    public UUID createMessage(Message message);
    public int deleteMessageById(UUID messageId);
}
