package com.team_7.moment_film.domain.alarm.service;


import com.team_7.moment_film.domain.alarm.controller.NotificationController;
import com.team_7.moment_film.domain.like.entity.Like;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.post.repository.PostRepository;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import com.team_7.moment_film.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final PostRepository postRepository;


    public SseEmitter subscribe(Long userId){
        //현재 클라이언트를 위한 sseEmitter 객채 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        //연결
        try{
            sseEmitter.send(SseEmitter.event().name("알림 연결!"));
        } catch (IOException e){
            new IllegalArgumentException("알림 연결 실패");
        }

        // 3.저장
        NotificationController.sseEmitters.put(userId, sseEmitter);

        //4.연결 종료 처리
        sseEmitter.onCompletion(() -> NotificationController.sseEmitters.remove(userId));
        sseEmitter.onTimeout(() -> NotificationController.sseEmitters.remove(userId));
        sseEmitter.onError((e) -> NotificationController.sseEmitters.remove(userId));

        return sseEmitter;
    }

    public void notifyLike(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new IllegalArgumentException("게시글을 찾을 수 없습니다.")
        );
        //게시글 id나 (누른 유저 이름, id)
        Long userId = post.getUser().getId();
        String username = post.getUser().getUsername();
        if(NotificationController.sseEmitters.containsKey(userId)){
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(userId);
            try{
                sseEmitter.send(SseEmitter.event().name("addLike").data(username+"게시물에 좋아요를 눌렀습니다."));
            }catch (Exception e){
                NotificationController.sseEmitters.remove(userId);
            }
        }
    }
}
