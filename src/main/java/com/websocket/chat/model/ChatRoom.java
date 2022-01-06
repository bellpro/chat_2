package com.websocket.chat.model;

import com.websocket.chat.service.ChatService;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

@Getter
public class ChatRoom {
    private String roomId;      // 방번호
    private String name;        // 방제목
    private Set<WebSocketSession> sessions = new HashSet<>();       // 채팅방은 입장한 클라이언트 정보 리스트

    @Builder
    public ChatRoom(String roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }

    // 채팅방에는 입장, 대화 기능이 있으므로 분기 처리
    // 입장 시에 채팅룸의 세션 정보에 클라이언트의 세선 리스트를 추가
    // 채팅룸에 메시지가 도착할 경우 채팀룸의 모든 세션에 메시지를 발송
    public void handleActions(WebSocketSession session, ChatMessage chatMessage, ChatService chatService) {
        // 메시지 타입이 입장이면 리스트 추가
        if (chatMessage.getType().equals(ChatMessage.MessageType.ENTER)) {
            sessions.add(session);
            chatMessage.setMessage(chatMessage.getSender() + "님이 입장했습니다.");
        }
        sendMessage(chatMessage, chatService);
    }

    public <T> void sendMessage(T message, ChatService chatService) {
        sessions.parallelStream().forEach(session -> chatService.sendMessage(session, message));
    }
}
