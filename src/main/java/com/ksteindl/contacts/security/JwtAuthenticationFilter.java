package com.ksteindl.contacts.security;

import com.ksteindl.contacts.domain.entities.AppUser;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String HEADER_STRING = "Authorization";
    @Autowired
    private JwtProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = httpServletRequest.getHeader(HEADER_STRING);
            Long userId = tokenProvider.getUserIdFromToken(jwt);
            AppUser user = customUserDetailsService.loadUserById(userId);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (SignatureException signatureException) {
            logger.info("Invalid JWT Signature");
        } catch (MalformedJwtException malformedJwtException) {
            logger.info("Invalid JWT token");
        } catch (UnsupportedJwtException unsupportedJwtException) {
            logger.info("Unsopported JWT token");
        } catch (IllegalArgumentException illegalArgumentException) {
            logger.info("JWT claims string is emptry");
        } catch (Exception exception) {
            logger.error("Could not set user authentication in security context", exception);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
