package com.arquisoft.fichas.infrastructure.fichaperfil.query.adapter.out.persistence;

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
        assertThat(ruta).isEqualTo("asesorFicha.nombre");
    }

    @Test
    void debeTraducirAsesorEmail_cuandoCampoValido() {
        // Arrange
        String clave = "asesorEmail";

        // Act
        String ruta = FichaPerfilSortMapper.traducir(clave);

        // Assert
        assertThat(ruta).isEqualTo("asesorFicha.email");
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
}
