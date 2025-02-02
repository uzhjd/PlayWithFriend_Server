package com.octopus.friends.controller;

import com.octopus.friends.common.domain.SingleResponse;
import com.octopus.friends.common.domain.enums.Status;
import com.octopus.friends.common.service.ResponseService;
import com.octopus.friends.dto.request.chat.CreateChatRoomRequestDto;
import com.octopus.friends.dto.request.chat.JoinChatRoomRequestDto;
import com.octopus.friends.dto.response.chat.ChatRoomRelationResponseDto;
import com.octopus.friends.dto.response.chat.ChatRoomResponseDto;
import com.octopus.friends.dto.response.chat.CreateChatRoomResponseDto;
import com.octopus.friends.dto.response.chat.JoinChatRoomResponseDto;
import com.octopus.friends.service.ChatRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 패키지명 com.octopus.friends.controller
 * 클래스명 ChatRoomController
 * 클래스설명
 * 작성일 2022-09-17
 *
 * @author 원지윤
 * @version 1.0
 * [수정내용]
 * 예시) [2022-09-17] 주석추가 - 원지윤
 * [2022-09-21] 채팅방 입장 시 topic을 생성할 수 있도록 수정 - 원지윤
 * [2022-09-27] userId -> userEmail로 수정 - 원지윤
 * [2022-09-27] 주석 추가 - 원지윤
 */
@Slf4j
@Tag(name = "chatRoom", description = "채팅방 관리 관련 API")
@RestController
@AllArgsConstructor
@RequestMapping("/api/chat/room")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ResponseService responseService;

    /**
     * user가 새로운 채팅방을 생성
     * @param userEmail 로그인한 user의 email로 header에 담겨옴
     * @param request 새로운 채팅방을 생성하기 위한 정보
     * @return 생성된 채팅방의 정보와 response 상태
     */
    @PostMapping
    public ResponseEntity<SingleResponse<CreateChatRoomResponseDto>> createChatRoom(@RequestHeader("USER-EMAIL") String userEmail,
                                                                                    @RequestBody CreateChatRoomRequestDto request){
        CreateChatRoomResponseDto chatRoom = chatRoomService.save(userEmail,request);
        SingleResponse<CreateChatRoomResponseDto> response = responseService.getSingleResponse(chatRoom,Status.SUCCESS_CREATED_CHATROOM);
        return ResponseEntity.ok().body(response);
    }

    /**
     * user가 기존의 채팅방에 입장
     * @param request 입장할 채팅방의 정보
     * @return 입장한 채팅방의 정보와 response상태
     */
    @PostMapping("/join")
    public ResponseEntity<SingleResponse<JoinChatRoomResponseDto>> joinChatRoom(@RequestBody JoinChatRoomRequestDto request){
        JoinChatRoomResponseDto joinChatRoom = chatRoomService.joinChatRoom(request);
        SingleResponse<JoinChatRoomResponseDto> response = responseService.getSingleResponse(joinChatRoom,Status.SUCCESS_JOINED_CHATROOM);
        return ResponseEntity.ok().body(response);
    }

    /**
     * user가 참여하고 있던 채팅방에서 나가기
     * @param userEmail 로그인한 유저의 email
     * @param roomIdx 나가기를 요청한 채팅방의 idx
     * @return 요청에 대한 응답
     */
    @PostMapping("/leave/{roomIdx}")
    public ResponseEntity<SingleResponse<ChatRoomRelationResponseDto>> leaveChatRoom(@RequestHeader("USER-EMAIL") String userEmail,
                                                                                     @PathVariable Long roomIdx){
        ChatRoomRelationResponseDto chatRoomRelation = chatRoomService.leaveChatRoom(userEmail, roomIdx);
        SingleResponse<ChatRoomRelationResponseDto> response = responseService.getSingleResponse(chatRoomRelation, Status.SUCCESS_DELETED_CHATROOM);
        return  ResponseEntity.ok().body(response);
    }

    /**
     * 로그인한 유저가 기존의 채팅방에 입장
     * @param userEmail 로그인한 유저의 email
     * @param roomIdx 참여하려는 채팅방의 idx
     * @return
     */
    @GetMapping("/enter/{roomIdx}")
    public ResponseEntity<SingleResponse<ChatRoomResponseDto>> enterChatRoom(@RequestHeader("USER-EMAIL") String userEmail,
                                                                             @PathVariable Long roomIdx){
        chatRoomService.enterChatRoom(roomIdx.toString());
        ChatRoomResponseDto chatRoomResponseDto = chatRoomService.findRoomByRoomIdx(userEmail, roomIdx);
        SingleResponse<ChatRoomResponseDto> response = responseService.getSingleResponse(chatRoomResponseDto, Status.SUCCESS_ENTERED_CHATROOM);
        return ResponseEntity.ok().body(response);
    }

    /**
     * 로그인한 유저가 참여중인 모든 채팅방 조회
     * @param userEmail
     * @return
     */
    @GetMapping
    public ResponseEntity<SingleResponse<List<ChatRoomRelationResponseDto>>> findAllByUserId(@RequestHeader("USER-EMAIL")String userEmail){
        List<ChatRoomRelationResponseDto> responses = chatRoomService.findAllByUserId(userEmail);
        SingleResponse<List<ChatRoomRelationResponseDto>> response = responseService.getSingleResponse(responses,Status.SUCCESS_SEARCHED_CHATROOM);
        return ResponseEntity.ok().body(response);
    }

    /**
     * 로그인한 user의 채팅방 하나 조회
     * @param userEmail 로그인한 user의 email
     * @param chatRoomIdx 선택한 채팅방의 idx
     * @return
     */
    @GetMapping("/{roomIdx}")
    public ResponseEntity<SingleResponse<ChatRoomResponseDto>> findAllByUserId(@RequestHeader("USER-EMAIL")String userEmail,
                                                                               @PathVariable("roomIdx") Long chatRoomIdx){
        ChatRoomResponseDto responses = chatRoomService.findRoomByRoomIdx(userEmail, chatRoomIdx);
        SingleResponse<ChatRoomResponseDto> response = responseService.getSingleResponse(responses,Status.SUCCESS_SEARCHED_CHATROOM);
        return ResponseEntity.ok().body(response);
    }

    /**
     * 모든 채팅방 조회
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity<SingleResponse<List<ChatRoomResponseDto>>> findAll(){
        List<ChatRoomResponseDto> responses = chatRoomService.findAll();
        SingleResponse<List<ChatRoomResponseDto>> response =
                responseService.getSingleResponse(responses,Status.SUCCESS_SEARCHED_CHATROOM);
        return ResponseEntity.ok().body(response);
    }


}
