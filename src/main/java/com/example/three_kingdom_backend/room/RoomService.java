package com.example.three_kingdom_backend.room;

import com.example.three_kingdom_backend.user.User;
import com.example.three_kingdom_backend.room.CreateRoomRequest;
import com.example.three_kingdom_backend.room.STATUS;
import com.example.three_kingdom_backend.room.Room;
import com.example.three_kingdom_backend.room.RoomRepository;
import com.example.three_kingdom_backend.room.RoomDTO;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.example.three_kingdom_backend.util.response.StandardResponse;
import com.example.three_kingdom_backend.user.UserRepository;
import com.example.three_kingdom_backend.room.RoomMemberRepository;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomMemberRepository roomMemberRepository;

    public RoomService(RoomRepository roomRepository, UserRepository userRepository,
            RoomMemberRepository roomMemberRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roomMemberRepository = roomMemberRepository;
    }

    public StandardResponse<RoomDTO> createRoom(CreateRoomRequest request, Long ownerId) {

        User owner = userRepository.findById(ownerId).orElseThrow(() -> new RuntimeException("User not found"));

        Room room = new Room();
        room.setPassword(request.getPassword());
        room.setDescription(request.getDescription());
        room.setStatus(STATUS.OPENING);
        room.setOwner(owner);
        var saved = roomRepository.save(room);

        RoomMember roomMember = new RoomMember();
        roomMember.setRoom(saved);
        roomMember.setUser(owner);
        roomMemberRepository.save(roomMember);

        return StandardResponse.create("200", RoomDTO.fromEntity(saved));
    }

    public StandardResponse<RoomDTO> getRoomById(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
        return StandardResponse.create("200", RoomDTO.fromEntity(room));
    }

    public StandardResponse<List<RoomDTO>> getRoomsByStatus(STATUS status) {
        List<RoomDTO> rooms = roomRepository.findByStatus(status).stream()
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
        return StandardResponse.create("200", rooms);
    }

    public StandardResponse<RoomDTO> joinRoom(Long roomId, String password, Long userId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (roomMemberRepository.findByRoomIdAndUserId(roomId, userId).isPresent()) {
            return StandardResponse.create("400", "User is already a member of this room", RoomDTO.fromEntity(room));
        }

        String roomPassword = room.getPassword();
        String providedPassword = password != null ? password : "";

        if (roomPassword != null && !roomPassword.isEmpty()) {
            if (!roomPassword.equals(providedPassword)) {
                return StandardResponse.createMessage("401", "Invalid password");
            }
        }

        RoomMember roomMember = new RoomMember();
        roomMember.setRoom(room);
        roomMember.setUser(user);
        roomMemberRepository.save(roomMember);

        return StandardResponse.create("200", "Joined room successfully", RoomDTO.fromEntity(room));
    }

    public StandardResponse<RoomDTO> leaveRoom(Long roomId, Long userId) {
        RoomMember roomMember = roomMemberRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new RuntimeException("User is not a member of this room"));
        roomMemberRepository.delete(roomMember);
        return StandardResponse.createMessage("200", "Left room successfully");
    }
}
