package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class IdentidadTrazaFilter extends OncePerRequestFilter {

    private final GestorTraza gestorTraza;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion != null && autenticacion.getPrincipal() instanceof Jwt jwt) {
            gestorTraza.registrarUsuario(jwt.getSubject());
        }
        filterChain.doFilter(request, response);
    }
}
