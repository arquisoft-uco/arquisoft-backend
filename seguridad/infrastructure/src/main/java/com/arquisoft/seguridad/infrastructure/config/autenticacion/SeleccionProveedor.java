package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import com.arquisoft.shared.util.UtilTexto;
import jakarta.servlet.http.HttpServletRequest;

public final class SeleccionProveedor {

    public static final String CABECERA = "X-Proveedor-Identidad";
    public static final String BEARER = "bearer";

    public static final String MOTIVO_DESCONOCIDO = "desconocido";
    public static final String MOTIVO_DESHABILITADO = "deshabilitado";
    public static final String MOTIVO_SIN_CREDENCIAL = "sin-credencial";

    private SeleccionProveedor() {}

    public sealed interface Seleccion {

        record NoActua() implements Seleccion {}

        record Rechazada(String motivo) implements Seleccion {}

        record Elegida(ProveedorAutenticacion proveedor, String credencial) implements Seleccion {}
    }

    public static Seleccion resolver(HttpServletRequest request, boolean hayBearerAutenticado,
                                     ProveedoresAutenticacion registro) {
        var valor = UtilTexto.aplicarTrim(request.getHeader(CABECERA));
        if (UtilTexto.esVacioONulo(valor)) {
            return hayBearerAutenticado ? new Seleccion.NoActua() : primeroConCredencial(request, registro);
        }
        if (BEARER.equalsIgnoreCase(valor)) {
            return new Seleccion.NoActua();
        }
        var forzado = registro.porId(valor);
        if (forzado.isEmpty()) {
            return new Seleccion.Rechazada(registro.contiene(valor) ? MOTIVO_DESHABILITADO : MOTIVO_DESCONOCIDO);
        }
        var proveedor = forzado.get();
        return proveedor.extraerCredencial(request)
                .<Seleccion>map(credencial -> new Seleccion.Elegida(proveedor, credencial))
                .orElseGet(() -> new Seleccion.Rechazada(MOTIVO_SIN_CREDENCIAL));
    }

    private static Seleccion primeroConCredencial(HttpServletRequest request, ProveedoresAutenticacion registro) {
        for (var proveedor : registro.habilitados()) {
            var credencial = proveedor.extraerCredencial(request);
            if (credencial.isPresent()) {
                return new Seleccion.Elegida(proveedor, credencial.get());
            }
        }
        return new Seleccion.NoActua();
    }
}
