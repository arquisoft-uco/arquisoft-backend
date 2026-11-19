package com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.dto;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class ParametrosCatalogoResponseDTOTest {

    @Test
    void debeIgnorarCamposDesconocidos() {
        // Arrange
        var mapper = JsonMapper.builder().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        var json = """
                {"extraRaiz":1,"parametros":[{"extraItem":true,
                  "parametro":{"nombre":"PARAM_X","activo":true,"extraParametro":"x"},
                  "metadatos":[{"extraMetadatoItem":0,"metadato":{"valor":"v","extraMetadato":[1]}}]}]}
                """;

        // Act
        var dto = mapper.readValue(json, ParametrosCatalogoResponseDTO.class);

        // Assert
        assertThat(dto.parametros()).hasSize(1);
        var item = dto.parametros().get(0);
        assertThat(item.parametro().nombre()).isEqualTo("PARAM_X");
        assertThat(item.parametro().activo()).isTrue();
        assertThat(item.metadatos().get(0).metadato().valor()).isEqualTo("v");
    }
}
