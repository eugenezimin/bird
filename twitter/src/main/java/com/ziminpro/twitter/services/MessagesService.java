package com.ziminpro.twitter.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

@Service
public class MessagesService {
    
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UMSConnector umsConnector;

    @Value("${ums.paths.user}")
    private String uriUser;

    Map<String, Object> response = new HashMap<>();

    public Mono<ResponseEntity<Map<String, Object>>> createMessage(Message message) {
        return umsConnector.retrieveUmsData(uriUser + "/" + message.getAuthor().toString())
            .flatMap(res -> {
            UUID messageId = null;
            User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);

            if (user.hasRole(Roles.PRODUCER)) {
                messageId = messageRepository.createMessage(message);
            }
            if (messageId == null) {
                response.put(Constants.CODE, "400");
                response.put(Constants.MESSAGE, "Message has not been created");
                response.put(Constants.DATA, "Something went wrong");
            } else {
                response.put(Constants.CODE, "201");
                response.put(Constants.MESSAGE, "Message has been created");
                response.put(Constants.DATA, messageId.toString());
            }
            return Mono.just(ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, Constants.APPLICATION_JSON)
                    .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
        });
    }

    public Mono<ResponseEntity<Map<String, Object>>> getMessagebyId(UUID messageId) {
        Message message = messageRepository.getMessagebyId(messageId);
        if (message.getId() == null) {
            response.put(Constants.CODE, "404");
            response.put(Constants.MESSAGE, "Message not found");
            response.put(Constants.DATA, message);
        } else {
            response.put(Constants.CODE, "200");
            response.put(Constants.MESSAGE, "Message has been found");
            response.put(Constants.DATA, message);
        }
        return Mono.just(ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));

    }

    public Mono<ResponseEntity<Map<String, Object>>> getMessagesForProducerById(UUID producerId) {
        List<Message> messages = messageRepository.getMessagesForProducerById(producerId);
        if (messages.size() == 0) {
            response.put(Constants.CODE, "404");
            response.put(Constants.MESSAGE, "Either producer didn't produce any messages or producer not found");
            response.put(Constants.DATA, new ArrayList<>());
        } else {
            response.put(Constants.CODE, "200");
            response.put(Constants.MESSAGE, "List of messages has been requested successfully");
            response.put(Constants.DATA, messages);
        }
        return Mono.just(ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));

    }

    public Mono<ResponseEntity<Map<String, Object>>> getMessagesForSubscriberById(UUID subscriberId) {
        return umsConnector.retrieveUmsData(uriUser + "/" + subscriberId.toString()).flatMap(res -> {
            User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);
            List<Message> messages = new ArrayList<>();

            if (user.hasRole(Roles.SUBSCRIBER)) {
                messages = messageRepository.getMessagesForSubscriberById(subscriberId);
            }
            if (messages.size() == 0) {
                response.put(Constants.CODE, "404");
                response.put(Constants.MESSAGE, "Subscription not found or empty");
                response.put(Constants.DATA, new ArrayList<>());
            } else {
                response.put(Constants.CODE, "200");
                response.put(Constants.MESSAGE, "List of messages has been requested successfully");
                response.put(Constants.DATA, messages);
            }
            return Mono.just(ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, Constants.APPLICATION_JSON)
                    .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
        });
    }

    public Mono<ResponseEntity<Map<String, Object>>> deleteMessageById(UUID messageId) {
        int result = messageRepository.deleteMessageById(messageId);
        if (result != 1) {
            response.put(Constants.CODE, "500");
            response.put(Constants.MESSAGE, "Message " + messageId.toString() + " has not been deleted");
            response.put(Constants.DATA, false);
        } else {
            response.put(Constants.CODE, "200");
            response.put(Constants.MESSAGE, "Message " + messageId.toString() + " successfully deleted");
            response.put(Constants.DATA, true);
        }
        return Mono.just(ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }
}
