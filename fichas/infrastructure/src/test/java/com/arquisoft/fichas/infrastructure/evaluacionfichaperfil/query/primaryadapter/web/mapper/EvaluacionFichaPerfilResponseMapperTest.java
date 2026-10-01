package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilReadModel;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionFichaPerfilResponseMapperTest {

    @Test
    void debeMapearReadModelAResponseDTO_conLosCincoCampos() {
        // Arrange
        var readModel = new EvaluacionFichaPerfilReadModel(
                UUID.randomUUID(), UUID.randomUUID(), Instant.now(), "APROBADA", "Aprobada");

        // Act
        var dto = EvaluacionFichaPerfilResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.fichaPerfilId()).isEqualTo(readModel.fichaPerfilId());
        assertThat(dto.fechaCreacion()).isEqualTo(readModel.fechaCreacion());
        assertThat(dto.estadoEvaluacion()).isEqualTo("APROBADA");
        assertThat(dto.estadoEvaluacionNombre()).isEqualTo("Aprobada");
    }

    @Test
    void debeMapearEstadoNulo_cuandoLaEvaluacionNoTieneTrazabilidad() {
        // Arrange
        var readModel = new EvaluacionFichaPerfilReadModel(
                UUID.randomUUID(), UUID.randomUUID(), Instant.now(), null, null);

        // Act
        var dto = EvaluacionFichaPerfilResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.estadoEvaluacion()).isNull();
        assertThat(dto.estadoEvaluacionNombre()).isNull();
    }
}
