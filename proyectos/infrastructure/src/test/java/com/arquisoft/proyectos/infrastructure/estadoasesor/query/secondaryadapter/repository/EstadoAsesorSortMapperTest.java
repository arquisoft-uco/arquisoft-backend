package com.arquisoft.proyectos.infrastructure.estadoasesor.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.estadoasesor.query.criteria.EstadoAsesorCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoAsesorSortMapperTest {

    @Test
    void debeTraducirIdYNombre_cuandoCamposOrdenables() {
        // Act & Assert
        assertThat(EstadoAsesorSortMapper.traducir("id")).isEqualTo("id");
        assertThat(EstadoAsesorSortMapper.traducir("nombre")).isEqualTo("nombre");
    }

    @Test
    void debeDevolverNuloYCoincidirConElCriteria_cuandoCampoInexistente() {
        // Act & Assert
        assertThat(EstadoAsesorSortMapper.traducir("descripcion")).isNull();
        for (var campo : EstadoAsesorCriteria.Campo.values()) {
            assertThat(EstadoAsesorCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("campo %s", campo.getClave())
                    .isEqualTo(EstadoAsesorSortMapper.traducir(campo.getClave()) != null);
        }
    }
}
