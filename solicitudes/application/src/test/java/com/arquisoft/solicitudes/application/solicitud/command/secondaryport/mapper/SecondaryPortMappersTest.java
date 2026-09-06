package com.arquisoft.solicitudes.application.solicitud.command.secondaryport.mapper;

import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.entity.DestinatarioEntity;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.mapper.DestinatarioMapper;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.entity.RemitenteEntity;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.mapper.RemitenteMapper;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity.SolicitudEntity;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.mapper.UsuarioMapper;
import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;
import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;
import com.arquisoft.solicitudes.domain.solicitud.SolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SecondaryPortMappersTest {

    @Test
    void debeConvertirLaSolicitudEnAmbasDirecciones() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID destinatario = UUID.randomUUID();
        UUID remitente = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.now();
        SolicitudDomain domain = SolicitudDomain.reconstruir(
                id, destinatario, remitente, fecha, "mensaje", TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);

        // Act
        SolicitudEntity entity = SolicitudMapper.toEntity(domain);
        SolicitudDomain vuelta = SolicitudMapper.toDomain(entity);

        // Assert
        assertThat(entity.tipoSolicitud()).isEqualTo("NOVEDAD_PARA_EL_COORDINADOR");
        assertThat(entity.destinatario()).isEqualTo(destinatario);
        assertThat(vuelta.getTipoSolicitud()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
        assertThat(vuelta.getId()).isEqualTo(id);
        assertThat(vuelta.getMensajeSolicitud()).isEqualTo("mensaje");
    }

    @Test
    void debeConvertirRemitenteYDestinatarioEnAmbasDirecciones() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID usuario = UUID.randomUUID();

        // Act & Assert — remitente
        RemitenteEntity remitenteEntity = RemitenteMapper.toEntity(RemitenteDomain.reconstruir(id, usuario));
        assertThat(remitenteEntity).isEqualTo(new RemitenteEntity(id, usuario));
        RemitenteDomain remitenteDomain = RemitenteMapper.toDomain(remitenteEntity);
        assertThat(remitenteDomain.getId()).isEqualTo(id);
        assertThat(remitenteDomain.getUsuario()).isEqualTo(usuario);

        // Act & Assert — destinatario
        DestinatarioEntity destinatarioEntity =
                DestinatarioMapper.toEntity(DestinatarioDomain.reconstruir(id, usuario));
        assertThat(destinatarioEntity).isEqualTo(new DestinatarioEntity(id, usuario));
        DestinatarioDomain destinatarioDomain = DestinatarioMapper.toDomain(destinatarioEntity);
        assertThat(destinatarioDomain.getId()).isEqualTo(id);
        assertThat(destinatarioDomain.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void debeConvertirElUsuarioEnAmbasDirecciones() {
        // Arrange
        UUID id = UUID.randomUUID();
        UsuarioDomain domain = UsuarioDomain.reconstruir(id, "EST-1", "Ana", "ana@uco.edu.co");

        // Act
        UsuarioEntity entity = UsuarioMapper.toEntity(domain);
        UsuarioDomain vuelta = UsuarioMapper.toDomain(entity);

        // Assert
        assertThat(entity).isEqualTo(new UsuarioEntity(id, "EST-1", "Ana", "ana@uco.edu.co"));
        assertThat(vuelta.getId()).isEqualTo(id);
        assertThat(vuelta.getIdentificador()).isEqualTo("EST-1");
        assertThat(vuelta.getEmail()).isEqualTo("ana@uco.edu.co");
    }
}
