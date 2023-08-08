package com.team_7.moment_film.domain.comment.service;


import com.amazonaws.services.kms.model.NotFoundException;
import com.team_7.moment_film.domain.comment.dto.CommentRequestDTO;
import com.team_7.moment_film.domain.comment.entity.Comment;
import com.team_7.moment_film.domain.comment.mapper.CommentRequestMapper;
import com.team_7.moment_film.domain.comment.repository.CommentRepository;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.post.repository.PostRepository;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postrepository;
    private final UserRepository userRepository;
    private final CommentRequestMapper commentRequestMapper;


    public CustomResponseEntity<?> createComment(Long postId, CommentRequestDTO requestDTO, UserDetailsImpl userDetails){
        User user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Post post = getPostById(postId);

        Comment comment = commentRequestMapper.toEntity(requestDTO);

        Comment parentcomment;
        if(requestDTO.getParentId() != null){
            parentcomment = commentRepository.findById(requestDTO.getParentId())
                    .orElseThrow(()-> new NotFoundException("댓글을 찾을 수 없습니다." + requestDTO.getParentId()));
            comment.updateParent(parentcomment);
        }

        comment.updateWriter(user);
        comment.updatePost(post);
        commentRepository.save(comment);

        return new CustomResponseEntity<>(HttpStatus.CREATED,"댓글 성공",comment);
    }

    public CustomResponseEntity<?> deleteComment(Long postId, Long commentId, UserDetailsImpl userDetails){
        getPostById(postId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if(comment.getChildren().size() != 0){ // 자식이 있으면 상태 변경
            comment.changeIsDeleted(true);
        } else {
            commentRepository.delete(getDeletableAncestorComment(comment));
        }
        commentRepository.delete(comment);
        return CustomResponseEntity.msgResponse(HttpStatus.OK,"삭제 성공!");
    }





    private Comment getDeletableAncestorComment(Comment comment){
        Comment parent = comment.getParent();
        if(parent != null && parent.getChildren().size() == 1 && parent.getIsDeleted()) return getDeletableAncestorComment(parent);
        return comment;
    }

    private Post getPostById(Long postId) {
        return postrepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    public void update(Long commentId, CommentRequestDTO commentRequestDTO) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Could not found comment id : " + commentId));
        // 해당 메서드를 호출하는 사옹자와 댓글을 작성한 작성자가 같은지 확인하는 로직이 필요함
        comment.updateContent(commentRequestDTO.getContent());
    }
}
