package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.FichaPerfilJpaQueryEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FichaPerfilQueryMapperTest {

    @Test
    void debeMapearFichaAsesorYEstado_cuandoLaEntidadTraeTodosLosCampos() {
        // Arrange
        var idFicha = UUID.randomUUID();
        var idAsesor = UUID.randomUUID();
        var fechaActualizacion = Instant.parse("2026-09-30T10:15:30Z");
        var entity = FichaPerfilJpaQueryEntity.builder()
                .id(idFicha)
                .tituloProyecto("Arquisoft Backend")
                .asesorId(idAsesor)
                .asesorIdentificador("DOC-001")
                .asesorNombre("Juan Salazar")
                .asesorEmail("juan@uco.edu.co")
                .estadoId("APROBADA")
                .estadoNombre("Aprobada")
                .estadoFechaActualizacion(fechaActualizacion)
                .build();

        // Act
        var readModel = FichaPerfilQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(idFicha);
        assertThat(readModel.tituloProyecto()).isEqualTo("Arquisoft Backend");
        assertThat(readModel.asesorFicha().id()).isEqualTo(idAsesor);
        assertThat(readModel.asesorFicha().nombre()).isEqualTo("Juan Salazar");
        assertThat(readModel.estado().id()).isEqualTo("APROBADA");
        assertThat(readModel.estado().nombre()).isEqualTo("Aprobada");
        assertThat(readModel.estado().fechaActualizacion()).isEqualTo(fechaActualizacion);
    }
}
