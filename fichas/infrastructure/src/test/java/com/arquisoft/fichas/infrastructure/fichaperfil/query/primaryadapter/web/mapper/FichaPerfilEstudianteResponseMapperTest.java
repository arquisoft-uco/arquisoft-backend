package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FichaPerfilEstudianteResponseMapperTest {

    @Test
    void debeMapearReadModelAResponseDTO_conTodosLosCampos() {
        // Arrange
        var fichaId = UUID.randomUUID();
        var asesorId = UUID.randomUUID();
        var estudianteReadModel = new EstudianteFichaPerfilReadModel(
                UUID.randomUUID(), fichaId, UUID.randomUUID(), "Estudiante Uno", "e1@uco.edu.co");
        var readModel = new FichaPerfilEstudianteReadModel(
                fichaId, "Sistema de gestion",
                new AsesorFichaReadModel(asesorId, "A100", "Asesor Uno", "asesor@uco.edu.co"),
                new EstadoFichaPerfilReadModel("FORMULACION", "Formulacion", Instant.now()),
                List.of(estudianteReadModel));

        // Act
        var response = FichaPerfilEstudianteResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.idFichaPerfil()).isEqualTo(fichaId);
        assertThat(response.titulo()).isEqualTo("Sistema de gestion");
        assertThat(response.asesor().id()).isEqualTo(asesorId);
        assertThat(response.asesor().nombre()).isEqualTo("Asesor Uno");
        assertThat(response.estado().id()).isEqualTo("FORMULACION");
        assertThat(response.estudiantes()).hasSize(1);
        assertThat(response.estudiantes().getFirst().id()).isEqualTo(estudianteReadModel.id());
    }

    @Test
    void debeMapearListaEstudiantesVacia_sinError() {
        // Arrange
        var readModel = new FichaPerfilEstudianteReadModel(
                UUID.randomUUID(), "Titulo",
                new AsesorFichaReadModel(UUID.randomUUID(), "A1", "Asesor", "asesor@uco.edu.co"),
                new EstadoFichaPerfilReadModel("FORMULACION", "Formulacion", Instant.now()),
                List.of());

        // Act
        var response = FichaPerfilEstudianteResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.estudiantes()).isEmpty();
    }
}
