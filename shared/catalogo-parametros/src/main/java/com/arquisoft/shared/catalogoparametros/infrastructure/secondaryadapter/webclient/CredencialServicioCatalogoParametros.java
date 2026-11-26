package com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient;

import com.arquisoft.shared.catalogoparametros.CatalogoParametrosEncendido;
import com.arquisoft.shared.catalogoparametros.infrastructure.config.CatalogoParametrosClienteHttpConfig;
import com.arquisoft.shared.catalogoparametros.infrastructure.config.CatalogoParametrosProperties;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.dto.TokenServicioCatalogoResponseDTO;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.mapper.ParametrosCatalogoMapper;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.model.ObtencionTokenServicio;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.webclient.ClienteHttp;
import com.arquisoft.shared.webclient.exception.ClienteHttpTransporteException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

// Token client_credentials de la identidad de servicio con la que arquisoft consulta el Catalogo.
// Se reutiliza hasta MARGEN_EXPIRACION antes de vencer; un 401 del Catalogo lo invalida.
@Component
@CatalogoParametrosEncendido
public class CredencialServicioCatalogoParametros {

    private static final Duration MARGEN_EXPIRACION = Duration.ofSeconds(30);
    private static final int ESTADO_SOLICITUD_INVALIDA = 400;
    private static final int ESTADO_NO_AUTORIZADO = 401;
    private static final String MOTIVO_TOKEN_INVALIDO = "token-servicio-invalido";
    private static final String MOTIVO_ESTADO_TOKEN_PREFIJO = "token-estado-";

    private final ClienteHttp clienteToken;
    private final CatalogoParametrosProperties propiedades;
    private final ReentrantLock cerrojo = new ReentrantLock();

    private String token;
    private Instant vigenteHasta = Instant.MIN;

    public CredencialServicioCatalogoParametros(
            @Qualifier(CatalogoParametrosClienteHttpConfig.CLIENTE_TOKEN_CATALOGO_PARAMETROS) ClienteHttp clienteToken,
            CatalogoParametrosProperties propiedades) {
        this.clienteToken = clienteToken;
        this.propiedades = propiedades;
    }

    public ObtencionTokenServicio obtener() {
        cerrojo.lock();
        try {
            if (UtilObjeto.noEsNulo(token) && UtilFecha.generarInstanteActual().isBefore(vigenteHasta)) {
                return new ObtencionTokenServicio.Vigente(token);
            }
            return renovar();
        } finally {
            cerrojo.unlock();
        }
    }

    public void invalidar() {
        cerrojo.lock();
        try {
            token = null;
            vigenteHasta = Instant.MIN;
        } finally {
            cerrojo.unlock();
        }
    }

    private ObtencionTokenServicio renovar() {
        token = null;
        vigenteHasta = Instant.MIN;
        var solicitud = ParametrosCatalogoMapper.toSolicitudToken(
                propiedades.rutaTokenUri(), propiedades.clientId(), propiedades.clientSecret());
        try {
            var respuesta = clienteToken.ejecutar(solicitud, TokenServicioCatalogoResponseDTO.class);
            if (respuesta.estado() == ESTADO_SOLICITUD_INVALIDA || respuesta.estado() == ESTADO_NO_AUTORIZADO) {
                return new ObtencionTokenServicio.Rechazada(respuesta.estado());
            }
            if (!respuesta.esExitosa()) {
                return new ObtencionTokenServicio.NoDisponible(MOTIVO_ESTADO_TOKEN_PREFIJO + respuesta.estado());
            }
            var cuerpo = respuesta.cuerpo();
            if (UtilObjeto.esNulo(cuerpo) || UtilTexto.esVacioONulo(cuerpo.accessToken())) {
                return new ObtencionTokenServicio.NoDisponible(MOTIVO_TOKEN_INVALIDO);
            }
            token = cuerpo.accessToken();
            vigenteHasta = UtilFecha.generarInstanteActual().plusSeconds(cuerpo.expiresIn()).minus(MARGEN_EXPIRACION);
            return new ObtencionTokenServicio.Vigente(token);
        } catch (ClienteHttpTransporteException e) {
            return new ObtencionTokenServicio.NoDisponible(e.getClass().getSimpleName());
        }
    }
}
