package com.websocket.chat.repo;

import com.websocket.chat.model.ChatRoom;
import com.websocket.chat.pubsub.RedisSubscriber;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

// 채팅방 정보는 초기화 되지 않도록 생성 시 Redis Hash에 저장되도록 처리
// 채팅방 정보 조회할 떄는 Redis Hash에 저장된 데이터를 불러오도록 메서드 내용 수정
// 채팅방 입장 시 채팅방 id로 Redis topic을 조회하여 pub/sub 메시지 리스너와 연동

@RequiredArgsConstructor
@Service
public class ChatRoomRepository {

//    // 채팅방(topic)에 발행되는 메시지를 처리할 Listener
//	private final RedisMessageListenerContainer redisMessageListener;
//	
//	// 구독 처리 서비스
//	private final RedisSubscriber redisSubscriber;
	
	// Redis
	private static final String CHAT_ROOMS = "CHAT_ROOM";
	private final RedisTemplate<String, Object> redisTemplate;
	private HashOperations<String, String, ChatRoom> opsHashChatRoom;
	
//	// 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보.
//	// 서버별로 채팅방에 매치되는 topic 정보를 Map에 넣어 roomId로 찾을 수 있도록
//	private Map<String, ChannelTopic> topics;
	
	@PostConstruct
	private void init() {
		opsHashChatRoom = redisTemplate.opsForHash();
//		topics = new HashMap<>();
	}
	
	// 모든 채팅방 조회
	public List<ChatRoom> findAllRoom() {
		return opsHashChatRoom.values(CHAT_ROOMS);
	}
	
	// 특정 방 조회
	public ChatRoom findRoomById(String id) {
		return opsHashChatRoom.get(CHAT_ROOMS, id);
	}
	
	
	// 채팅방 생성 : 서버 간 채팅방 공유를 위해 redis hash에 저장
	public ChatRoom createChatRoom(String name) {
		ChatRoom chatRoom = ChatRoom.create(name);
		opsHashChatRoom.put(CHAT_ROOMS,  chatRoom.getRoomId(), chatRoom);
		return chatRoom;
	}
	
//	// 채팅방 입장 : redis에 topic을 만들고 pub/sub 통신을 하기 위해 리스너 설정
//	public void enterChatRoom(String roomId) {
//		ChannelTopic topic = topics.get(roomId);
//		if(topic == null) {
//			topic = new ChannelTopic(roomId);
//			redisMessageListener.addMessageListener(redisSubscriber, topic);
//			topics.put(roomId, topic);
//		}
//	}
//	
//	public ChannelTopic getTopic(String roomId) {
//		return topics.get(roomId);
//	}
}