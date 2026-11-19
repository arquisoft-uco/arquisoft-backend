package com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.mapper;

import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.dto.ParametrosCatalogoResponseDTO;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.dto.ParametrosCatalogoResponseDTO.Metadato;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.dto.ParametrosCatalogoResponseDTO.MetadatoItem;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.dto.ParametrosCatalogoResponseDTO.Parametro;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.dto.ParametrosCatalogoResponseDTO.ParametroItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ParametrosCatalogoMapperTest {

    private static MetadatoItem metadato(Object valor) {
        return new MetadatoItem(new Metadato(valor));
    }

    @Test
    void debeExcluirParametro_cuandoSinMetadatos() {
        // Arrange
        var dto = new ParametrosCatalogoResponseDTO(List.of(
                new ParametroItem(new Parametro("A", true), null),
                new ParametroItem(new Parametro("B", true), List.of())));

        // Act
        var entity = ParametrosCatalogoMapper.toEntity(dto);

        // Assert
        assertThat(entity.valores()).isEmpty();
    }

    @Test
    void debeSaltarElValorCompuesto_yUsarElSiguienteMetadato() {
        // Arrange
        var dto = new ParametrosCatalogoResponseDTO(List.of(new ParametroItem(new Parametro("A", true),
                List.of(metadato(Map.of("k", "v")), metadato(List.of(1)), metadato(true)))));

        // Act
        var entity = ParametrosCatalogoMapper.toEntity(dto);

        // Assert
        assertThat(entity.valores()).containsExactly(Map.entry("A", "true"));
    }

    @Test
    void debeTolerarElementosNulosYNombresVacios() {
        // Arrange
        var items = new ArrayList<ParametroItem>();
        items.add(null);
        items.add(new ParametroItem(null, List.of(metadato("v"))));
        items.add(new ParametroItem(new Parametro("  ", true), List.of(metadato("v"))));
        items.add(new ParametroItem(new Parametro("SIN_ACTIVO", null), List.of(metadato("v"))));
        items.add(new ParametroItem(new Parametro(" OK ", true),
                Arrays.asList(null, new MetadatoItem(null), metadato(" valor "))));

        // Act
        var entity = ParametrosCatalogoMapper.toEntity(new ParametrosCatalogoResponseDTO(items));

        // Assert
        assertThat(entity.valores()).containsExactly(Map.entry("OK", "valor"));
    }

    @Test
    void debeConservarElPrimero_cuandoElNombreSeRepite() {
        // Arrange
        var dto = new ParametrosCatalogoResponseDTO(List.of(
                new ParametroItem(new Parametro("A", true), List.of(metadato("uno"))),
                new ParametroItem(new Parametro("A", true), List.of(metadato("dos")))));

        // Act
        var entity = ParametrosCatalogoMapper.toEntity(dto);

        // Assert
        assertThat(entity.valores()).containsExactly(Map.entry("A", "uno"));
    }
}
