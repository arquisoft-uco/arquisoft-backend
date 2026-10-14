package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estadoproyecto.query.readmodel.EstadoProyectoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoResponseMapperTest {

    @Test
    void debeMapearReadModelADto_cuandoSeConvierte() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ficha = UtilUUID.generarNuevoUUID();
        var readModel = new ProyectoGradoReadModel(id, ficha, "Sistema de gestion",
                new EstadoProyectoReadModel("EN_PROCESO", "En proceso"));

        // Act
        var dto = ProyectoGradoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.fichaPerfil()).isEqualTo(ficha);
        assertThat(dto.tituloProyecto()).isEqualTo("Sistema de gestion");
        assertThat(dto.estado().id()).isEqualTo("EN_PROCESO");
        assertThat(dto.estado().nombre()).isEqualTo("En proceso");
    }
}
