package org.cst8277.twitter.dtos;

public class Constants {
        private Constants() {
                throw new IllegalStateException("Constants only class");
        }

        // HTTP Section
        public static final String CODE = "code";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String URI_MESSAGE = "/messages/message";
        public static final String URI_MESSAGES = "/messages";
        public static final String URI_PRODUCER = "/messages/producer";
        public static final String URI_SUBSCRIBER = "/messages/subscriber";
        public static final String URI_SUBSCRIPTION = "/subscriptions/subscriber";
        public static final String URI_SUBSCRIPTIONS = "/subscriptions";

        // HEADERS Section
        public static final String APPLICATION_JSON = "application/json";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String ACCEPT = "Accept";

        // Database Section
        public static final String DB = "`twitter`";
        public static final String TABLE_MESSAGES = "`messages`";
        public static final String TABLE_PRODUCERS = "`producers`";
        public static final String TABLE_SUBSCRIBERS = "`subscribers`";
        public static final String TABLE_SUBSCRIPTIONS = "`subscriptions`";

        public static final String GET_MESSAGE_BY_ID = "SELECT * FROM " + TABLE_MESSAGES + " WHERE id = UUID_TO_BIN(?)";
        public static final String GET_MESSAGES_FOR_PRODUCER = "SELECT * FROM " + TABLE_MESSAGES
                        + " WHERE producer_id = UUID_TO_BIN(?)";
        public static final String GET_MESSAGES_FOR_SUBSCRIBER = "SELECT messages.id as id, messages.producer_id as producer_id, "
                        + "messages.content as content, messages.created as created "
                        + "FROM messages LEFT JOIN producers ON messages.producer_id "
                        + "= producers.producer_id LEFT JOIN subscriptions ON producers.producer_id = subscriptions.producer_id "
                        + "LEFT JOIN subscribers ON subscriptions.subscriber_id = subscribers.subscriber_id "
                        + "WHERE subscribers.subscriber_id = UUID_TO_BIN(?);";
        public static final String CREATE_PRODUCER = "INSERT IGNORE INTO " + TABLE_PRODUCERS
                        + " (`producer_id`) VALUES(UUID_TO_BIN(?));";
        public static final String CREATE_SUBSCRIBER = "INSERT IGNORE INTO " + TABLE_SUBSCRIBERS
                        + " (`subscriber_id`) VALUES(UUID_TO_BIN(?));";
        public static final String CREATE_MESSAGE = "INSERT INTO " + TABLE_MESSAGES
                        + " (`id`, `producer_id`, `content`, `created`) "
                        + "VALUES(UUID_TO_BIN(?), UUID_TO_BIN(?), ?, ?);";
        public static final String CREATE_SUBSCRIPTION = "INSERT IGNORE INTO " + TABLE_SUBSCRIPTIONS
                        + " (`subscriber_id`,`producer_id`) VALUES(UUID_TO_BIN(?),UUID_TO_BIN(?));";
        public static final String DELETE_MESSAGE = "DELETE FROM " + TABLE_MESSAGES + " WHERE `id`=UUID_TO_BIN(?);";
        public static final String DELETE_SUBSCRIPTION = "DELETE FROM " + TABLE_SUBSCRIPTIONS
                        + " WHERE `subscriber_id`=UUID_TO_BIN(?);";
        public static final String GET_SUBSCRIPTION = "SELECT * FROM " + TABLE_SUBSCRIPTIONS
                        + " WHERE subscriber_id=UUID_TO_BIN(?);";
}
