package com.ziminpro.twitter.services;

import com.ziminpro.twitter.dao.MessageRepository;
import com.ziminpro.twitter.dtos.Constants;
import com.ziminpro.twitter.dtos.HttpResponseExtractor;
import com.ziminpro.twitter.dtos.Message;
import com.ziminpro.twitter.dtos.Roles;
import com.ziminpro.twitter.dtos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MessagesService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UMSConnector umsConnector;

    @Value("${ums.paths.user}")
    private String uriUser;

    // ----------------------------------------------------------------
    // Public API
    // ----------------------------------------------------------------

    public Mono<ResponseEntity<Map<String, Object>>> createMessage(Message message) {
        return umsConnector.retrieveUmsData(uriUser + "/" + message.getAuthor())
            .flatMap(res -> {
                Map<String, Object> response = new HashMap<>();
                User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);

                UUID messageId = null;
                if (user.hasRole(Roles.PRODUCER)) {
                    messageId = messageRepository.createMessage(message);
                }

                if (messageId == null) {
                    response.put(Constants.CODE,    "400");
                    response.put(Constants.MESSAGE, "Message has not been created — author must have PRODUCER role");
                    response.put(Constants.DATA,    null);
                } else {
                    response.put(Constants.CODE,    "201");
                    response.put(Constants.MESSAGE, "Message has been created");
                    response.put(Constants.DATA,    messageId.toString());
                }
                return Mono.just(jsonOk(response));
            });
    }

    public Mono<ResponseEntity<Map<String, Object>>> getMessageById(UUID messageId) {
        Map<String, Object> response = new HashMap<>();
        Message message = messageRepository.getMessageById(messageId);

        if (message.getId() == null) {
            response.put(Constants.CODE,    "404");
            response.put(Constants.MESSAGE, "Message not found");
            response.put(Constants.DATA,    null);
        } else {
            response.put(Constants.CODE,    "200");
            response.put(Constants.MESSAGE, "Message has been found");
            response.put(Constants.DATA,    message);
        }
        return Mono.just(jsonOk(response));
    }

    public Mono<ResponseEntity<Map<String, Object>>> getMessagesForProducerById(UUID producerId) {
        Map<String, Object> response = new HashMap<>();
        List<Message> messages = messageRepository.getMessagesForProducerById(producerId);

        if (messages.isEmpty()) {
            response.put(Constants.CODE,    "404");
            response.put(Constants.MESSAGE, "No messages found for this producer");
            response.put(Constants.DATA,    new ArrayList<>());
        } else {
            response.put(Constants.CODE,    "200");
            response.put(Constants.MESSAGE, "Messages retrieved successfully");
            response.put(Constants.DATA,    messages);
        }
        return Mono.just(jsonOk(response));
    }

    public Mono<ResponseEntity<Map<String, Object>>> getMessagesForSubscriberById(UUID subscriberId) {
        return umsConnector.retrieveUmsData(uriUser + "/" + subscriberId)
            .flatMap(res -> {
                Map<String, Object> response = new HashMap<>();
                User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);

                List<Message> messages = new ArrayList<>();
                if (user.hasRole(Roles.SUBSCRIBER)) {
                    messages = messageRepository.getMessagesForSubscriberById(subscriberId);
                }

                if (messages.isEmpty()) {
                    response.put(Constants.CODE,    "404");
                    response.put(Constants.MESSAGE, "No messages found — subscription may be empty or user is not a SUBSCRIBER");
                    response.put(Constants.DATA,    new ArrayList<>());
                } else {
                    response.put(Constants.CODE,    "200");
                    response.put(Constants.MESSAGE, "Messages retrieved successfully");
                    response.put(Constants.DATA,    messages);
                }
                return Mono.just(jsonOk(response));
            });
    }

    public Mono<ResponseEntity<Map<String, Object>>> deleteMessageById(UUID messageId) {
        Map<String, Object> response = new HashMap<>();
        int rows = messageRepository.deleteMessageById(messageId);

        if (rows != 1) {
            response.put(Constants.CODE,    "404");
            response.put(Constants.MESSAGE, "Message " + messageId + " not found or already deleted");
            response.put(Constants.DATA,    false);
        } else {
            response.put(Constants.CODE,    "200");
            response.put(Constants.MESSAGE, "Message " + messageId + " deleted successfully");
            response.put(Constants.DATA,    true);
        }
        return Mono.just(jsonOk(response));
    }

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

    private static ResponseEntity<Map<String, Object>> jsonOk(Map<String, Object> body) {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, Constants.APPLICATION_JSON)
            .header(Constants.ACCEPT,         Constants.APPLICATION_JSON)
            .body(body);
    }
}