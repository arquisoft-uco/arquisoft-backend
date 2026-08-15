package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FichaPerfilSortMapperTest {

    @Test
    void debeTraducirTituloProyecto_cuandoCampoValido() {
        // Arrange
        String clave = "tituloProyecto";

        // Act
        String ruta = FichaPerfilSortMapper.traducir(clave);

        // Assert
        assertThat(ruta).isEqualTo("tituloProyecto");
    }

    @Test
    void debeTraducirAsesorNombre_cuandoCampoValido() {
        // Arrange
        String clave = "asesorNombre";

        // Act
        String ruta = FichaPerfilSortMapper.traducir(clave);

        // Assert
        assertThat(ruta).isEqualTo("asesorNombre");
    }

    @Test
    void debeTraducirAsesorEmail_cuandoCampoValido() {
        // Arrange
        String clave = "asesorEmail";

        // Act
        String ruta = FichaPerfilSortMapper.traducir(clave);

        // Assert
        assertThat(ruta).isEqualTo("asesorEmail");
    }

    @Test
    void debeRetornarNull_cuandoCampoNoExiste() {
        // Arrange
        String claveInvalida = "campoInexistente";

        // Act
        String ruta = FichaPerfilSortMapper.traducir(claveInvalida);

        // Assert
        assertThat(ruta).isNull();
    }

    @Test
    void debeRetornarNull_cuandoCampoNoOrdenable() {
        // asesorId existe en RUTAS pero mapea a null (no ordenable)
        // Arrange
        String claveNoOrdenable = "asesorId";

        // Act
        String ruta = FichaPerfilSortMapper.traducir(claveNoOrdenable);

        // Assert
        assertThat(ruta).isNull();
    }

    // El Criteria rechaza en su builder toda clave que no sea ordenable, asi que el adapter solo
    // recibe claves ya validadas contra este enum. Si el SortMapper no resolviera alguna de ellas,
    // el fallo se manifestaria hasta la consulta como un error de Spring Data — un defecto de
    // mapeo disfrazado de error del cliente. Este test obliga a que ambas fuentes declaren
    // exactamente el mismo conjunto de campos ordenables.
    @Test
    void debeResolverUnaRutaJpa_paraTodoCampoQueElCriteriaDeclaraOrdenable() {
        for (FichaPerfilCriteria.Campo campo : FichaPerfilCriteria.Campo.values()) {
            // Act
            String ruta = FichaPerfilSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(FichaPerfilCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' debe ser ordenable en el Criteria si y solo si "
                            + "el SortMapper le resuelve una ruta JPA", campo.getClave())
                    .isEqualTo(ruta != null);
        }
    }
}
