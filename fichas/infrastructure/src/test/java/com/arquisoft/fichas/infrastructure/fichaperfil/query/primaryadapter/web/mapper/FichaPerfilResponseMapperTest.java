package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FichaPerfilResponseMapperTest {

    @Test
    void debeMapearFichaAsesorYEstado_cuandoElReadModelTraeEstado() {
        // Arrange
        var idFicha = UUID.randomUUID();
        var idAsesor = UUID.randomUUID();
        var fechaActualizacion = Instant.parse("2026-09-30T10:15:30Z");
        var readModel = new FichaPerfilReadModel(
                idFicha,
                "Arquisoft Backend",
                new AsesorFichaReadModel(idAsesor, "DOC-001", "Juan Salazar", "juan@uco.edu.co"),
                new EstadoFichaPerfilReadModel("EN_CONSTRUCCION", "En Construccion", fechaActualizacion));

        // Act
        var dto = FichaPerfilResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(idFicha);
        assertThat(dto.tituloProyecto()).isEqualTo("Arquisoft Backend");
        assertThat(dto.asesorFicha().id()).isEqualTo(idAsesor);
        assertThat(dto.estado().id()).isEqualTo("EN_CONSTRUCCION");
        assertThat(dto.estado().nombre()).isEqualTo("En Construccion");
        assertThat(dto.estado().fechaActualizacion()).isEqualTo(fechaActualizacion);
    }
}
