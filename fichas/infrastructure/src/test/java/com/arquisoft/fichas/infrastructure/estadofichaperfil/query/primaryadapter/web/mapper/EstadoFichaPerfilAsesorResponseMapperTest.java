package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilAsesorReadModel;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoFichaPerfilAsesorResponseMapperTest {

    @Test
    void debeMapearReadModelAResponseDTO_conTodosLosCampos() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var fecha = Instant.now();
        var readModel = new EstadoFichaPerfilAsesorReadModel(
                fichaPerfil, "Sistema de gestion", "APROBADA", "Aprobada", fecha);

        // Act
        var response = EstadoFichaPerfilAsesorResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(response.tituloProyecto()).isEqualTo("Sistema de gestion");
        assertThat(response.estadoId()).isEqualTo("APROBADA");
        assertThat(response.estadoNombre()).isEqualTo("Aprobada");
        assertThat(response.fechaActualizacion()).isEqualTo(fecha);
    }
}
