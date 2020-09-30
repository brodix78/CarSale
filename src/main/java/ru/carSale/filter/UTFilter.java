package ru.carSale.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class UTFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain next)
            throws IOException, ServletException {
        String path = ((HttpServletRequest) request).getRequestURI();
        if (!path.endsWith("js") && !path.endsWith("image.do")) {
            request.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
        }
        next.doFilter(request, response);
    }
}
