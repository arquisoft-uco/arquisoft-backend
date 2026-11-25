package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilDisponibleEvaluacionCriteria;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class FichaPerfilDisponibleEvaluacionSortMapperTest {

    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        // Arrange
        var campos = FichaPerfilDisponibleEvaluacionCriteria.Campo.values();

        // Act
        var rutas = Arrays.stream(campos)
                .collect(Collectors.toMap(
                        FichaPerfilDisponibleEvaluacionCriteria.Campo::getClave,
                        campo -> Optional.ofNullable(
                                FichaPerfilDisponibleEvaluacionSortMapper.traducir(campo.getClave()))));

        // Assert
        assertThat(campos).allSatisfy(campo -> {
            var ruta = rutas.get(campo.getClave());
            assertThat(FichaPerfilDisponibleEvaluacionCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable si y solo si el SortMapper le resuelve una ruta",
                            campo.getClave())
                    .isEqualTo(ruta.isPresent());
            assertThat(ruta).contains(campo.getClave());
        });
    }

    @Test
    void debeDevolverNulo_cuandoLaClaveNoEsOrdenable() {
        // Act & Assert
        assertThat(FichaPerfilDisponibleEvaluacionSortMapper.traducir("estadoFicha")).isNull();
        assertThat(FichaPerfilDisponibleEvaluacionSortMapper.traducir("asesorId")).isNull();
    }
}
