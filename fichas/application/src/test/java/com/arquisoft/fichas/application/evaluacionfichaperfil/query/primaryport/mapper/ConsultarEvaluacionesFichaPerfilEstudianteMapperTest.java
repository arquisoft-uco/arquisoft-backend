package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilEstudianteQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesFichaPerfilEstudianteMapperTest {

    @Test
    void debeMapearQueryACriteria_conLosDosIdentificadores() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesFichaPerfilEstudianteQuery.crear(fichaPerfil, estudiante);

        // Act
        var criteria = ConsultarEvaluacionesFichaPerfilEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
    }
}
