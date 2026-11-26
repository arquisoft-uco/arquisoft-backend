package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionParticipanteReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilRepresentanteReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FichaPerfilRepresentanteResponseMapperTest {

    @Test
    void debeMapearFichaYEvaluacionConSuEstado_cuandoHayReadModel() {
        // Arrange
        var fichaId = UtilUUID.generarNuevoUUID();
        var evaluacionId = UtilUUID.generarNuevoUUID();
        var asesor = new AsesorFichaReadModel(UtilUUID.generarNuevoUUID(), "DOC-001", "Laura Gomez",
                "laura@soyuco.edu.co");
        var fecha = Instant.parse("2026-11-01T10:00:00Z");
        var estado = new EstadoFichaPerfilReadModel("APROBADA", "Aprobada", fecha);
        var ficha = new FichaPerfilReadModel(fichaId, "Arquisoft Backend", asesor, estado);
        var evaluacion = new EvaluacionParticipanteReadModel(evaluacionId, "DESCARTADA", "Descartada");

        // Act
        var dto = FichaPerfilRepresentanteResponseMapper.toResponse(
                new FichaPerfilRepresentanteReadModel(ficha, evaluacion));

        // Assert
        assertThat(dto.id()).isEqualTo(fichaId);
        assertThat(dto.tituloProyecto()).isEqualTo("Arquisoft Backend");
        assertThat(dto.asesorFicha().nombre()).isEqualTo("Laura Gomez");
        assertThat(dto.estado().id()).isEqualTo("APROBADA");
        assertThat(dto.estado().fechaActualizacion()).isEqualTo(fecha);
        assertThat(dto.evaluacion().id()).isEqualTo(evaluacionId);
        assertThat(dto.evaluacion().estado().id()).isEqualTo("DESCARTADA");
        assertThat(dto.evaluacion().estado().nombre()).isEqualTo("Descartada");
    }
}
