package com.arquisoft.entregables.infrastructure.artefactoentregable.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarArtefactosEntregableRequestMapperTest {

    @Test
    void debeConstruirLaQuery_conElEntregableDelPathYLaIdentidad() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var identidad = new IdentidadActual(asesor, "a@uco.edu.co", "asesor", List.of(), null, null);

        // Act
        var query = ConsultarArtefactosEntregableRequestMapper.toQuery(entregable, identidad);

        // Assert
        assertThat(query.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(query.asesor()).isEqualTo(asesor);
    }
}
