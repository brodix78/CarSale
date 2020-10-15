package ru.carSale.filter;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.carSale.model.Customer;
import ru.carSale.servlet.CustomerServlet;
import ru.carSale.store.Store;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class AuthFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if("POST".equalsIgnoreCase(req.getMethod())) {
            Customer customer = (Customer) req.getSession().getAttribute("customer");
            if (customer != null && customer.getId() > 0) {
                Store store = (Store) request.getServletContext().getAttribute("store");
                if (!store.checkCustomer(customer)) {
                    customer = null;
                }
            }
            if (customer == null || customer.getId() == 0) {
                try {
                    PrintWriter pw = response.getWriter();
                    pw.write(new JSONObject(Map.of("redirect", true)).toString());
                    pw.flush();
                } catch (IOException e) {
                    logger.warn("Redirection of unauthorized customer issue");
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