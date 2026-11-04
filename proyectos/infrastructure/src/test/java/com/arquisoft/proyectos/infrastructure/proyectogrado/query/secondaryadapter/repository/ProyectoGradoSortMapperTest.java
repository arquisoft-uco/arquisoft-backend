package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoSortMapperTest {

    @Test
    void debeTraducirTituloProyecto_cuandoCampoOrdenable() {
        // Act
        var ruta = ProyectoGradoSortMapper.traducir("tituloProyecto");

        // Assert
        assertThat(ruta).isEqualTo("tituloProyecto");
    }

    @Test
    void debeDevolverNulo_cuandoCampoNoOrdenableOInexistente() {
        // Act & Assert
        assertThat(ProyectoGradoSortMapper.traducir("coordinador")).isNull();
        assertThat(ProyectoGradoSortMapper.traducir("estadoProyectoGrado")).isNull();
        assertThat(ProyectoGradoSortMapper.traducir("inexistente")).isNull();
    }

    @Test
    void debeResolverRutaSoloParaCamposOrdenablesDelCriteria_cuandoSeCompara() {
        for (var campo : ProyectoGradoCriteria.Campo.values()) {
            // Act
            var ruta = ProyectoGradoSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(ProyectoGradoCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("campo %s", campo.getClave())
                    .isEqualTo(ruta != null);
        }
    }
}
