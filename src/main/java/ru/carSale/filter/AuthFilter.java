package ru.carSale.filter;

import ru.carSale.model.Customer;
import ru.carSale.store.Store;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if(req.getMethod().equalsIgnoreCase("POST")) {
            Customer customer = (Customer) req.getSession().getAttribute("customer");
            if (customer != null && customer.getId() > 0) {
                Store store = (Store) request.getServletContext().getAttribute("store");
                if (!store.checkCustomer(customer)) {
                    customer = null;
                }
            }
            if (customer == null || customer.getId() == 0) {
                try {
                    req.getRequestDispatcher("auth.html").forward(req, resp);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}