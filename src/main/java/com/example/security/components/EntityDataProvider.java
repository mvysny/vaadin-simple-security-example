package com.example.security.components;

import com.gitlab.mvysny.jdbiorm.Dao;
import com.gitlab.mvysny.jdbiorm.OrderBy;
import com.gitlab.mvysny.jdbiorm.spi.AbstractEntity;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Vaadin {@link com.vaadin.flow.data.provider.DataProvider} which loads entities
 * using jdbi-orm.
 * @param <T> the entity type
 * @param <ID> the entity ID type
 */
public class EntityDataProvider<T extends AbstractEntity<ID>, ID> extends AbstractBackEndDataProvider<T, Void> {
    @NotNull
    private final Dao<T, ID> dao;

    public EntityDataProvider(@NotNull Dao<T, ID> dao) {
        this.dao = Objects.requireNonNull(dao);
    }

    @Override
    protected Stream<T> fetchFromBackEnd(Query<T, Void> query) {
        final List<OrderBy> orderBy = query.getSortOrders().stream()
                .map(it -> new OrderBy(it.getSorted(), it.getDirection() == SortDirection.ASCENDING ? OrderBy.ASC : OrderBy.DESC))
                .collect(Collectors.toList());
        return dao.findAll(orderBy, (long) query.getOffset(), (long) query.getLimit()).stream();
    }

    @Override
    protected int sizeInBackEnd(Query<T, Void> query) {
        return (int) dao.count();
    }
}
