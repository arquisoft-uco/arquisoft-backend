package com.arquisoft.solicitudes.infrastructure.respuesta.query.secondaryadapter.repository;

import com.arquisoft.solicitudes.application.respuesta.query.criteria.RespuestaCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RespuestaSortMapperTest {

    @Test
    void debeTraducirFechaRespuesta_cuandoCampoOrdenable() {
        // Act
        String ruta = RespuestaSortMapper.traducir("fechaRespuesta");

        // Assert
        assertThat(ruta).isEqualTo("fechaRespuesta");
    }

    @Test
    void debeTraducirDestinatarioNombre_cuandoCampoOrdenable() {
        // Act
        String ruta = RespuestaSortMapper.traducir("destinatarioNombre");

        // Assert
        assertThat(ruta).isEqualTo("destinatarioNombre");
    }

    @Test
    void debeRetornarNull_cuandoCamposNoOrdenables() {
        // Act & Assert
        assertThat(RespuestaSortMapper.traducir("contenido")).isNull();
        assertThat(RespuestaSortMapper.traducir("estadoRespuestaId")).isNull();
        assertThat(RespuestaSortMapper.traducir("tipoSolicitudId")).isNull();
        assertThat(RespuestaSortMapper.traducir("remitenteUsuarioId")).isNull();
        assertThat(RespuestaSortMapper.traducir("destinatarioUsuarioId")).isNull();
        assertThat(RespuestaSortMapper.traducir("destinatarioIdentificador")).isNull();
        assertThat(RespuestaSortMapper.traducir("destinatarioEmail")).isNull();
    }

    @Test
    void debeRetornarNull_cuandoCampoNoExiste() {
        // Act
        String ruta = RespuestaSortMapper.traducir("campoInexistente");

        // Assert
        assertThat(ruta).isNull();
    }

    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (RespuestaCriteria.Campo campo : RespuestaCriteria.Campo.values()) {
            // Act
            String ruta = RespuestaSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(RespuestaCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(ruta != null);
        }
    }
}
