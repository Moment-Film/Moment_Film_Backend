package com.team_7.moment_film.domain.customfilter.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFilter is a Querydsl query type for Filter
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFilter extends EntityPathBase<Filter> {

    private static final long serialVersionUID = -2082813807L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFilter filter = new QFilter("filter");

    public final StringPath blur = createString("blur");

    public final StringPath contrast = createString("contrast");

    public final StringPath grayscale = createString("grayscale");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.team_7.moment_film.domain.post.entity.QPost post;

    public final StringPath sepia = createString("sepia");

    public final com.team_7.moment_film.domain.user.entity.QUser user;

    public QFilter(String variable) {
        this(Filter.class, forVariable(variable), INITS);
    }

    public QFilter(Path<? extends Filter> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFilter(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFilter(PathMetadata metadata, PathInits inits) {
        this(Filter.class, metadata, inits);
    }

    public QFilter(Class<? extends Filter> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new com.team_7.moment_film.domain.post.entity.QPost(forProperty("post")) : null;
        this.user = inits.isInitialized("user") ? new com.team_7.moment_film.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

