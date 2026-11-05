package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import com.arquisoft.shared.util.UtilTexto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public final class SeleccionProveedor {

    public static final String CABECERA = "X-Proveedor-Identidad";
    public static final String BEARER = "bearer";

    public static final String SIN_EMISOR = UtilTexto.VACIO;

    public static final String MOTIVO_DESCONOCIDO = "desconocido";
    public static final String MOTIVO_DESHABILITADO = "deshabilitado";
    public static final String MOTIVO_SIN_CREDENCIAL = "sin-credencial";
    public static final String MOTIVO_NO_APLICABLE = "no-aplicable";

    private SeleccionProveedor() {}

    public sealed interface Seleccion {

        record NoActua() implements Seleccion {}

        record Rechazada(String motivo) implements Seleccion {}

        record Elegida(ProveedorAutenticacion proveedor, String credencial, String emisor) implements Seleccion {}
    }

    public static Seleccion resolver(HttpServletRequest request, ProveedoresAutenticacion registro,
                                     boolean rutaNoDeNegocio) {
        var aplicables = registro.aplicables(rutaNoDeNegocio);
        var valor = UtilTexto.aplicarTrim(request.getHeader(CABECERA));
        if (UtilTexto.esVacioONulo(valor)) {
            return primeroConCredencial(request, aplicables);
        }
        var forzado = registro.porId(valor);
        if (forzado.isPresent()) {
            return elegir(request, aplicables, forzado.get(), SIN_EMISOR);
        }
        var porEmisor = registro.porSelectorDeEmisor(valor);
        if (porEmisor.isPresent()) {
            return elegir(request, aplicables, porEmisor.get().proveedor(), porEmisor.get().emisor());
        }
        return new Seleccion.Rechazada(
                registro.contiene(valor) || registro.declaraEmisor(valor) ? MOTIVO_DESHABILITADO : MOTIVO_DESCONOCIDO);
    }

    private static Seleccion elegir(HttpServletRequest request, List<ProveedorAutenticacion> aplicables,
                                    ProveedorAutenticacion proveedor, String emisor) {
        if (!aplicables.contains(proveedor)) {
            return new Seleccion.Rechazada(MOTIVO_NO_APLICABLE);
        }
        return proveedor.extraerCredencial(request)
                .<Seleccion>map(credencial -> new Seleccion.Elegida(proveedor, credencial, emisor))
                .orElseGet(() -> new Seleccion.Rechazada(MOTIVO_SIN_CREDENCIAL));
    }

    private static Seleccion primeroConCredencial(HttpServletRequest request,
                                                  List<ProveedorAutenticacion> aplicables) {
        for (var proveedor : aplicables) {
            var credencial = proveedor.extraerCredencial(request);
            if (credencial.isPresent()) {
                return new Seleccion.Elegida(proveedor, credencial.get(), SIN_EMISOR);
            }
        }
        return new Seleccion.NoActua();
    }
}
