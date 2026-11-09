package com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoMapperTest {

    @Test
    void debeMapearFechasEnAmbosSentidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregadoEn = Instant.parse("2026-11-01T10:00:00Z");
        var finalizadoEn = Instant.parse("2026-11-02T10:00:00Z");
        var domain = ProyectoGradoDomain.reconstruir(id, "Proyecto", entregadoEn, finalizadoEn);

        // Act
        var entity = ProyectoGradoMapper.toEntity(domain);
        var reconstruido = ProyectoGradoMapper.toDomain(new ProyectoGradoEntity(id, "Proyecto", UtilFecha.VACIO, finalizadoEn));

        // Assert
        assertThat(entity).isEqualTo(new ProyectoGradoEntity(id, "Proyecto", entregadoEn, finalizadoEn));
        assertThat(reconstruido.estaEntregado()).isFalse();
        assertThat(reconstruido.getFinalizadoEn()).isEqualTo(finalizadoEn);
    }
}
