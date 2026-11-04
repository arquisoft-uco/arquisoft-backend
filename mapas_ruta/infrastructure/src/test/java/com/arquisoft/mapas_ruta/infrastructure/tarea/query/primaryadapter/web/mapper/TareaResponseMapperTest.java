package com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TareaResponseMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoSeConvierteElReadModel() {
        // Arrange
        var readModel = new TareaReadModel(UtilUUID.generarNuevoUUID(), "Redactar", UtilUUID.generarNuevoUUID(),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1), "EN_PROGRESO", "En progreso",
                UtilUUID.generarNuevoUUID(), "Ana", "ana@uco.edu.co");

        // Act
        var dto = TareaResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto).usingRecursiveComparison().isEqualTo(readModel);
    }
}
