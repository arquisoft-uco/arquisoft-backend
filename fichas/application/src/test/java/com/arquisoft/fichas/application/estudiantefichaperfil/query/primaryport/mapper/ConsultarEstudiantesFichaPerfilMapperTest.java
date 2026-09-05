package com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.criteria.EstudianteFichaPerfilCriteria;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model.ConsultarEstudiantesFichaPerfilQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstudiantesFichaPerfilMapperTest {

    @Test
    void debeMapearQueryACriteria_conMismoFichaPerfil() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var query = ConsultarEstudiantesFichaPerfilQuery.crear(fichaPerfil);

        // Act
        EstudianteFichaPerfilCriteria criteria = ConsultarEstudiantesFichaPerfilMapper.toCriteria(query);

        // Assert
        assertThat(criteria.fichaPerfil()).isEqualTo(fichaPerfil);
    }
}
