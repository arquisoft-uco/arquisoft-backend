package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionFichaPerfilAsesorResponseMapperTest {

    @Test
    void debeMapearReadModelAResponseDTO_conTodosLosCamposYElRepresentante() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var representanteId = UtilUUID.generarNuevoUUID();
        var fecha = Instant.now();
        var readModel = new EvaluacionFichaPerfilAsesorReadModel(
                id, fichaPerfil, "Sistema de gestion", fecha, "APROBADA", "Aprobada",
                new RepresentanteComiteReadModel(representanteId, "María Gómez"));

        // Act
        var response = EvaluacionFichaPerfilAsesorResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(response.tituloProyecto()).isEqualTo("Sistema de gestion");
        assertThat(response.fechaCreacion()).isEqualTo(fecha);
        assertThat(response.estadoEvaluacion()).isEqualTo("APROBADA");
        assertThat(response.estadoEvaluacionNombre()).isEqualTo("Aprobada");
        assertThat(response.representanteComite().id()).isEqualTo(representanteId);
        assertThat(response.representanteComite().nombre()).isEqualTo("María Gómez");
    }
}
