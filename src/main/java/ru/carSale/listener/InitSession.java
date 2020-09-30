package ru.carSale.listener;

import ru.carSale.model.Customer;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class InitSession implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        event.getSession().setAttribute("customer", Customer.emptyCustomer());
    }
}
