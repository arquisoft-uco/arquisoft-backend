package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import com.arquisoft.shared.util.UtilColeccion;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.time.Instant;
import java.util.List;

public final class BearerAuthenticationToken extends AbstractAuthenticationToken implements IdentidadAutenticada {

    private final String emisor;
    private final String sujeto;
    private final String correo;
    private final String nombreUsuario;
    private final List<String> roles;
    private final String idToken;
    private final Instant expiraEn;

    public BearerAuthenticationToken(String emisor, String sujeto, String correo, String nombreUsuario,
                                     List<String> roles, String idToken, Instant expiraEn) {
        super(List.of());
        this.emisor = emisor;
        this.sujeto = sujeto;
        this.correo = correo;
        this.nombreUsuario = nombreUsuario;
        this.roles = UtilColeccion.aplicarPorDefecto(roles);
        this.idToken = idToken;
        this.expiraEn = expiraEn;
        setAuthenticated(true);
    }

    @Override
    public String proveedor() {
        return SeleccionProveedor.BEARER;
    }

    @Override
    public String emisor() {
        return emisor;
    }

    @Override
    public String sujeto() {
        return sujeto;
    }

    @Override
    public String correo() {
        return correo;
    }

    @Override
    public String nombreUsuario() {
        return nombreUsuario;
    }

    @Override
    public List<String> roles() {
        return roles;
    }

    @Override
    public String idToken() {
        return idToken;
    }

    @Override
    public Instant expiraEn() {
        return expiraEn;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return sujeto;
    }

    @Override
    public String getName() {
        return sujeto;
    }
}
