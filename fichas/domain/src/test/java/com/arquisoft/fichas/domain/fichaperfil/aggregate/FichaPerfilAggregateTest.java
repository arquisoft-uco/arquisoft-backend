package com.arquisoft.fichas.domain.fichaperfil.aggregate;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.message.FichasMessages;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

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
        assertThat(ficha.getAsesorFichaId()).isEqualTo(asesorId);
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
                .hasMessageContaining(FichasMessages.FichaPerfil.CAMPO_ASESOR_FICHA);
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
        assertThat(ficha.getAsesorFichaId()).isEqualTo(asesorId);
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
    void debeCambiarAsesor_cuandoNuevoAsesorEsDiferenteYEstadoNoTerminal() {
        // Arrange
        UUID asesorActualId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        FichaPerfilAggregate ficha = FichaPerfilAggregate.reconstruir(
                UUID.randomUUID(),
                "Título de prueba",
                asesorActualId
        );

        // Act
        ficha.cambiarAsesorFicha(nuevoAsesorId, EstadoFicha.EN_CONSTRUCCION);

        // Assert
        assertThat(ficha.getAsesorFichaId()).isEqualTo(nuevoAsesorId);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoNuevoAsesorIdEsNulo() {
        // Arrange
        FichaPerfilAggregate ficha = FichaPerfilAggregate.reconstruir(
                UUID.randomUUID(),
                "Título de prueba",
                UUID.randomUUID()
        );

        // Act
        Throwable excepcion = catchThrowable(() ->
                ficha.cambiarAsesorFicha(null, EstadoFicha.EN_CONSTRUCCION)
        );

        // Assert
        assertThat(excepcion).isInstanceOf(DomainValidationException.class);
        DomainValidationException domainEx = (DomainValidationException) excepcion;
        assertThat(domainEx.getValidationResult().getErrors()).hasSize(1);
        assertThat(domainEx.getValidationResult().getErrors().get(0).field())
                .isEqualTo(FichasMessages.FichaPerfil.CAMPO_ASESOR_FICHA);
        assertThat(domainEx.getValidationResult().getErrors().get(0).errorCode())
                .isEqualTo(FichasMessages.FichaPerfil.ASESOR_REQUERIDO);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoNuevoAsesorEsIgualAlActual() {
        // Arrange
        UUID asesorActualId = UUID.randomUUID();
        FichaPerfilAggregate ficha = FichaPerfilAggregate.reconstruir(
                UUID.randomUUID(),
                "Título de prueba",
                asesorActualId
        );

        // Act
        Throwable excepcion = catchThrowable(() ->
                ficha.cambiarAsesorFicha(asesorActualId, EstadoFicha.EN_CONSTRUCCION)
        );

        // Assert
        assertThat(excepcion).isInstanceOf(DomainValidationException.class);
        DomainValidationException domainEx = (DomainValidationException) excepcion;
        assertThat(domainEx.getValidationResult().getErrors()).hasSize(1);
        assertThat(domainEx.getValidationResult().getErrors().get(0).field())
                .isEqualTo(FichasMessages.FichaPerfil.CAMPO_ASESOR_FICHA);
        assertThat(domainEx.getValidationResult().getErrors().get(0).errorCode())
                .isEqualTo(FichasMessages.FichaPerfil.MISMO_ASESOR);
        assertThat(domainEx.getValidationResult().getErrors().get(0).message())
                .contains(asesorActualId.toString());
    }

    @Test
    void debeLanzarDomainValidationException_cuandoEstadoEsAprobada() {
        // Arrange
        UUID asesorActualId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        FichaPerfilAggregate ficha = FichaPerfilAggregate.reconstruir(
                UUID.randomUUID(),
                "Título de prueba",
                asesorActualId
        );

        // Act
        Throwable excepcion = catchThrowable(() ->
                ficha.cambiarAsesorFicha(nuevoAsesorId, EstadoFicha.APROBADA)
        );

        // Assert
        assertThat(excepcion).isInstanceOf(DomainValidationException.class);
        DomainValidationException domainEx = (DomainValidationException) excepcion;
        assertThat(domainEx.getValidationResult().getErrors()).hasSize(1);
        assertThat(domainEx.getValidationResult().getErrors().get(0).field())
                .isEqualTo(FichasMessages.FichaPerfil.CAMPO_ESTADO_FICHA);
        assertThat(domainEx.getValidationResult().getErrors().get(0).errorCode())
                .isEqualTo(FichasMessages.FichaPerfil.ESTADO_TERMINAL);
        assertThat(domainEx.getValidationResult().getErrors().get(0).message())
                .contains("APROBADA");
    }

    @Test
    void debeLanzarDomainValidationException_cuandoEstadoEsAprobadaConObservaciones() {
        // Arrange
        UUID asesorActualId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        FichaPerfilAggregate ficha = FichaPerfilAggregate.reconstruir(
                UUID.randomUUID(),
                "Título de prueba",
                asesorActualId
        );

        // Act
        Throwable excepcion = catchThrowable(() ->
                ficha.cambiarAsesorFicha(nuevoAsesorId, EstadoFicha.APROBADA_CON_OBSERVACIONES)
        );

        // Assert
        assertThat(excepcion).isInstanceOf(DomainValidationException.class);
        DomainValidationException domainEx = (DomainValidationException) excepcion;
        assertThat(domainEx.getValidationResult().getErrors()).hasSize(1);
        assertThat(domainEx.getValidationResult().getErrors().get(0).field())
                .isEqualTo(FichasMessages.FichaPerfil.CAMPO_ESTADO_FICHA);
        assertThat(domainEx.getValidationResult().getErrors().get(0).errorCode())
                .isEqualTo(FichasMessages.FichaPerfil.ESTADO_TERMINAL);
        assertThat(domainEx.getValidationResult().getErrors().get(0).message())
                .contains("APROBADA_CON_OBSERVACIONES");
    }

    @Test
    void debeLanzarDomainValidationException_cuandoEstadoEsNoAprobada() {
        // Arrange
        UUID asesorActualId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        FichaPerfilAggregate ficha = FichaPerfilAggregate.reconstruir(
                UUID.randomUUID(),
                "Título de prueba",
                asesorActualId
        );

        // Act
        Throwable excepcion = catchThrowable(() ->
                ficha.cambiarAsesorFicha(nuevoAsesorId, EstadoFicha.NO_APROBADA)
        );

        // Assert
        assertThat(excepcion).isInstanceOf(DomainValidationException.class);
        DomainValidationException domainEx = (DomainValidationException) excepcion;
        assertThat(domainEx.getValidationResult().getErrors()).hasSize(1);
        assertThat(domainEx.getValidationResult().getErrors().get(0).field())
                .isEqualTo(FichasMessages.FichaPerfil.CAMPO_ESTADO_FICHA);
        assertThat(domainEx.getValidationResult().getErrors().get(0).errorCode())
                .isEqualTo(FichasMessages.FichaPerfil.ESTADO_TERMINAL);
        assertThat(domainEx.getValidationResult().getErrors().get(0).message())
                .contains("NO_APROBADA");
    }
}
