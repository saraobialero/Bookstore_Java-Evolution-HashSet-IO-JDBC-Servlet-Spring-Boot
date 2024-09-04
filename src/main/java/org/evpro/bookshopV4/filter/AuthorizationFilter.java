package org.evpro.bookshopV4.filter;

import org.evpro.bookshopV4.model.User;
import org.evpro.bookshopV4.model.enums.UserRole;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        if (path.equals("/authentication/login") || path.equals("/authentication/signup")) {
            chain.doFilter(request, response);
            return;
        }


        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            if (isAdminResource(httpRequest) && !user.getRole().equals(UserRole.ADMIN)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isAdminResource(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.startsWith("/admin");
    }

}