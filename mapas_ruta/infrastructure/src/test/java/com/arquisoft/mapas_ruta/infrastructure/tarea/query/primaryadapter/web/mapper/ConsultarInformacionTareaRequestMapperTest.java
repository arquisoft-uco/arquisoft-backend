package com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarInformacionTareaRequestMapperTest {

    @Test
    void debeArmarElQuery_cuandoLaTareaEsValida() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarInformacionTareaRequestMapper.toQuery(tarea);

        // Assert
        assertThat(query.tarea()).isEqualTo(tarea);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoLaTareaEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarInformacionTareaRequestMapper.toQuery(null))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
