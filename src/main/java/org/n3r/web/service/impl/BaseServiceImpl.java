package org.n3r.web.service.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.n3r.esql.Esql;
import org.n3r.web.common.utils.UUIDUtils;
import org.n3r.web.entity.BaseEntity;
import org.n3r.web.service.BaseService;
import org.springframework.util.Assert;

import com.google.common.base.Throwables;

public class BaseServiceImpl<T, PK extends Serializable> implements BaseService<T, PK> {

    private Class<T> entityClass;

    public BaseServiceImpl() {
        Class c = getClass();
        Type type = c.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
            this.entityClass = (Class<T>) parameterizedType[0];
        }
    }

    @Override
    public T get(PK id) {
        Assert.notNull(id, "id is required");
        return null;
    }

    @Override
    public T load(PK id) {
        return null;
    }

    @Override
    public List<T> getAllList() {
        return null;
    }

    @Override
    public Long getTotalCount() {
        return null;
    }

    @Override
    public PK save(T entity) {
        Assert.notNull(entity, "entity is required");
        try {

            String id = ((BaseEntity) entity).getId();
            if (StringUtils.isEmpty(id))
                ((BaseEntity) entity).setId(UUIDUtils.getUUID());

            Method method = entity.getClass().getMethod(BaseEntity.ON_SAVE_METHOD_NAME);
            method.invoke(entity);
        } catch (Exception e) {
            Throwables.propagate(e);
            return null;
        }
        new Esql().useSqlFile(getClass())
                .update(BaseEntity.ON_SAVE_METHOD_NAME + entity.getClass().getSimpleName())
                .params(entity).execute();
        return (PK) ((BaseEntity) entity).getId();
    }

    @Override
    public void update(T entity) {
    }

    @Override
    public void delete(T entity) {
    }

    @Override
    public void delete(PK id) {
    }

    @Override
    public void delete(PK[] ids) {
    }
}