package com.es.phoneshop.model.order.dao;

import com.es.phoneshop.model.general.dao.AbstractDao;
import com.es.phoneshop.model.order.Order;

import java.util.Optional;

public class ArrayListOrderDao extends AbstractDao<Order> implements OrderDao {

    private ArrayListOrderDao() {
    }

    private static class SingletonHelper {
        private static final ArrayListOrderDao INSTANCE = new ArrayListOrderDao();
    }

    public static ArrayListOrderDao getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public Optional<Order> getOrderBySecureId(String secureId) {
        return getItemList().stream()
                .filter(i -> i.getSecureId().equals(secureId))
                .findAny();
    }

    @Override
    public Optional<Order> getOrder(Long id) {
        return getItem(id);
    }
}