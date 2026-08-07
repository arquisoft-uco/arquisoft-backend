package com.arquisoft.fichas.domain.fichaperfil.aggregate;

import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FichaPerfilDomainTest {

    @Test
    void debeConstruirFicha_cuandoDatosValidos() {
        // Arrange
        String titulo = "Título de prueba";
        UUID asesorId = UUID.randomUUID();

        // Act
        FichaPerfilDomain ficha = FichaPerfilDomain.crear(titulo, asesorId);

        // Assert
        assertThat(ficha.getId()).isNotNull();
        assertThat(ficha.getTituloProyecto()).isEqualTo("Título de prueba");
        assertThat(ficha.getAsesorFicha()).isEqualTo(asesorId);
    }

    @Test
    void debeLanzarExcepcion_cuandoTituloVacio() {
        // Arrange
        String tituloVacio = "";
        UUID asesorId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> FichaPerfilDomain.crear(tituloVacio, asesorId))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("tituloProyecto");
    }

    @Test
    void debeLanzarExcepcion_cuandoTituloMuyLargo() {
        // Arrange
        String tituloDe101Caracteres = "a".repeat(101);
        UUID asesorId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> FichaPerfilDomain.crear(tituloDe101Caracteres, asesorId))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("tituloProyecto");
    }

    @Test
    void debeLanzarExcepcion_cuandoAsesorNull() {
        // Arrange
        String titulo = "Título válido";

        // Act & Assert
        assertThatThrownBy(() -> FichaPerfilDomain.crear(titulo, null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.FichaPerfil.ASESOR_FICHA);
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        UUID id = UUID.randomUUID();
        String titulo = "Título reconstruido";
        UUID asesorId = UUID.randomUUID();

        // Act
        FichaPerfilDomain ficha = FichaPerfilDomain.reconstruir(id, titulo, asesorId);

        // Assert
        assertThat(ficha.getId()).isEqualTo(id);
        assertThat(ficha.getTituloProyecto()).isEqualTo(titulo);
        assertThat(ficha.getAsesorFicha()).isEqualTo(asesorId);
    }

}
