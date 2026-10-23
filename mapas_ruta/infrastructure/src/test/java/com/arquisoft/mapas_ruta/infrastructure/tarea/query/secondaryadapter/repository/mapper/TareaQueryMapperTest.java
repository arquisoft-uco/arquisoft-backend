package com.arquisoft.mapas_ruta.infrastructure.tarea.query.secondaryadapter.repository.mapper;

import com.arquisoft.mapas_ruta.infrastructure.tarea.query.secondaryadapter.repository.TareaJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TareaQueryMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoSeConvierteLaEntidad() {
        // Arrange
        var entity = TareaJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .descripcion("Redactar")
                .mapaRutaId(UtilUUID.generarNuevoUUID())
                .fechaInicioMapaRuta(LocalDate.of(2026, 10, 1))
                .fechaFinMapaRuta(LocalDate.of(2026, 12, 1))
                .estadoItemId("NUEVA")
                .nombreEstadoItem("Nueva")
                .asesorId(UtilUUID.generarNuevoUUID())
                .nombreAsesor("Ana")
                .emailAsesor("ana@uco.edu.co")
                .build();

        // Act
        var readModel = TareaQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.descripcion()).isEqualTo("Redactar");
        assertThat(readModel.mapaRuta()).isEqualTo(entity.getMapaRutaId());
        assertThat(readModel.fechaInicioMapaRuta()).isEqualTo(LocalDate.of(2026, 10, 1));
        assertThat(readModel.fechaFinMapaRuta()).isEqualTo(LocalDate.of(2026, 12, 1));
        assertThat(readModel.estadoItem()).isEqualTo("NUEVA");
        assertThat(readModel.nombreEstadoItem()).isEqualTo("Nueva");
        assertThat(readModel.asesor()).isEqualTo(entity.getAsesorId());
        assertThat(readModel.nombreAsesor()).isEqualTo("Ana");
        assertThat(readModel.emailAsesor()).isEqualTo("ana@uco.edu.co");
    }
}
