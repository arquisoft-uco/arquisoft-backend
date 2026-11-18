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
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarVistaPreviaPaqueteQuery.crear(proyecto, asesor);
        var criteria = ConsultarVistaPreviaPaqueteMapper.toCriteria(query);

        // Assert
        assertThat(query.proyectoGrado()).isEqualTo(proyecto);
        assertThat(criteria.proyectoGrado()).isEqualTo(proyecto);
        assertThat(criteria.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeRechazarConErrorDeEntrada_cuandoFaltaElProyecto() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarVistaPreviaPaqueteQuery.crear(null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    assertThat(ex.getValidationResult()
                            .tieneErroresDeCampo(EntregablesFields.ProyectoGrado.ID)).isTrue();
                    assertThat(ex.getValidationResult()
                            .tieneErroresDeCampo(EntregablesFields.AsesorProyectoGrado.ASESOR)).isTrue();
                });
    }
}
