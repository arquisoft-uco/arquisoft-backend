package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.EvaluacionFichaPerfilAsesorJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionFichaPerfilAsesorQueryMapperTest {

    @Test
    void debeMapearEntityAReadModel_conTodosLosCamposYElRepresentanteAnidado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var fichaPerfilId = UtilUUID.generarNuevoUUID();
        var representanteId = UtilUUID.generarNuevoUUID();
        var fecha = Instant.now();
        var entity = EvaluacionFichaPerfilAsesorJpaQueryEntity.builder()
                .id(id)
                .fichaPerfilId(fichaPerfilId)
                .tituloProyecto("Sistema de gestion")
                .asesorFichaId(UtilUUID.generarNuevoUUID())
                .fechaCreacion(fecha)
                .estadoEvaluacionId("APROBADA")
                .estadoEvaluacionNombre("Aprobada")
                .representanteComiteId(representanteId)
                .representanteComiteNombre("María Gómez")
                .build();

        // Act
        var readModel = EvaluacionFichaPerfilAsesorQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(id);
        assertThat(readModel.fichaPerfil()).isEqualTo(fichaPerfilId);
        assertThat(readModel.tituloProyecto()).isEqualTo("Sistema de gestion");
        assertThat(readModel.fechaCreacion()).isEqualTo(fecha);
        assertThat(readModel.estadoEvaluacion()).isEqualTo("APROBADA");
        assertThat(readModel.estadoEvaluacionNombre()).isEqualTo("Aprobada");
        assertThat(readModel.representanteComite().id()).isEqualTo(representanteId);
        assertThat(readModel.representanteComite().nombre()).isEqualTo("María Gómez");
    }
}
