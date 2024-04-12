package org.cst8277.twitter.dao;

import java.util.List;
import java.util.UUID;

import org.cst8277.twitter.dtos.Message;

public interface MessageRepository {
    public Message getMessagebyId(UUID messageId);
    public List<Message> getMessagesForProducerById(UUID producerId);
    public List<Message> getMessagesForSubscriberById(UUID subscriberId);
    public UUID createMessage(Message message);
    public int deleteMessageById(UUID messageId);
}
