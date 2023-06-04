package com.xlg.component.ks.utils;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * @author
 * Created on 2023-05-27
 */
public interface GetByShardCursorDAO<ShardKey, Id, Entity> {

    List<Entity> getByCursor(@Nonnull ShardKey shardKey, @Nonnull Id start, int limitCount);
}
