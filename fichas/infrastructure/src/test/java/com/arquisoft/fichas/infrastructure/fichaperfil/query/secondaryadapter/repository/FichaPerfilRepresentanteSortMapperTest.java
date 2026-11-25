package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilRepresentanteCriteria;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class FichaPerfilRepresentanteSortMapperTest {

    @Test
    void debeTraducirLosTresCamposOrdenables() {
        // Arrange
        var campos = FichaPerfilRepresentanteCriteria.Campo.values();

        // Act
        var rutas = Arrays.stream(campos)
                .collect(Collectors.toMap(
                        FichaPerfilRepresentanteCriteria.Campo::getClave,
                        campo -> Optional.ofNullable(
                                FichaPerfilRepresentanteSortMapper.traducir(campo.getClave()))));

        // Assert
        assertThat(campos).allSatisfy(campo -> {
            var ruta = rutas.get(campo.getClave());
            assertThat(FichaPerfilRepresentanteCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable si y solo si el SortMapper le resuelve una ruta",
                            campo.getClave())
                    .isEqualTo(ruta.isPresent());
        });
        assertThat(rutas.values().stream().filter(Optional::isPresent)).hasSize(3);
    }

    @Test
    void debeDevolverNulo_cuandoElCampoNoEsOrdenable() {
        // Act & Assert
        assertThat(FichaPerfilRepresentanteSortMapper.traducir("estadoFicha")).isNull();
        assertThat(FichaPerfilRepresentanteSortMapper.traducir("asesorId")).isNull();
    }
}
