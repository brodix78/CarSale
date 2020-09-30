package ru.carSale.listener;

import ru.carSale.store.Store;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InitContext implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Store store = new Store();
        sce.getServletContext().setAttribute("store", store);
    }
}
