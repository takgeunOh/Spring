package com.websocket.chat.controller;

import com.websocket.chat.model.ChatMessage;
import com.websocket.chat.repo.ChatRoomRepository;
import com.websocket.chat.service.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;


// 클라이언트가 채팅방 입장 시 채팅방(topic)에서 대화가 가능하도록 리스너 연동하는 enterChatRoom 메서드 세팅
// 채팅방에 발행된 메시지는 서로 다른 서버에 공유하기 위해 redis의 Topic으로 발행

@RequiredArgsConstructor
@Controller
public class ChatController {

//  private final SimpMessageSendingOperations messagingTemplate;
//	private final RedisPublisher redisPublisher;
//	private final ChatRoomRepository chatRoomRepository;
	
	private final RedisTemplate<String, Object> redisTemplate;
	private final JwtTokenProvider jwtTokenProvider;
	private final ChannelTopic channelTopic;
	

	// Websocket "/pub/chat/message" 로 들어오는 메시징 처리
    @MessageMapping("/chat/message")
    public void message(ChatMessage message, @Header("token") String token) {
    	
    	String nickname = jwtTokenProvider.getUserNameFromJwt(token);
    	
    	// 로그인 회원 정보로 대화명 설정하기
    	message.setSender(nickname);
    	
    	// 채팅방 입장 시 대화명과 메시지 자동 세팅
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
//        	chatRoomRepository.enterChatRoom(message.getRoomId());
//        	message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        	message.setSender("[알림]");
        	message.setMessage(nickname + "님이 입장하셨습니다.");
        }
        
        // messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
        
        // Websocket에 발행된 메시지를 redis로 발행(publish)
//        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}