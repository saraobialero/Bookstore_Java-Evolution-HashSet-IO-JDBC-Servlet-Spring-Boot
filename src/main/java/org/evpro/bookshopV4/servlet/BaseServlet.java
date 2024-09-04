package org.evpro.bookshopV4.servlet;

import org.evpro.bookshopV4.utilities.HandlerMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseServlet extends HttpServlet {
    private Map<String, Method> pathToMethodMap = new HashMap<>();

    @Override
    public void init() throws ServletException {
        super.init();
        for (Method method : getClass().getDeclaredMethods()) {
            HandlerMapping mapping = method.getAnnotation(HandlerMapping.class);
            if (mapping != null) {
                pathToMethodMap.put(mapping.path() + ":" + mapping.method(), method);
            }
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        String httpMethod = req.getMethod();
        log("Received request: " + httpMethod + " " + path);

        Method method = pathToMethodMap.get(path + ":" + httpMethod);

        if (method != null) {
            log("Invoking method: " + method.getName());
            try {
                method.invoke(this, req, resp);
            } catch (Exception e) {
                log("Error invoking method", e);
                throw new ServletException("Error invoking method for " + path, e);
            }
        } else {
            log("No method found for: " + httpMethod + " " + path);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}