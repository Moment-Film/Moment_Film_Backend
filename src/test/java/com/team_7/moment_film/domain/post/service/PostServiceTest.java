package com.team_7.moment_film.domain.post.service;

import com.nimbusds.oauth2.sdk.ProtectedResourceRequest;
import com.team_7.moment_film.domain.customfilter.entity.Filter;
import com.team_7.moment_film.domain.customfilter.repository.FilterRepository;
import com.team_7.moment_film.domain.customframe.entity.Frame;
import com.team_7.moment_film.domain.customframe.repository.FrameRepository;
import com.team_7.moment_film.domain.post.dto.PostRequestDto;
import com.team_7.moment_film.domain.post.repository.PostRepository;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.mockito.Mockito.*;


@SpringBootTest
@RequiredArgsConstructor
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private FilterRepository filterRepository;

    @Mock
    private FrameRepository frameRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void testCreatePost() {
        // 필요한 Mock 설정
        Frame mockFrame = new Frame();
        when(frameRepository.findById(any())).thenReturn(Optional.of(mockFrame));

        Filter mockFilter = new Filter();
        when(filterRepository.findById(any())).thenReturn(Optional.of(mockFilter));

        User mockUser = mock(User.class);

        when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));

        when(s3Service.upload(any(), any())).thenReturn("mockImageUrl");

        // 테스트할 메서드 호출
        PostRequestDto requestDto = new PostRequestDto("제목","내용",1L,1L);
        MultipartFile mockImage = mock(MultipartFile.class);
        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

        ResponseEntity<ApiResponse> response = postService.createPost(requestDto, mockImage, userDetails);

        // 예상 동작 검증
        verify(postRepository, times(1)).save(any());
        verify(s3Service, times(1)).generateOriginalImageUrl(any(), any());


    }

}