package ru.carSale.servlet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.carSale.model.car.Brand;
import ru.carSale.store.Store;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

public class CarServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CarServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        Store store = (Store) getServletContext().getAttribute("store");
        try {
            String json = null;
            Integer id = Integer.parseInt(request.getParameter("reqId"));
            if ("brand".equals(request.getParameter("get"))) {
                if (id == -100) {
                    List<Brand> brands = store.getLazyBrands();
                    if (brands.size() > 0) {
                        json = new JSONArray(brands).toString();
                    }
                } else if (id > 0) {
                    Brand br = store.brandById(id);
                    System.out.println("BRAND:  " + br.toString());
                    json = new JSONObject(store.brandById(id).forJson()).toString();
                    System.out.println("JSON:  " + json);
                }
            } else if ("model".equals(request.getParameter("get")) && id > 0) {
                json = new JSONObject(store.modelById(id).forJson()).toString();
            } else if ("generation".equals(request.getParameter("get")) && id > 0) {
                json = new JSONObject(store.generationById(id).forJson()).toString();
            } else if ("config".equals(request.getParameter("get")) && id > 0) {
                json = new JSONObject(store.configById(id).forJson()).toString();
            }
            System.out.println("SEND : " + json);
            if (json != null) {
                PrintWriter pw = response.getWriter();
                pw.write(json);
            }
        }  catch (Exception e) {
            logger.error("Car data parsing/sending error");
            e.printStackTrace();
        }
    }
}