package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionAsesorQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarObservacionesEvaluacionAsesorMapperTest {

    @Test
    void debeCopiarEvaluacionYAsesorFicha_cuandoMapeaACriteria() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var query = ConsultarObservacionesEvaluacionAsesorQuery.crear(evaluacionFichaPerfil, asesorFicha);

        // Act
        var criteria = ConsultarObservacionesEvaluacionAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.evaluacionFichaPerfil()).isEqualTo(evaluacionFichaPerfil);
        assertThat(criteria.asesorFicha()).isEqualTo(asesorFicha);
    }
}
