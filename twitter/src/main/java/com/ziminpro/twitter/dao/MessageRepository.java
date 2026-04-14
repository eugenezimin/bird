package com.ziminpro.twitter.dao;

import com.ziminpro.twitter.dtos.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message       getMessageById(UUID messageId);
    List<Message> getMessagesForProducerById(UUID producerId);
    List<Message> getMessagesForSubscriberById(UUID subscriberId);
    UUID          createMessage(Message message);
    int           deleteMessageById(UUID messageId);
}