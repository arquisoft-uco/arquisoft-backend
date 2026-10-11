package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionFichaPerfilEstudianteResponseMapperTest {

    @Test
    void debeMapearReadModelAResponseDTO_conRepresentanteAnidado() {
        // Arrange
        var representante = new RepresentanteComiteReadModel(UtilUUID.generarNuevoUUID(), "María Gómez");
        var readModel = new EvaluacionFichaPerfilEstudianteReadModel(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), Instant.now(),
                "APROBADA", "Aprobada", representante);

        // Act
        var dto = EvaluacionFichaPerfilEstudianteResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.fichaPerfil()).isEqualTo(readModel.fichaPerfil());
        assertThat(dto.fechaCreacion()).isEqualTo(readModel.fechaCreacion());
        assertThat(dto.estadoEvaluacion()).isEqualTo("APROBADA");
        assertThat(dto.estadoEvaluacionNombre()).isEqualTo("Aprobada");
        assertThat(dto.representanteComite().id()).isEqualTo(representante.id());
        assertThat(dto.representanteComite().nombre()).isEqualTo("María Gómez");
    }
}
