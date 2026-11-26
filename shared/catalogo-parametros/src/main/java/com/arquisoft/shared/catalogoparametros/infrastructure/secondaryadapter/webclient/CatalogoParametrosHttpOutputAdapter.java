package com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient;

import com.arquisoft.shared.catalogoparametros.CatalogoParametrosEncendido;
import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.FuenteParametrosCatalogoOutputPort;
import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.model.ObtencionParametrosCatalogo;
import com.arquisoft.shared.catalogoparametros.infrastructure.config.CatalogoParametrosClienteHttpConfig;
import com.arquisoft.shared.catalogoparametros.infrastructure.config.CatalogoParametrosProperties;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.dto.ParametrosCatalogoResponseDTO;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.mapper.ParametrosCatalogoMapper;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.webclient.ClienteHttp;
import com.arquisoft.shared.webclient.RespuestaHttp;
import com.arquisoft.shared.webclient.exception.ClienteHttpTransporteException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@CatalogoParametrosEncendido
public class CatalogoParametrosHttpOutputAdapter implements FuenteParametrosCatalogoOutputPort {

    private static final int ESTADO_NO_AUTORIZADO = 401;
    private static final int ESTADO_PROHIBIDO = 403;
    private static final String MOTIVO_RESPUESTA_INVALIDA = "respuesta-invalida";
    private static final String MOTIVO_ESTADO_PREFIJO = "estado-";
    private static final String RUTA_TOKENS = "/tokens/";
    private static final String RUTA_PARAMETROS = "/parametros";

    private final ClienteHttp clienteHttp;
    private final CatalogoParametrosProperties propiedades;

    public CatalogoParametrosHttpOutputAdapter(
            @Qualifier(CatalogoParametrosClienteHttpConfig.CLIENTE_CATALOGO_PARAMETROS) ClienteHttp clienteHttp,
            CatalogoParametrosProperties propiedades) {
        this.clienteHttp = clienteHttp;
        this.propiedades = propiedades;
    }

    @Override
    public ObtencionParametrosCatalogo obtener() {
        if (!propiedades.configurado()) {
            return new ObtencionParametrosCatalogo.SinConfigurar();
        }
        var solicitud = ParametrosCatalogoMapper.toSolicitud(
                RUTA_TOKENS + propiedades.tokenId() + RUTA_PARAMETROS, propiedades.secreto());
        try {
            return traducir(clienteHttp.ejecutar(solicitud, ParametrosCatalogoResponseDTO.class));
        } catch (ClienteHttpTransporteException e) {
            return new ObtencionParametrosCatalogo.NoDisponible(e.getClass().getSimpleName());
        }
    }

    private ObtencionParametrosCatalogo traducir(RespuestaHttp<ParametrosCatalogoResponseDTO> respuesta) {
        if (respuesta.estado() == ESTADO_NO_AUTORIZADO || respuesta.estado() == ESTADO_PROHIBIDO) {
            return new ObtencionParametrosCatalogo.Rechazada(respuesta.estado());
        }
        if (!respuesta.esExitosa()) {
            return new ObtencionParametrosCatalogo.NoDisponible(MOTIVO_ESTADO_PREFIJO + respuesta.estado());
        }
        var cuerpo = respuesta.cuerpo();
        if (UtilObjeto.esNulo(cuerpo) || UtilObjeto.esNulo(cuerpo.parametros())) {
            return new ObtencionParametrosCatalogo.NoDisponible(MOTIVO_RESPUESTA_INVALIDA);
        }
        return new ObtencionParametrosCatalogo.Obtenida(ParametrosCatalogoMapper.toEntity(cuerpo));
    }
}
