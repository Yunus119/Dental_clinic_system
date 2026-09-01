package com.dentalclinic.filter;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// runs before every request, checks if the person is logged in
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    // pages that don't require login - everyone can reach these
    private static final Set<String> PUBLIC_PAGES = Set.of(
            "/login", "/login.jsp"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getServletPath();

        // let public pages through with no check
        if (PUBLIC_PAGES.contains(path)) {
            chain.doFilter(req, res);
            return;
        }

        // everything else requires a logged in user
        HttpSession session = request.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("currentUser") != null);

        if (!loggedIn) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // logged in, let the request continue to whatever servlet/jsp it was headed to
        chain.doFilter(req, res);
    }
}