package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import com.arquisoft.shared.util.UtilTexto;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.time.Instant;
import java.util.List;

public final class SesionAuthenticationToken extends AbstractAuthenticationToken implements IdentidadAutenticada {

    private final String proveedor;
    private final String sujeto;
    private final String nombre;
    private final String correo;
    private final String tenant;

    public SesionAuthenticationToken(String proveedor, String sujeto, String nombre, String correo, String tenant) {
        super(List.of());
        this.proveedor = proveedor;
        this.sujeto = sujeto;
        this.nombre = nombre;
        this.correo = correo;
        this.tenant = tenant;
        setAuthenticated(true);
    }

    public String getTenant() {
        return tenant;
    }

    @Override
    public String proveedor() {
        return proveedor;
    }

    @Override
    public String emisor() {
        return proveedor;
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
        return UtilTexto.esVacioONulo(nombre) ? correo : nombre;
    }

    @Override
    public List<String> roles() {
        return List.of();
    }

    @Override
    public String idToken() {
        return null;
    }

    @Override
    public Instant expiraEn() {
        return null;
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
