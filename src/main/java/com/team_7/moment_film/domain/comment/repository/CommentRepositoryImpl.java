package com.team_7.moment_film.domain.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team_7.moment_film.domain.comment.dto.CommentResponseDTO;
import com.team_7.moment_film.domain.comment.entity.Comment;
import lombok.RequiredArgsConstructor;

import java.util.*;

import static com.team_7.moment_film.domain.comment.dto.CommentResponseDTO.convertCommentToDto;
import static com.team_7.moment_film.domain.comment.entity.QComment.comment;


@RequiredArgsConstructor
public class CommentRepositoryImpl implements CustomCommentRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommentResponseDTO> findByPostId(Long id){

        List<Comment> comments = queryFactory.selectFrom(comment)
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.post.id.eq(id))
                .orderBy(comment.parent.id.asc().nullsFirst(),
                        comment.createdAt.asc())
                .fetch();

        List<CommentResponseDTO> commentResponseDTOList = new ArrayList<>();
        Map<Long, CommentResponseDTO> commentDTOHashMap = new HashMap<>();

        comments.forEach(c -> {
            CommentResponseDTO commentResponseDTO = convertCommentToDto(c);
            commentDTOHashMap.put(commentResponseDTO.getId(), commentResponseDTO);
            if(c.getParent() != null) commentDTOHashMap.get(c.getParent().getId()).getChildren().add(commentResponseDTO);
            else commentResponseDTOList.add(commentResponseDTO);
        });

        return commentResponseDTOList;

    }

    @Override
    public Optional<Comment> findCommentByIdWithParent(Long id) {

        Comment selectedComment = queryFactory.select(comment)
                .from(comment)
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(selectedComment);
    }
}
