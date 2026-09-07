package com.arquisoft.solicitudes.infrastructure.solicitud.query.secondaryadapter.repository;

import com.arquisoft.solicitudes.application.solicitud.query.criteria.SolicitudCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudSortMapperTest {

    @Test
    void debeTraducirRemitenteNombre_cuandoCampoOrdenable() {
        // Act
        String ruta = SolicitudSortMapper.traducir("remitenteNombre");

        // Assert
        assertThat(ruta).isEqualTo("remitenteNombre");
    }

    @Test
    void debeTraducirFechaCreacion_cuandoCampoOrdenable() {
        // Act
        String ruta = SolicitudSortMapper.traducir("fechaCreacion");

        // Assert
        assertThat(ruta).isEqualTo("fechaCreacion");
    }

    @Test
    void debeRetornarNull_cuandoCampoNoExiste() {
        // Act
        String ruta = SolicitudSortMapper.traducir("campoInexistente");

        // Assert
        assertThat(ruta).isNull();
    }

    @Test
    void debeRetornarNull_cuandoCampoFiltrablePeroNoOrdenable() {
        // Act
        String ruta = SolicitudSortMapper.traducir("remitenteEmail");

        // Assert
        assertThat(ruta).isNull();
    }

    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (SolicitudCriteria.Campo campo : SolicitudCriteria.Campo.values()) {
            // Act
            String ruta = SolicitudSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(SolicitudCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(ruta != null);
        }
    }
}
