package com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.mapper;

import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.entity.ParametrosCatalogoEntity;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.dto.ParametrosCatalogoResponseDTO;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.webclient.MetodoHttp;
import com.arquisoft.shared.webclient.SolicitudHttp;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ParametrosCatalogoMapper {

    private static final String CABECERA_ACCEPT = "Accept";
    private static final String CABECERA_AUTORIZACION = "Authorization";
    private static final String TIPO_JSON = "application/json";
    private static final String ESQUEMA_BEARER = "Bearer ";

    private ParametrosCatalogoMapper() {}

    public static SolicitudHttp toSolicitud(String ruta, String secreto) {
        return new SolicitudHttp(MetodoHttp.GET, ruta,
                Map.of(CABECERA_ACCEPT, TIPO_JSON, CABECERA_AUTORIZACION, ESQUEMA_BEARER + secreto), null, null);
    }

    public static ParametrosCatalogoEntity toEntity(ParametrosCatalogoResponseDTO dto) {
        var valores = new LinkedHashMap<String, String>();
        for (var item : dto.parametros()) {
            if (UtilObjeto.esNulo(item) || UtilObjeto.esNulo(item.parametro())) {
                continue;
            }
            var nombre = UtilTexto.aplicarTrim(item.parametro().nombre());
            if (UtilTexto.esVacioONulo(nombre) || !Boolean.TRUE.equals(item.parametro().activo())) {
                continue;
            }
            var valor = primerValor(item.metadatos());
            if (!UtilTexto.esVacioONulo(valor)) {
                valores.putIfAbsent(nombre, valor);
            }
        }
        return new ParametrosCatalogoEntity(valores);
    }

    private static String primerValor(List<ParametrosCatalogoResponseDTO.MetadatoItem> metadatos) {
        if (UtilObjeto.esNulo(metadatos)) {
            return UtilTexto.VACIO;
        }
        for (var item : metadatos) {
            if (UtilObjeto.esNulo(item) || UtilObjeto.esNulo(item.metadato())) {
                continue;
            }
            var texto = comoTexto(item.metadato().valor());
            if (!UtilTexto.esVacioONulo(texto)) {
                return texto;
            }
        }
        return UtilTexto.VACIO;
    }

    private static String comoTexto(Object valor) {
        if (valor instanceof String texto) {
            return UtilTexto.aplicarTrim(texto);
        }
        if (valor instanceof Number || valor instanceof Boolean) {
            return valor.toString();
        }
        return UtilTexto.VACIO;
    }
}
