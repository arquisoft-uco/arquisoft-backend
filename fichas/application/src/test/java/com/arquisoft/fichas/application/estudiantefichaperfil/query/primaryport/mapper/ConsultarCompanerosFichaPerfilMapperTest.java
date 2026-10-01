package com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.criteria.EstudianteFichaPerfilCompaneroCriteria;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model.ConsultarCompanerosFichaPerfilQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarCompanerosFichaPerfilMapperTest {

    @Test
    void debeMapearQueryACriteria_conMismoFichaPerfilYEstudiante() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        var query = ConsultarCompanerosFichaPerfilQuery.crear(fichaPerfil, estudiante);

        // Act
        EstudianteFichaPerfilCompaneroCriteria criteria = ConsultarCompanerosFichaPerfilMapper.toCriteria(query);

        // Assert
        assertThat(criteria.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
    }
}
