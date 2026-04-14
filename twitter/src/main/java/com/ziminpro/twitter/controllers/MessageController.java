package com.ziminpro.twitter.controllers;

import com.ziminpro.twitter.dtos.Constants;
import com.ziminpro.twitter.dtos.Message;
import com.ziminpro.twitter.services.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
public class MessageController {

    @Autowired
    private MessagesService messages;

    @GetMapping(Constants.URI_MESSAGE + "/{message-id}")
    public Mono<ResponseEntity<Map<String, Object>>> getMessageById(
            @PathVariable("message-id") UUID messageId) {
        return messages.getMessageById(messageId);
    }

    @GetMapping(Constants.URI_PRODUCER + "/{producer-id}")
    public Mono<ResponseEntity<Map<String, Object>>> getMessagesForProducerById(
            @PathVariable("producer-id") UUID producerId) {
        return messages.getMessagesForProducerById(producerId);
    }

    @GetMapping(Constants.URI_SUBSCRIBER + "/{subscriber-id}")
    public Mono<ResponseEntity<Map<String, Object>>> getMessagesForSubscriberById(
            @PathVariable("subscriber-id") UUID subscriberId) {
        return messages.getMessagesForSubscriberById(subscriberId);
    }

    @PostMapping(value = Constants.URI_MESSAGE, consumes = Constants.APPLICATION_JSON)
    public Mono<ResponseEntity<Map<String, Object>>> createMessage(
            @RequestBody Message message) {
        return messages.createMessage(message);
    }

    @DeleteMapping(Constants.URI_MESSAGE + "/{message-id}")
    public Mono<ResponseEntity<Map<String, Object>>> deleteMessageById(
            @PathVariable("message-id") UUID messageId) {
        return messages.deleteMessageById(messageId);
    }
}