package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilAsesorCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoFichaPerfilAsesorSortMapperTest {

    @Test
    void debeTraducirTituloProyecto_cuandoCampoValido() {
        // Arrange
        var clave = "tituloProyecto";

        // Act
        var ruta = EstadoFichaPerfilAsesorSortMapper.traducir(clave);

        // Assert
        assertThat(ruta).isEqualTo("tituloProyecto");
    }

    @Test
    void debeRetornarNull_cuandoCampoNoOrdenable() {
        // Arrange & Act & Assert
        assertThat(EstadoFichaPerfilAsesorSortMapper.traducir("fichaPerfil")).isNull();
        assertThat(EstadoFichaPerfilAsesorSortMapper.traducir("estadoFicha")).isNull();
        assertThat(EstadoFichaPerfilAsesorSortMapper.traducir("asesorFicha")).isNull();
    }

    @Test
    void debeRetornarNull_cuandoCampoNoExiste() {
        // Arrange
        var claveInvalida = "campoInexistente";

        // Act
        var ruta = EstadoFichaPerfilAsesorSortMapper.traducir(claveInvalida);

        // Assert
        assertThat(ruta).isNull();
    }

    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (var campo : EstadoFichaPerfilAsesorCriteria.Campo.values()) {
            // Act
            var ruta = EstadoFichaPerfilAsesorSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(EstadoFichaPerfilAsesorCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(ruta != null);
        }
    }
}
