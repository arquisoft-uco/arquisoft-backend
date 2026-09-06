package com.arquisoft.solicitudes.infrastructure.solicitud.command.secondaryadapter.mapper;

import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.entity.DestinatarioEntity;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.entity.RemitenteEntity;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity.SolicitudEntity;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.entity.DestinatarioJpaEntity;
import com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.mapper.DestinatarioJpaMapper;
import com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.entity.RemitenteJpaEntity;
import com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.mapper.RemitenteJpaMapper;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.secondaryadapter.entity.SolicitudJpaEntity;
import com.arquisoft.solicitudes.infrastructure.tiposolicitud.command.secondaryadapter.entity.TipoSolicitudJpaEntity;
import com.arquisoft.solicitudes.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import com.arquisoft.solicitudes.infrastructure.usuario.command.secondaryadapter.mapper.UsuarioJpaMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JpaMappersConversionTest {

    @Test
    void debeAplanarLaSolicitud_cuandoConvierteDesdeJpaEntity() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID destinatarioId = UUID.randomUUID();
        UUID remitenteId = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.now();
        SolicitudJpaEntity jpa = SolicitudJpaEntity.builder()
                .id(id)
                .destinatario(DestinatarioJpaEntity.builder().id(destinatarioId).build())
                .remitente(RemitenteJpaEntity.builder().id(remitenteId).build())
                .fechaCreacion(fecha)
                .mensajeSolicitud("mensaje")
                .tipoSolicitud(TipoSolicitudJpaEntity.builder().id("NOVEDAD_PARA_EL_COORDINADOR").build())
                .build();

        // Act
        SolicitudEntity entity = SolicitudJpaMapper.toEntity(jpa);

        // Assert
        assertThat(entity).isEqualTo(new SolicitudEntity(
                id, destinatarioId, remitenteId, fecha, "mensaje", "NOVEDAD_PARA_EL_COORDINADOR"));
    }

    @Test
    void debeConvertirRemitenteYDestinatario_cuandoConvierteDesdeJpaEntity() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID usuario = UUID.randomUUID();

        // Act & Assert
        assertThat(RemitenteJpaMapper.toEntity(
                RemitenteJpaEntity.builder().id(id).usuarioId(usuario).build()))
                .isEqualTo(new RemitenteEntity(id, usuario));
        assertThat(DestinatarioJpaMapper.toEntity(
                DestinatarioJpaEntity.builder().id(id).usuarioId(usuario).build()))
                .isEqualTo(new DestinatarioEntity(id, usuario));
    }

    @Test
    void debeConvertirElUsuario_cuandoConvierteDesdeJpaEntity() {
        // Arrange
        UUID id = UUID.randomUUID();
        UsuarioJpaEntity jpa = UsuarioJpaEntity.builder()
                .id(id).identificador("EST-1").nombre("Ana").email("ana@uco.edu.co").build();

        // Act & Assert
        assertThat(UsuarioJpaMapper.toEntity(jpa))
                .isEqualTo(new UsuarioEntity(id, "EST-1", "Ana", "ana@uco.edu.co"));
    }
}
