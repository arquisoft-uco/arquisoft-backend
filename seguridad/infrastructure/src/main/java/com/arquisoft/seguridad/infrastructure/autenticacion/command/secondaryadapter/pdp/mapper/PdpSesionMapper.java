package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.pdp.mapper;

import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.ResolucionSesion;
import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.pdp.dto.SesionPdpResponseDTO;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

public final class PdpSesionMapper {

    public static final String CABECERA_REQUEST_ID = "X-Request-Id";

    private PdpSesionMapper() {}

    public static HttpHeaders toCabeceras(String credencial) {
        var cabeceras = new HttpHeaders();
        cabeceras.set(HttpHeaders.COOKIE, credencial);
        cabeceras.setAccept(List.of(MediaType.APPLICATION_JSON));
        cabeceras.set(CABECERA_REQUEST_ID, UtilUUID.generarNuevoUUID().toString());
        return cabeceras;
    }

    public static boolean tieneSujeto(SesionPdpResponseDTO dto) {
        return UtilObjeto.noEsNulo(dto) && UtilObjeto.noEsNulo(dto.data())
                && !UtilTexto.esVacioONulo(dto.data().subject());
    }

    public static ResolucionSesion.Resuelta toResolucion(SesionPdpResponseDTO dto) {
        var datos = dto.data();
        return new ResolucionSesion.Resuelta(
                UtilTexto.aplicarTrim(datos.subject()),
                UtilTexto.aplicarTrim(datos.name()),
                UtilTexto.aplicarTrim(datos.email()),
                UtilTexto.aplicarTrim(datos.tenantId()));
    }
}
