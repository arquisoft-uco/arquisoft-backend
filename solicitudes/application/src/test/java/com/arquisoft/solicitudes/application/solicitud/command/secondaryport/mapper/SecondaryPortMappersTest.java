package com.arquisoft.solicitudes.application.solicitud.command.secondaryport.mapper;

import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.entity.DestinatarioEntity;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.mapper.DestinatarioMapper;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.entity.RemitenteEntity;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.mapper.RemitenteMapper;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.mapper.UsuarioMapper;
import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;
import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;
import com.arquisoft.solicitudes.domain.solicitud.SolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SecondaryPortMappersTest {

    @Test
    void debeConvertirLaSolicitudUsandoLosIdsDeFilaDeRemitenteYDestinatario() {
        // Arrange
        var id = UUID.randomUUID();
        var destinatarioUsuario = UUID.randomUUID();
        var remitenteUsuario = UUID.randomUUID();
        var destinatarioFila = UUID.randomUUID();
        var remitenteFila = UUID.randomUUID();
        var fecha = Instant.now();
        var domain = SolicitudDomain.reconstruir(id, destinatarioUsuario, remitenteUsuario, fecha,
                "mensaje", TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);

        // Act
        var entity = SolicitudMapper.toEntity(domain, remitenteFila, destinatarioFila);

        // Assert
        assertThat(entity.id()).isEqualTo(id);
        assertThat(entity.tipoSolicitud()).isEqualTo("NOVEDAD_PARA_EL_COORDINADOR");
        assertThat(entity.destinatario()).isEqualTo(destinatarioFila);
        assertThat(entity.remitente()).isEqualTo(remitenteFila);
        assertThat(entity.fechaCreacion()).isEqualTo(fecha);
        assertThat(entity.mensajeSolicitud()).isEqualTo("mensaje");
    }

    @Test
    void debeConvertirRemitenteYDestinatarioEnAmbasDirecciones() {
        // Arrange
        var id = UUID.randomUUID();
        var usuario = UUID.randomUUID();

        // Act & Assert — remitente
        var remitenteEntity = RemitenteMapper.toEntity(RemitenteDomain.reconstruir(id, usuario));
        assertThat(remitenteEntity).isEqualTo(new RemitenteEntity(id, usuario));
        var remitenteDomain = RemitenteMapper.toDomain(remitenteEntity);
        assertThat(remitenteDomain.getId()).isEqualTo(id);
        assertThat(remitenteDomain.getUsuario()).isEqualTo(usuario);

        // Act & Assert — destinatario
        var destinatarioEntity =
                DestinatarioMapper.toEntity(DestinatarioDomain.reconstruir(id, usuario));
        assertThat(destinatarioEntity).isEqualTo(new DestinatarioEntity(id, usuario));
        var destinatarioDomain = DestinatarioMapper.toDomain(destinatarioEntity);
        assertThat(destinatarioDomain.getId()).isEqualTo(id);
        assertThat(destinatarioDomain.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void debeConvertirElUsuarioEnAmbasDirecciones() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var domain = UsuarioDomain.reconstruir(id, "EST-1", "Ana", "ana@uco.edu.co", ocurridoEn);

        // Act
        var entity = UsuarioMapper.toEntity(domain);
        var vuelta = UsuarioMapper.toDomain(entity);

        // Assert
        assertThat(entity).isEqualTo(new UsuarioEntity(id, "EST-1", "Ana", "ana@uco.edu.co", ocurridoEn));
        assertThat(vuelta.getId()).isEqualTo(id);
        assertThat(vuelta.getIdentificador()).isEqualTo("EST-1");
        assertThat(vuelta.getEmail()).isEqualTo("ana@uco.edu.co");
    }
}
