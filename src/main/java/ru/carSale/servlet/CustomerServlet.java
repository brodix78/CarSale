package ru.carSale.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.carSale.model.Customer;
import ru.carSale.model.Passport;
import ru.carSale.store.Store;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;

public class CustomerServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        Store store = (Store) getServletContext().getAttribute("store");//
        Customer customer = (Customer) request.getSession().getAttribute("customer");
      // Исключитьлено в целях автологирования для отладки
      /*  if (customer.getId() == 0) {
            customer = store.getAuth(Passport.of("tester", "123456"));
            request.getSession().setAttribute("customer", customer);
        }*/
        if (customer == null) {
            customer = Customer.emptyCustomer();
            request.getSession().setAttribute("customer", customer);
        }
        try {
            PrintWriter pw = response.getWriter();
            pw.write(new JSONObject(customer.forJson()).toString());
            pw.flush();
        } catch (Exception e) {
            logger.error("Response writing mistake");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Store store = (Store) getServletContext().getAttribute("store");
        Customer customer = Customer.emptyCustomer();
            try {
                BufferedReader reader = request.getReader();
                String line;
                JSONObject jo = null;
                while ((line = reader.readLine()) != null) {
                    jo = new JSONObject(line);
                }
                ObjectMapper mapper = new ObjectMapper();
                if (jo != null && !jo.isEmpty()) {
                    customer = mapper.readValue(jo.toString(), new TypeReference<>(){});
                }
                if (customer.getId() > 0) {
                    customer.setId(0);
                }
            } catch (Exception e) {
                logger.warn("Request JSON parsing issue");
                e.printStackTrace();
            }
            if (customer.getId() == -500) {
                customer = store.saveCustomer(customer);
            } else if(customer.getId() == 0) {
                customer = store.getAuth(Passport.of(customer.getLogin(), customer.getPassword()));
            }
            if (customer.getId() > 0) {
                request.getSession().setAttribute("customer", customer);
            }
        try {
            PrintWriter pw = response.getWriter();
            pw.write(new JSONObject(customer.forJson()).toString());
            pw.flush();
        } catch (Exception e) {
            logger.error("Response writing mistake");
            e.printStackTrace();
        }
    }
}