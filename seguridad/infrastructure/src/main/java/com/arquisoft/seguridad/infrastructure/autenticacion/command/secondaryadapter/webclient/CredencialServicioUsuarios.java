package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient;

import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient.dto.TokenServicioResponseDTO;
import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient.mapper.TokenServicioMapper;
import com.arquisoft.seguridad.infrastructure.autenticacion.exception.UsuariosNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.UsuarioSesionProperties;
import com.arquisoft.seguridad.infrastructure.config.http.UsuariosClienteHttpConfig;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.ResolucionSesionKey;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.webclient.ClienteHttp;
import com.arquisoft.shared.webclient.exception.ClienteHttpTransporteException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class CredencialServicioUsuarios {

    private static final Duration MARGEN_EXPIRACION = Duration.ofSeconds(30);
    private static final String MOTIVO_RESPUESTA_INVALIDA = "token-servicio-invalido";

    private final ClienteHttp clienteToken;
    private final UsuarioSesionProperties propiedades;
    private final AppLogger logger;

    private String token;
    private Instant vigenteHasta = Instant.MIN;

    public CredencialServicioUsuarios(
            @Qualifier(UsuariosClienteHttpConfig.CLIENTE_TOKEN) ClienteHttp clienteToken,
            UsuarioSesionProperties propiedades,
            AppLogger logger) {
        this.clienteToken = clienteToken;
        this.propiedades = propiedades;
        this.logger = logger;
    }

    public synchronized String token() {
        if (UtilObjeto.esNulo(token) || !Instant.now().isBefore(vigenteHasta)) {
            renovar();
        }
        return token;
    }

    public synchronized void invalidar() {
        token = null;
        vigenteHasta = Instant.MIN;
    }

    private void renovar() {
        var solicitud = TokenServicioMapper.toSolicitud(
                UsuariosClienteHttpConfig.ruta(propiedades.tokenUrl()), propiedades.clientId(), propiedades.clientSecret());
        try {
            var respuesta = clienteToken.ejecutar(solicitud, TokenServicioResponseDTO.class);
            if (!respuesta.esExitosa() || UtilObjeto.esNulo(respuesta.cuerpo())
                    || UtilTexto.esVacioONulo(respuesta.cuerpo().accessToken())) {
                throw noDisponible(MOTIVO_RESPUESTA_INVALIDA, respuesta.estado(), null);
            }
            token = respuesta.cuerpo().accessToken();
            vigenteHasta = Instant.now().plusSeconds(respuesta.cuerpo().expiresIn()).minus(MARGEN_EXPIRACION);
        } catch (ClienteHttpTransporteException e) {
            throw noDisponible(e.getClass().getSimpleName(), 0, e);
        }
    }

    private UsuariosNoDisponibleException noDisponible(String motivo, int estado, Exception causa) {
        invalidar();
        logger.error(ResolucionSesionKey.LOG_USUARIOS_NO_DISPONIBLE, motivo, estado);
        return new UsuariosNoDisponibleException(Mensajes.obtener(ResolucionSesionKey.ERROR_USUARIOS_NO_DISPONIBLE), causa);
    }
}
