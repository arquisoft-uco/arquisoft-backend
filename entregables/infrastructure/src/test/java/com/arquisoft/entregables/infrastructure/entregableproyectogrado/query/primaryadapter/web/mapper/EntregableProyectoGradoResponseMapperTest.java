package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoResponseMapperTest {

    @Test
    void debeMapearElReadModelAlDTOAnidado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var readModel = new EntregableProyectoGradoReadModel(id, 2,
                new EntregableProyectoGradoReadModel.ProyectoGradoResumen(proyecto, "Proyecto"),
                new EntregableProyectoGradoReadModel.AsesorResumen(asesor, "Ana", "ana@uco.edu.co"),
                new EntregableProyectoGradoReadModel.EstadoEntregableResumen("APROBADO", "Aprobado"));

        // Act
        var dto = EntregableProyectoGradoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.versionEntregable()).isEqualTo(2);
        assertThat(dto.proyectoGrado().id()).isEqualTo(proyecto);
        assertThat(dto.proyectoGrado().nombre()).isEqualTo("Proyecto");
        assertThat(dto.asesor().id()).isEqualTo(asesor);
        assertThat(dto.asesor().nombre()).isEqualTo("Ana");
        assertThat(dto.asesor().email()).isEqualTo("ana@uco.edu.co");
        assertThat(dto.estadoEntregable().id()).isEqualTo("APROBADO");
        assertThat(dto.estadoEntregable().nombre()).isEqualTo("Aprobado");
    }
}
