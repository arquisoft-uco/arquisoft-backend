package com.arquisoft.fichas.application.observacionitem.command.validator.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.observacionitem.AgregacionObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.ObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemDuplicadaException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemCerradaException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarObservacionItemValidatorImplTest {

    private final AgregarObservacionItemValidatorImpl validator = new AgregarObservacionItemValidatorImpl();

    @Test
    void debeNoLanzar_cuandoTodoEsValido() {
        // Arrange
        var asesorFicha = UUID.randomUUID();
        var entrada = entradaCon(asesorFicha);

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada, true, "NUEVA", UUID.randomUUID(), asesorFicha, 0L))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarRevisionItemNoEncontrada_antesQueRevisionCerrada_cuandoLaRevisionNoExiste() {
        // Arrange — estadoRevisionId también sería "CERRADA": la existencia decide primero
        var entrada = entradaCon(UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, false, "CERRADA", UUID.randomUUID(), entrada.getAsesorFicha(), 0L))
                .isInstanceOf(RevisionItemNoEncontradoException.class);
    }

    @Test
    void debeLanzarRevisionItemCerrada_antesQuePropiedad_cuandoLaRevisionEstaCerrada() {
        // Arrange — el asesor tampoco es el propietario: la revisión cerrada decide primero
        var entrada = entradaCon(UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, true, "CERRADA", UUID.randomUUID(), UUID.randomUUID(), 0L))
                .isInstanceOf(RevisionItemCerradaException.class);
    }

    @Test
    void debeLanzarFichaNoPerteneceAsesor_antesQueObservacionDuplicada_cuandoElAsesorNoEsPropietario() {
        // Arrange — el texto también está duplicado: la propiedad decide primero
        var entrada = entradaCon(UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, true, "NUEVA", UUID.randomUUID(), UUID.randomUUID(), 1L))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);
    }

    @Test
    void debeLanzarObservacionItemDuplicada_cuandoElTextoYaExisteYTodoLoDemasEsValido() {
        // Arrange
        var asesorFicha = UUID.randomUUID();
        var entrada = entradaCon(asesorFicha);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, "NUEVA", UUID.randomUUID(), asesorFicha, 1L))
                .isInstanceOf(ObservacionItemDuplicadaException.class);
    }

    private AgregacionObservacionItemDomain entradaCon(UUID asesorFicha) {
        var observacionItem = ObservacionItemDomain.crear(UUID.randomUUID(), "Observación válida");
        return AgregacionObservacionItemDomain.crear(observacionItem, asesorFicha);
    }
}
