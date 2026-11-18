package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarMapaRutaProyectoGradoAsesorRequestMapperTest {

    @Test
    void debeCrearLaConsultaConElProyectoGrado_cuandoElUuidEsValido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarMapaRutaProyectoGradoAsesorRequestMapper.toQuery(proyectoGrado);

        // Assert
        assertThat(query.proyectoGrado()).isEqualTo(proyectoGrado);
    }
}
