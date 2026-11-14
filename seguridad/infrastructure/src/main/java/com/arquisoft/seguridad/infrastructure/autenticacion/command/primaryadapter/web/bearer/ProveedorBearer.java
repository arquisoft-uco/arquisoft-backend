package com.arquisoft.seguridad.infrastructure.autenticacion.command.primaryadapter.web.bearer;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionPorEmisorConfig;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.BearerEmisorForzadoToken;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.IdentidadAutenticada;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedorAutenticacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SeleccionProveedor;
import com.arquisoft.shared.util.UtilTexto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ProveedorBearer implements ProveedorAutenticacion {

    private static final String PREFIJO_BEARER = "Bearer ";

    private final AutenticacionProperties propiedades;
    private final AuthenticationManager gestorAutenticacion;
    private final DefaultBearerTokenResolver resolvedorToken = new DefaultBearerTokenResolver();

    public ProveedorBearer(AutenticacionProperties propiedades,
                           @Qualifier(AutenticacionPorEmisorConfig.BEAN_GESTOR_BEARER)
                           AuthenticationManager gestorAutenticacion) {
        this.propiedades = propiedades;
        this.gestorAutenticacion = gestorAutenticacion;
    }

    @Override
    public String id() {
        return SeleccionProveedor.BEARER;
    }

    @Override
    public boolean habilitado() {
        return !propiedades.habilitados().isEmpty();
    }

    @Override
    public Optional<ModoAutorizacion> autorizacion(String emisor) {
        return propiedades.porId(emisor).map(AutenticacionProperties.Emisor::authorization);
    }

    @Override
    public Optional<String> emisorHabilitado(String selector) {
        return propiedades.porIdIgnorandoMayusculas(selector).map(AutenticacionProperties.Emisor::id);
    }

    @Override
    public boolean declaraEmisor(String selector) {
        return propiedades.declara(selector);
    }

    @Override
    public boolean exigeProteccionCsrf() {
        return false;
    }

    @Override
    public boolean autenticaPrimero() {
        return true;
    }

    @Override
    public boolean actuaEnRutasNoDeNegocio() {
        return true;
    }

    @Override
    public Optional<String> extraerCredencial(HttpServletRequest request) {
        return Optional.ofNullable(resolvedorToken.resolve(request)).filter(token -> !UtilTexto.esVacioONulo(token));
    }

    @Override
    public void adjuntarCredencial(HttpHeaders cabeceras, String credencial) {
        cabeceras.set(HttpHeaders.AUTHORIZATION, PREFIJO_BEARER + credencial);
    }

    @Override
    public IdentidadAutenticada autenticar(String credencial, String emisor) {
        var token = UtilTexto.esVacioONulo(emisor)
                ? new BearerTokenAuthenticationToken(credencial)
                : new BearerEmisorForzadoToken(credencial, emisor);
        return (IdentidadAutenticada) gestorAutenticacion.authenticate(token);
    }
}
