package com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.mapper;

import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.entity.ParametrosCatalogoEntity;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.dto.ParametrosCatalogoResponseDTO;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.webclient.MetodoHttp;
import com.arquisoft.shared.webclient.SolicitudHttp;
import com.arquisoft.shared.webclient.TipoContenido;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ParametrosCatalogoMapper {

    private static final String CABECERA_ACCEPT = "Accept";
    private static final String CABECERA_AUTORIZACION = "Authorization";
    private static final String TIPO_JSON = "application/json";
    private static final String ESQUEMA_BEARER = "Bearer ";
    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String GRANT_CLIENT_CREDENTIALS = "client_credentials";

    private ParametrosCatalogoMapper() {}

    public static SolicitudHttp toSolicitud(String ruta, String accessToken) {
        return new SolicitudHttp(MetodoHttp.GET, ruta,
                Map.of(CABECERA_ACCEPT, TIPO_JSON, CABECERA_AUTORIZACION, ESQUEMA_BEARER + accessToken), null, null);
    }

    public static SolicitudHttp toSolicitudToken(String ruta, String clientId, String clientSecret) {
        var formulario = new LinkedHashMap<String, String>();
        formulario.put(GRANT_TYPE, GRANT_CLIENT_CREDENTIALS);
        formulario.put(CLIENT_ID, clientId);
        formulario.put(CLIENT_SECRET, clientSecret);
        return new SolicitudHttp(MetodoHttp.POST, ruta, Map.of(CABECERA_ACCEPT, TIPO_JSON), formulario, TipoContenido.FORM);
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
