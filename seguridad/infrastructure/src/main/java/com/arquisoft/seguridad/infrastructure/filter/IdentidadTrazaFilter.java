package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionAuthenticationToken;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilObjeto;
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
        if (UtilObjeto.noEsNulo(autenticacion)) {
            if (autenticacion.getPrincipal() instanceof Jwt jwt) {
                gestorTraza.registrarUsuario(jwt.getSubject());
            } else if (autenticacion instanceof SesionAuthenticationToken sesion) {
                gestorTraza.registrarUsuario(sesion.getSujeto());
            }
        }
        filterChain.doFilter(request, response);
    }
}
