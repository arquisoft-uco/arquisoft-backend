package com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model;

public record SolicitudAcceso(String metodo, String recurso, String credencial, String emisor, String correlacion) {

    @Override
    public String toString() {
        return "SolicitudAcceso[metodo=" + metodo + ", recurso=" + recurso
                + ", credencial=***, emisor=" + emisor + ", correlacion=" + correlacion + "]";
    }
}
