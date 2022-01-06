package com.websocket.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.websocket.chat.model.ChatMessage;
import com.websocket.chat.model.ChatRoom;
import com.websocket.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j                        // 로깅
@RequiredArgsConstructor      // 의존성 주입
@Component                    // 빈 등록
public class WebSockChatHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 웹소켓 클라이언트로부터 채팅 메시지를 전달받음
        String payload = message.getPayload();
        log.info("payload {}", payload);

        // 채팅 메시지 객체로 반환
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);

        // 전달받은 메시지에 담긴 채팅방ID 로 채팅방 정보 조회
        ChatRoom room = chatService.findRoomById(chatMessage.getRoomId());

        // 해당 채팅방에 있는 모든 클라이언트에게 타입에 따른 메시지 발송
        room.handleActions(session, chatMessage, chatService);
    }
}
