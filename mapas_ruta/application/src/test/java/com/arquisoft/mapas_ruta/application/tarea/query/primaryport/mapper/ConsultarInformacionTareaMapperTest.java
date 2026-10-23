package com.arquisoft.mapas_ruta.application.tarea.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.tarea.query.criteria.TareaCriteria;
import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarInformacionTareaQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarInformacionTareaMapperTest {

    @Test
    void debeMapearQueryACriteria_cuandoLaConsultaEsValida() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();

        // Act
        var criteria = ConsultarInformacionTareaMapper.toCriteria(ConsultarInformacionTareaQuery.crear(tarea));

        // Assert
        assertThat(criteria).isEqualTo(new TareaCriteria(tarea));
    }
}
