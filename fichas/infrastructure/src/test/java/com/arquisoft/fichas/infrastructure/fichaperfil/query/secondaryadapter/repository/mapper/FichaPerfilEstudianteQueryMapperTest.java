package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.FichaPerfilEstudianteJpaQueryEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FichaPerfilEstudianteQueryMapperTest {

    @Test
    void debeMapearEntityYEstudiantesAReadModel_conTodosLosCampos() {
        // Arrange
        var fichaId = UUID.randomUUID();
        var asesorId = UUID.randomUUID();
        var fechaEstado = Instant.now();
        var entity = FichaPerfilEstudianteJpaQueryEntity.builder()
                .id(fichaId)
                .tituloProyecto("Sistema de gestion")
                .asesorId(asesorId)
                .asesorIdentificador("A123")
                .asesorNombre("Ana Asesora")
                .asesorEmail("ana@uco.edu.co")
                .estadoId("FORMULACION")
                .estadoNombre("Formulacion")
                .estadoFechaActualizacion(fechaEstado)
                .build();
        var estudiantes = List.of(new EstudianteFichaPerfilReadModel(
                UUID.randomUUID(), fichaId, UUID.randomUUID(), "Estudiante Uno", "e1@uco.edu.co"));

        // Act
        var readModel = FichaPerfilEstudianteQueryMapper.toReadModel(entity, estudiantes);

        // Assert
        assertThat(readModel.id()).isEqualTo(fichaId);
        assertThat(readModel.tituloProyecto()).isEqualTo("Sistema de gestion");
        assertThat(readModel.asesorFicha().id()).isEqualTo(asesorId);
        assertThat(readModel.asesorFicha().identificador()).isEqualTo("A123");
        assertThat(readModel.asesorFicha().nombre()).isEqualTo("Ana Asesora");
        assertThat(readModel.asesorFicha().email()).isEqualTo("ana@uco.edu.co");
        assertThat(readModel.estado().id()).isEqualTo("FORMULACION");
        assertThat(readModel.estado().nombre()).isEqualTo("Formulacion");
        assertThat(readModel.estado().fechaActualizacion()).isEqualTo(fechaEstado);
        assertThat(readModel.estudiantes()).isEqualTo(estudiantes);
    }
}
