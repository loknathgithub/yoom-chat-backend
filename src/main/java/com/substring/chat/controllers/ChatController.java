package com.substring.chat.controllers;

import com.substring.chat.entities.Message;
import com.substring.chat.entities.Room;
import com.substring.chat.payload.MessageRequest;
import com.substring.chat.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Controller
//@CrossOrigin("*")
public class ChatController {

    @Autowired
    private RoomRepository roomRepository;


    //for sending and receiving messages
    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public Message sendMessage(
            @DestinationVariable String roomId,
            MessageRequest request
    ) {
        String decodedRoomId = URLDecoder.decode(roomId, StandardCharsets.UTF_8);
        System.out.println("📩 Received message for room: " + decodedRoomId);
        System.out.println("📩 Payload: " + request.getSender() + " -> " + request.getContent());

        Room room = roomRepository.findByRoomId(decodedRoomId);
        Message message = new Message();
        message.setContent(request.getContent());
        message.setSender(request.getSender());
        message.setTimeStamp(LocalDateTime.now());

        if (room != null) {
            room.getMessages().add(message);
            roomRepository.save(room);
        } else {
            throw new RuntimeException("room not found !!");
        }

        return message;
    }
}
