package com.arquisoft.fichas.domain.fichaperfil.aggregate;

import com.arquisoft.shared.message.FichasFields;
import com.arquisoft.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FichaPerfilAggregateTest {

    @Test
    void debeConstruirFicha_cuandoDatosValidos() {
        // Arrange
        String titulo = "Título de prueba";
        UUID asesorId = UUID.randomUUID();

        // Act
        FichaPerfilAggregate ficha = FichaPerfilAggregate.crear(titulo, asesorId);

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
        assertThatThrownBy(() -> FichaPerfilAggregate.crear(tituloVacio, asesorId))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("tituloProyecto");
    }

    @Test
    void debeLanzarExcepcion_cuandoTituloMuyLargo() {
        // Arrange
        String tituloDe101Caracteres = "a".repeat(101);
        UUID asesorId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> FichaPerfilAggregate.crear(tituloDe101Caracteres, asesorId))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("tituloProyecto");
    }

    @Test
    void debeLanzarExcepcion_cuandoAsesorNull() {
        // Arrange
        String titulo = "Título válido";

        // Act & Assert
        assertThatThrownBy(() -> FichaPerfilAggregate.crear(titulo, null))
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
        FichaPerfilAggregate ficha = FichaPerfilAggregate.reconstruir(id, titulo, asesorId);

        // Assert
        assertThat(ficha.getId()).isEqualTo(id);
        assertThat(ficha.getTituloProyecto()).isEqualTo(titulo);
        assertThat(ficha.getAsesorFicha()).isEqualTo(asesorId);
    }

    @Test
    void debeActualizarTitulo_cuandoTituloValido() {
        // Arrange
        String titulo = "Título original";
        UUID asesorId = UUID.randomUUID();
        FichaPerfilAggregate ficha = FichaPerfilAggregate.crear(titulo, asesorId);

        // Act
        ficha.actualizarTitulo("Título nuevo");

        // Assert
        assertThat(ficha.getTituloProyecto()).isEqualTo("Título nuevo");
    }

    @Test
    void debeRechazarActualizacion_cuandoTituloVacio() {
        // Arrange
        String titulo = "Título válido";
        UUID asesorId = UUID.randomUUID();
        FichaPerfilAggregate ficha = FichaPerfilAggregate.crear(titulo, asesorId);

        // Act & Assert
        assertThatThrownBy(() -> ficha.actualizarTitulo(""))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("tituloProyecto");
    }

    @Test
    void debeRechazarActualizacion_cuandoTituloExcedeMaximo() {
        // Arrange
        String titulo = "Título válido";
        UUID asesorId = UUID.randomUUID();
        FichaPerfilAggregate ficha = FichaPerfilAggregate.crear(titulo, asesorId);

        // Act & Assert
        assertThatThrownBy(() -> ficha.actualizarTitulo("a".repeat(101)))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("tituloProyecto");
    }

    @Test
    void debeConstruirFichaParaCambioDeAsesor_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();

        // Act
        FichaPerfilAggregate ficha = FichaPerfilAggregate.cambiarAsesorFicha(fichaId, nuevoAsesorId);

        // Assert — el título no lo conoce este factory; lo trae quien reconstruye desde persistencia
        assertThat(ficha.getId()).isEqualTo(fichaId);
        assertThat(ficha.getAsesorFicha()).isEqualTo(nuevoAsesorId);
        assertThat(ficha.getTituloProyecto()).isNull();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoNuevoAsesorEsNulo() {
        // Arrange
        UUID fichaId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> FichaPerfilAggregate.cambiarAsesorFicha(fichaId, null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.FichaPerfil.ASESOR_FICHA);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoFichaPerfilEsNula() {
        // Arrange
        UUID nuevoAsesorId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> FichaPerfilAggregate.cambiarAsesorFicha(null, nuevoAsesorId))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.FichaPerfil.ID);
    }
}
