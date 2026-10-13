package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.AsignarTareaCommand;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsignarTareaMapperTest {

    @Test
    void debeMapearCommandADomain() {
        // Arrange
        var command = new AsignarTareaCommand(
                UtilUUID.generarNuevoUUID(), "Redactar capitulo", UtilUUID.generarNuevoUUID());

        // Act
        var tarea = AsignarTareaMapper.toDomain(command);

        // Assert
        assertThat(tarea.getMapaRuta()).isEqualTo(command.mapaRuta());
        assertThat(tarea.getDescripcion()).isEqualTo(command.descripcion());
        assertThat(tarea.getAsesor()).isEqualTo(command.asesor());
        assertThat(tarea.getEstadoItem()).isEqualTo(EstadoItem.NUEVA);
    }
}
