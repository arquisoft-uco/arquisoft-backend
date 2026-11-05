package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

public final class SesionAuthenticationToken extends AbstractAuthenticationToken implements AutenticacionAutorizada {

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

    public String getProveedor() {
        return proveedor;
    }

    public String getSujeto() {
        return sujeto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTenant() {
        return tenant;
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
