package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient.mapper;

import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient.dto.UsuarioPorCorreoRequestDTO;
import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient.dto.UsuarioPorCorreoResponseDTO;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SeleccionProveedor;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.webclient.MetodoHttp;
import com.arquisoft.shared.webclient.SolicitudHttp;
import com.arquisoft.shared.webclient.TipoContenido;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class UsuarioPorCorreoMapper {

    private static final String ESQUEMA_BEARER = "Bearer ";

    private UsuarioPorCorreoMapper() {}

    public static SolicitudHttp toSolicitud(String ruta, String token, String correo, String proveedor) {
        return new SolicitudHttp(MetodoHttp.POST, ruta,
                Map.of(HttpHeaders.AUTHORIZATION, ESQUEMA_BEARER + token,
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE,
                        SeleccionProveedor.CABECERA, proveedor),
                new UsuarioPorCorreoRequestDTO(correo), TipoContenido.JSON);
    }

    public static Optional<UUID> toId(UsuarioPorCorreoResponseDTO respuesta) {
        if (UtilObjeto.esNulo(respuesta)) {
            return Optional.empty();
        }
        return Optional.ofNullable(UtilUUID.generarUUIDDesdeTexto(respuesta.id()));
    }
}
