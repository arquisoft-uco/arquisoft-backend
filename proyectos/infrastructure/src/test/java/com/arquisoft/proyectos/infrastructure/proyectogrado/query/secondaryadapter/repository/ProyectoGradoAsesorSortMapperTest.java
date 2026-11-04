package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoAsesorCriteria;
import com.arquisoft.shared.util.UtilObjeto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoAsesorSortMapperTest {

    @Test
    void debeTraducirTitulo_cuandoCampoOrdenable() {
        // Act
        var ruta = ProyectoGradoAsesorSortMapper.traducir("tituloProyecto");

        // Assert
        assertThat(ruta).isEqualTo("tituloProyecto");
    }

    @Test
    void debeResolverRutaSoloParaCamposOrdenablesDelCriteria_cuandoSeCompara() {
        for (var campo : ProyectoGradoAsesorCriteria.Campo.values()) {
            // Act
            var ruta = ProyectoGradoAsesorSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(ProyectoGradoAsesorCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("campo %s", campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
        assertThat(ProyectoGradoAsesorSortMapper.traducir("inexistente")).isNull();
    }
}
