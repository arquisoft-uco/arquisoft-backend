package com.arquisoft.entregables.application.artefacto.query.primaryport.model;

import com.arquisoft.entregables.application.artefacto.query.primaryport.mapper.ConsultarVistaPreviaPaqueteMapper;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarVistaPreviaPaqueteQueryTest {

    @Test
    void debeCrearYMapearAlCriteria_cuandoHayProyecto() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarVistaPreviaPaqueteQuery.crear(proyecto);
        var criteria = ConsultarVistaPreviaPaqueteMapper.toCriteria(query);

        // Assert
        assertThat(query.proyectoGrado()).isEqualTo(proyecto);
        assertThat(criteria.proyectoGrado()).isEqualTo(proyecto);
    }

    @Test
    void debeRechazarConErrorDeEntrada_cuandoFaltaElProyecto() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarVistaPreviaPaqueteQuery.crear(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult()
                                .tieneErroresDeCampo(EntregablesFields.ProyectoGrado.ID)).isTrue());
    }
}
