package com.team_7.moment_film.domain.customframe.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFrame is a Querydsl query type for Frame
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFrame extends EntityPathBase<Frame> {

    private static final long serialVersionUID = 769290215L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFrame frame = new QFrame("frame");

    public final StringPath frameName = createString("frameName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.team_7.moment_film.domain.post.entity.QPost post;

    public final com.team_7.moment_film.domain.user.entity.QUser user;

    public QFrame(String variable) {
        this(Frame.class, forVariable(variable), INITS);
    }

    public QFrame(Path<? extends Frame> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFrame(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFrame(PathMetadata metadata, PathInits inits) {
        this(Frame.class, metadata, inits);
    }

    public QFrame(Class<? extends Frame> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new com.team_7.moment_film.domain.post.entity.QPost(forProperty("post")) : null;
        this.user = inits.isInitialized("user") ? new com.team_7.moment_film.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

