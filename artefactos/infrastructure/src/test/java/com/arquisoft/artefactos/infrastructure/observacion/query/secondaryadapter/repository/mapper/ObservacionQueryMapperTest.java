package com.arquisoft.artefactos.infrastructure.observacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.infrastructure.observacion.query.secondaryadapter.repository.ObservacionEstudianteJpaQueryEntity;
import com.arquisoft.artefactos.infrastructure.observacion.query.secondaryadapter.repository.ObservacionJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionQueryMapperTest {

    @Test
    void debeCopiarLosCincoCampos_cuandoMapeaLaVistaDelAsesor() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var revision = UtilUUID.generarNuevoUUID();
        var entity = ObservacionJpaQueryEntity.builder()
                .id(id).revisionAsesor(revision).asesor(UtilUUID.generarNuevoUUID())
                .descripcion("Obs").estado("NUEVA").estadoNombre("Nueva").build();

        // Act
        var readModel = ObservacionQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(id);
        assertThat(readModel.revisionAsesor()).isEqualTo(revision);
        assertThat(readModel.descripcion()).isEqualTo("Obs");
        assertThat(readModel.estado()).isEqualTo("NUEVA");
        assertThat(readModel.estadoNombre()).isEqualTo("Nueva");
    }

    @Test
    void debeCopiarLosCincoCampos_cuandoMapeaLaVistaDelEstudiante() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var revision = UtilUUID.generarNuevoUUID();
        var entity = ObservacionEstudianteJpaQueryEntity.builder()
                .id(id).revisionAsesor(revision).estudiante(UtilUUID.generarNuevoUUID())
                .descripcion("Obs").estado("CERRADA").estadoNombre("Cerrada").build();

        // Act
        var readModel = ObservacionQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(id);
        assertThat(readModel.revisionAsesor()).isEqualTo(revision);
        assertThat(readModel.descripcion()).isEqualTo("Obs");
        assertThat(readModel.estado()).isEqualTo("CERRADA");
        assertThat(readModel.estadoNombre()).isEqualTo("Cerrada");
    }
}
