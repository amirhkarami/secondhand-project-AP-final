package ir.ac.aut.secondhand.frontend.controller;

import ir.ac.aut.secondhand.frontend.dto.ChatMessageDto;
import ir.ac.aut.secondhand.frontend.dto.ConversationDto;
import ir.ac.aut.secondhand.frontend.service.ConversationService;
import ir.ac.aut.secondhand.frontend.session.UserSession;
import ir.ac.aut.secondhand.frontend.util.Alerts;
import ir.ac.aut.secondhand.frontend.util.UiExecutor;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public final class ConversationsController {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ConversationService conversationService;
    private final UiExecutor uiExecutor;
    private Long conversationToSelect;

    @FXML
    private ListView<ConversationDto> conversationsList;
    @FXML
    private ListView<ChatMessageDto> messagesList;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    @FXML
    private Label conversationTitle;
    @FXML
    private Label stateLabel;

    public ConversationsController(ConversationService conversationService, UiExecutor uiExecutor) {
        this.conversationService = conversationService;
        this.uiExecutor = uiExecutor;
    }

    @FXML
    private void initialize() {
        configureCells();
        conversationsList.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> loadMessages(newValue));
        loadConversations();
    }

    public void setConversationToSelect(Long conversationId) {
        this.conversationToSelect = conversationId;
        selectRequestedConversation();
    }

    @FXML
    private void onRefresh() {
        loadConversations();
    }

    @FXML
    private void onSend() {
        ConversationDto conversation = conversationsList.getSelectionModel().getSelectedItem();
        if (conversation == null) {
            Alerts.error("Conversation", "Select a conversation first");
            return;
        }
        //Long conversationId = conversation.getProductId();
        Long conversationId = conversation.getId();
        System.out.println(conversationId);

        if (conversationId == null) {
            Alerts.error("Conversation", "Conversation id is missing");
            return;
        }
        String content = messageField.getText();
        if (content == null || content.isBlank()) {
            Alerts.error("Message", "Message cannot be empty");
            return;
        }
        sendButton.setDisable(true);
        uiExecutor.run(() -> conversationService.sendReply(conversationId, content.trim()),
                receipt -> {
                    sendButton.setDisable(false);
                    messageField.clear();
                    loadMessages(conversation);
                },
                error -> {
                    sendButton.setDisable(false);
                    Alerts.error("Send message", error);
                });
    }

    private void loadConversations() {
        stateLabel.setText("Loading conversations...");
        uiExecutor.run(conversationService::getConversations,
                conversations -> {
                    conversationsList.getItems().setAll(conversations == null ? List.of() : conversations);
                    stateLabel.setText(conversations == null || conversations.isEmpty()
                            ? "No conversations yet"
                            : conversations.size() + " conversation(s)");
                    selectRequestedConversation();
                    if (conversationToSelect == null && !conversationsList.getItems().isEmpty()
                            && conversationsList.getSelectionModel().isEmpty()) {
                        conversationsList.getSelectionModel().selectFirst();
                    }
                },
                error -> {
                    stateLabel.setText("Unable to load conversations");
                    Alerts.error("Conversations", error);
                });
    }

    private void loadMessages(ConversationDto conversation) {
        messagesList.getItems().clear();
        if (conversation == null || conversation.getId() == null) {
            conversationTitle.setText("Select a conversation");
            return;
        }
        conversationTitle.setText(conversation.getProductTitle() == null
                ? "Conversation #" + conversation.getId()
                : conversation.getProductTitle());
        uiExecutor.run(() -> conversationService.getMessages(conversation.getId()),
                messages -> messagesList.getItems().setAll(messages == null ? List.of() : messages),
                error -> Alerts.error("Messages", error));
    }

    private void selectRequestedConversation() {
        if (conversationToSelect == null || conversationsList == null) {
            return;
        }
        conversationsList.getItems().stream()
                .filter(item -> conversationToSelect.equals(item.getId()))
                .findFirst()
                .ifPresent(item -> conversationsList.getSelectionModel().select(item));
    }

    private void configureCells() {
        conversationsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(ConversationDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                String title = item.getProductTitle() == null ? "Conversation #" + item.getId() : item.getProductTitle();
                String currentUsername = UserSession.getInstance().username().orElse(null);
                String otherUsername = item.resolveOtherUsername(currentUsername);
                String user = otherUsername == null ? "" : "\n" + otherUsername;
                String last = item.getLastMessage() == null ? "" : "\n" + item.getLastMessage();
                setText(title + user + last);
            }
        });

        messagesList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(ChatMessageDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                String time = item.getSentAt() == null ? "" : " • " + TIME_FORMAT.format(item.getSentAt());
                setText(item.getSenderUsername() + time + "\n" + item.getContent());
                boolean own = UserSession.getInstance().username()
                        .map(username -> username.equals(item.getSenderUsername()))
                        .orElse(false);
                setStyle(own ? "-fx-alignment: CENTER-RIGHT;" : "-fx-alignment: CENTER-LEFT;");
            }
        });
    }
}
