package ru.carSale.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.carSale.model.Advert;
import ru.carSale.model.Customer;
import ru.carSale.model.Filter;
import ru.carSale.store.Store;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdvertServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        Store store = (Store) getServletContext().getAttribute("store");
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        Integer id;
        String json = request.getParameter("filter");
        if (json != null) {
            json = URLDecoder.decode(json, "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            Filter filter = null;
            try {
                filter = mapper.readValue(json, new TypeReference<>(){});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if (filter != null) {
                List<Advert> adverts = store.advertsByFilter(filter);
                adverts.stream().forEach(advert -> advert = advert.forJson());
                json = new JSONArray(adverts).toString();
                if (json != null) {
                    writeResp(response, json);
                }
            }
        } else if ((id = Integer.parseInt(request.getParameter("advertId"))) > 0) {
            Advert advert = store.advertById(id).forJson();
            if(advert != null) {
                writeResp(response, new JSONObject(advert).toString());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Store store = (Store) getServletContext().getAttribute("store");
        Customer customer = (Customer) request.getSession().getAttribute("customer");
        Advert advert = Advert.ofLazyMap(getObject(request, new LinkedHashMap()));
        if (advert != null && (advert.getId() >= 0)) {
            advert.setCustomer(customer);
            advert.setConfig(store.configById(advert.getConfig().getId()));
            advert = store.saveAdvert(advert);
        }
        advert = advert.forJson();
        System.out.println(advert);
        writeResp(response, new JSONObject(advert.forJson()).toString());
    }

    private <T> T getObject(HttpServletRequest request, T obj) {
        try {
            String line;
            JSONObject jo = null;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jo = new JSONObject(line);
            }
            ObjectMapper mapper = new ObjectMapper();
            if (jo != null && !jo.isEmpty()) {
                obj = mapper.readValue(jo.toString(), new TypeReference<>(){});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    private void writeResp(HttpServletResponse response, String data) {
        try {
            PrintWriter pw = response.getWriter();
            pw.write(data);
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
