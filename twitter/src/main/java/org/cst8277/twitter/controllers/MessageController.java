package org.cst8277.twitter.controllers;

import java.util.Map;
import java.util.UUID;

import org.cst8277.twitter.dtos.Constants;
import org.cst8277.twitter.dtos.Message;
import org.cst8277.twitter.services.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class MessageController {

    @Autowired
    private MessagesService messages;

    @RequestMapping(method = RequestMethod.GET, path = Constants.URI_MESSAGE + "/{message-id}")
    public Mono<ResponseEntity<Map<String, Object>>> getMessagebyId(
            @PathVariable(value = "message-id", required = true) String messageId) {
        return messages.getMessagebyId(UUID.fromString(messageId));
    }

    @RequestMapping(method = RequestMethod.GET, path = Constants.URI_PRODUCER + "/{producer-id}")
    public Mono<ResponseEntity<Map<String, Object>>> getMessagesForProducerById(
            @PathVariable(value = "producer-id", required = true) String producerId) {
        return messages.getMessagesForProducerById(UUID.fromString(producerId));
    }

    @RequestMapping(method = RequestMethod.GET, path = Constants.URI_SUBSCRIBER + "/{subscriber-id}")
    public Mono<ResponseEntity<Map<String, Object>>> getMessagesForSubscriberById(
            @PathVariable(value = "subscriber-id", required = true) String subscriberId) {
        return messages.getMessagesForSubscriberById(UUID.fromString(subscriberId));
    }

    @RequestMapping(method = RequestMethod.POST, path = Constants.URI_MESSAGE, consumes = Constants.APPLICATION_JSON)
    public Mono<ResponseEntity<Map<String, Object>>> createMessage(@RequestBody Message message) {
        return messages.createMessage(message);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = Constants.URI_MESSAGE + "/{message-id}")
    public Mono<ResponseEntity<Map<String, Object>>> deleteMessageById(
            @PathVariable(value = "message-id", required = true) String messageId) {
        return messages.deleteMessageById(UUID.fromString(messageId));
    }

}