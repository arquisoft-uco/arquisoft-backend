package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient;

import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient.dto.UsuarioPorCorreoResponseDTO;
import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient.mapper.UsuarioPorCorreoMapper;
import com.arquisoft.seguridad.infrastructure.autenticacion.exception.UsuariosNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.config.http.UsuariosClienteHttpConfig;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.ResolucionSesionKey;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.webclient.ClienteHttp;
import com.arquisoft.shared.webclient.RespuestaHttp;
import com.arquisoft.shared.webclient.exception.ClienteHttpTransporteException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UsuarioPorCorreoWebClientOutputAdapter implements UsuarioPorCorreoWebClientOutputPort {

    private static final int ESTADO_OK = 200;
    private static final int ESTADO_UNPROCESSABLE = 422;
    private static final int ESTADO_UNAUTHORIZED = 401;
    private static final String MOTIVO_RESPUESTA_INVALIDA = "respuesta-invalida";
    private static final String MOTIVO_ESTADO_INESPERADO = "estado-inesperado";

    private final ClienteHttp clienteUsuarios;
    private final CredencialServicioUsuarios credencial;
    private final AppLogger logger;

    public UsuarioPorCorreoWebClientOutputAdapter(
            @Qualifier(UsuariosClienteHttpConfig.CLIENTE_USUARIOS) ClienteHttp clienteUsuarios,
            CredencialServicioUsuarios credencial,
            AppLogger logger) {
        this.clienteUsuarios = clienteUsuarios;
        this.credencial = credencial;
        this.logger = logger;
    }

    @Override
    public Optional<UUID> obtenerIdPorCorreo(String correo) {
        if (UtilTexto.esVacioONulo(correo)) {
            return Optional.empty();
        }
        var respuesta = enviar(correo);
        if (respuesta.estado() == ESTADO_UNAUTHORIZED) {
            credencial.invalidar();
            respuesta = enviar(correo);
        }
        return switch (respuesta.estado()) {
            case ESTADO_OK -> extraerId(respuesta);
            case ESTADO_UNPROCESSABLE -> Optional.empty();
            default -> throw noDisponible(MOTIVO_ESTADO_INESPERADO, respuesta.estado(), null);
        };
    }

    private RespuestaHttp<UsuarioPorCorreoResponseDTO> enviar(String correo) {
        var solicitud = UsuarioPorCorreoMapper.toSolicitud(credencial.token(), correo);
        try {
            return clienteUsuarios.ejecutar(solicitud, UsuarioPorCorreoResponseDTO.class);
        } catch (ClienteHttpTransporteException e) {
            throw noDisponible(e.getClass().getSimpleName(), 0, e);
        }
    }

    private Optional<UUID> extraerId(RespuestaHttp<UsuarioPorCorreoResponseDTO> respuesta) {
        var id = UsuarioPorCorreoMapper.toId(respuesta.cuerpo());
        if (id.isEmpty()) {
            throw noDisponible(MOTIVO_RESPUESTA_INVALIDA, respuesta.estado(), null);
        }
        return id;
    }

    private UsuariosNoDisponibleException noDisponible(String motivo, int estado, Exception causa) {
        logger.error(ResolucionSesionKey.LOG_USUARIOS_NO_DISPONIBLE, motivo, estado);
        return new UsuariosNoDisponibleException(Mensajes.obtener(ResolucionSesionKey.ERROR_USUARIOS_NO_DISPONIBLE), causa);
    }
}
