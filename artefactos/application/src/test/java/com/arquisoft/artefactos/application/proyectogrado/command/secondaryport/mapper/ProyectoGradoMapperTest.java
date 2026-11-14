package com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyecto;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoMapperTest {

    @Test
    void debeMapearEstadoYFechasEnAmbosSentidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregadoEn = Instant.parse("2026-11-01T10:00:00Z");
        var ocurridoEn = Instant.parse("2026-11-02T10:00:00Z");
        var domain = ProyectoGradoDomain.reconstruir(id, "Proyecto", entregadoEn, EstadoProyecto.FINALIZADO, ocurridoEn);

        // Act
        var entity = ProyectoGradoMapper.toEntity(domain);
        var reconstruido = ProyectoGradoMapper.toDomain(
                new ProyectoGradoEntity(id, "Proyecto", UtilFecha.VACIO, "CANCELADO", ocurridoEn));

        // Assert
        assertThat(entity).isEqualTo(new ProyectoGradoEntity(id, "Proyecto", entregadoEn, "FINALIZADO", ocurridoEn));
        assertThat(reconstruido.estaEntregado()).isFalse();
        assertThat(reconstruido.getEstadoProyecto()).isEqualTo(EstadoProyecto.CANCELADO);
        assertThat(reconstruido.getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
