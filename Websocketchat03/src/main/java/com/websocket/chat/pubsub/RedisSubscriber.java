package com.websocket.chat.pubsub;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.websocket.chat.model.ChatMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


// Redis 구독 서비스 구현
// Redis에 메시지 발행이 될 때까지 대기하였다가 메시지가 발행되면 해당 메시지를 읽어 처리하는 리스너

// MessageListener를 상속 받아 onMessage 메서드 재정의
// Redis에 메시지가 발행되면 해당 메시지를 ChatMessage로 변환하고 messaging Template을 이용하여 채팅방의 모든 websocket 클라이언트들에게 메시지 전달

// 메시지 리스너에게 메시지가 수신되면 아래 RedisSubscriber.sendMessage가 수행
// 수신된 메시지는 /sub/chat/room/{roomId}를 구독한 websocket 클라이언트에게 메시지 발송
@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber{
	
	private final ObjectMapper objectMapper;
	private final SimpMessageSendingOperations messagingTemplate;
	
	
	// Redis에서 메시지가 발행되면 대기하고 있던 Redis Subscriber가 해당 메시지를 받아 처리
	public void sendMessage(String publishMessage) {
		// TODO Auto-generated method stub
		try {
			// redis에서 발행된 데이터를 받아 deserialize
//			String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
			// ChatMessage 객체로 맵핑
			ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
			// 채팅방을 구독한 클라이언트에게 메시지 발송
			messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
		} catch (Exception e) {
			log.error("Exception {}", e);
		}
	}
	
}
