package com.es.phoneshop.model.general.dao;

import com.es.phoneshop.model.general.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class Dao<T extends Entity> {
    private Long maxId = 0L;
    private final List<T> itemList = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public List<T> getItemList() {
        return itemList;
    }

    public Optional<T> getItem(Long id) {
        return itemList.stream()
                .filter(i -> i.getId().equals(id))
                .findAny();
    }

    public void save(T entity) {
        lock.writeLock().lock();
        if (null == entity.getId()) {
            entity.setId(maxId++);
            itemList.add(entity);
        } else {
            update(entity);
        }
        lock.writeLock().unlock();
    }

    private void update(T entity) {
        int index = itemList.indexOf(entity);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        itemList.set(index, entity);
    }
}
