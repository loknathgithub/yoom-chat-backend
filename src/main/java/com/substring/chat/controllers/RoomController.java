package com.substring.chat.controllers;

import com.substring.chat.DTO.RoomResponseDTO;
import com.substring.chat.entities.Message;
import com.substring.chat.entities.Room;
import com.substring.chat.repositories.RoomRepository;
import com.substring.chat.utils.RoomIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
//import java.util.Map;

@RestController
@RequestMapping("/api/v1/rooms")
//@CrossOrigin("*")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping("/health")
    public String healthCheck(){
        return new String("Healthy");
    }

    //create room
    @PostMapping("/create/{roomName}")
    public ResponseEntity<?> createRoom(@PathVariable("roomName") String roomName) {

        // transform into random string
        String newRoomId;

        do{
            newRoomId = RoomIdGenerator.generate();
        }while(roomRepository.findByRoomId(newRoomId).isPresent());

        if (roomRepository.findByRoomId(newRoomId).isPresent()) {
            //room is already there
            return ResponseEntity.badRequest().body("Room already exists!");
        }

        //create new room
        Room room = new Room();
        room.setRoomId(newRoomId);
        room.setRoomName(roomName);

        Room savedRoom = roomRepository.save(room);

        // create a DTO which sends only the roomId and Id not messages to frontend: done
        RoomResponseDTO responseId = new RoomResponseDTO(savedRoom.getRoomId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseId);
    }


    //get room: join
    @GetMapping("/find/{roomId}")
    public ResponseEntity<?> joinRoom(
            @PathVariable("roomId") String roomId
    ) {

        Optional<Room> room = roomRepository.findByRoomId(roomId);

        if(room.isEmpty()) return ResponseEntity.notFound().build();
        Room actualRoom = room.get();

        return ResponseEntity.ok(actualRoom);
    }


    //get messages of room
    @GetMapping("/find/{roomId}/messages")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable String roomId,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "20", required = false) int size
    ) {
        Optional<Room> room = roomRepository.findByRoomId(roomId);

        if(room.isEmpty()) return ResponseEntity.notFound().build();
        Room actualRoom = room.get();

        //pagination
        List<Message> messages = actualRoom.getMessages();
        int start = Math.max(0, messages.size() - (page + 1) * size);
        int end = Math.min(messages.size(), start + size);
        List<Message> paginatedMessages = messages.subList(start, end);
        return ResponseEntity.ok(paginatedMessages);

    }


}
