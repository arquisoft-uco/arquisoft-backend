package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.readmodel.CriterioItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorEstudianteReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.readmodel.ItemCualitativoAsesorReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionCualitativaAsesorEstudianteResponseMapperTest {

    @Test
    void debeMapearRespuesta_conItemYCriterio() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var item = new ItemCualitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Claridad", "Evalua la claridad");
        var criterio = new CriterioItemCualitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Excelente", "Cumple todo");
        var readModel = new EvaluacionCualitativaAsesorEstudianteReadModel(id, item, criterio);

        // Act
        var respuesta = EvaluacionCualitativaAsesorEstudianteResponseMapper.toResponse(readModel);

        // Assert
        assertThat(respuesta.id()).isEqualTo(id);
        assertThat(respuesta.item().id()).isEqualTo(item.id());
        assertThat(respuesta.item().nombre()).isEqualTo("Claridad");
        assertThat(respuesta.item().descripcion()).isEqualTo("Evalua la claridad");
        assertThat(respuesta.criterio().id()).isEqualTo(criterio.id());
        assertThat(respuesta.criterio().nombre()).isEqualTo("Excelente");
        assertThat(respuesta.criterio().descripcion()).isEqualTo("Cumple todo");
    }
}
