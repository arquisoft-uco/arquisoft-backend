package com.arquisoft.fichas.application.observacionitem.command.validator.impl;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.observacionitem.RemocionObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemNoEncontradaException;
import com.arquisoft.fichas.domain.observacionitem.model.ContextoObservacionItem;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemCerradaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverObservacionItemValidatorImplTest {

    private final RemoverObservacionItemValidatorImpl validator = new RemoverObservacionItemValidatorImpl();

    @ParameterizedTest
    @EnumSource(value = EstadoRevision.class,
            names = {"NUEVA", "VISUALIZADA", "EN_PROGRESO", "CORRECCION_DISPONIBLE"})
    void debeNoLanzar_cuandoLaRevisionNoEstaCerrada(EstadoRevision estado) {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var entrada = entradaCon(asesorFicha);
        var contexto = contextoCon(estado, asesorFicha);

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada, contexto)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoElContextoEsVacio() {
        // Arrange
        var entrada = entradaCon(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, ContextoObservacionItem.VACIO))
                .isInstanceOf(ObservacionItemNoEncontradaException.class);
    }

    @Test
    void debeLanzarFichaNoPerteneceAsesor_cuandoElAsesorNoEsElDeLaFicha() {
        // Arrange
        var entrada = entradaCon(UtilUUID.generarNuevoUUID());
        var contexto = contextoCon(EstadoRevision.NUEVA, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, contexto))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);
    }

    @Test
    void debeLanzarRevisionCerrada_cuandoElAsesorEsElDeLaFichaYLaRevisionEstaCerrada() {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var entrada = entradaCon(asesorFicha);
        var contexto = contextoCon(EstadoRevision.CERRADA, asesorFicha);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, contexto))
                .isInstanceOf(RevisionItemCerradaException.class);
    }

    @Test
    void debeLanzarFichaNoPerteneceAsesor_antesQueRevisionCerrada_cuandoAmbasFallan() {
        // Arrange — un asesor ajeno no debe averiguar que la revisión está cerrada: la propiedad decide primero
        var entrada = entradaCon(UtilUUID.generarNuevoUUID());
        var contexto = contextoCon(EstadoRevision.CERRADA, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, contexto))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);
    }

    private RemocionObservacionItemDomain entradaCon(UUID asesorFicha) {
        return RemocionObservacionItemDomain.crear(UtilUUID.generarNuevoUUID(), asesorFicha);
    }

    private ContextoObservacionItem contextoCon(EstadoRevision estado, UUID asesorDeLaFicha) {
        return new ContextoObservacionItem(
                UtilUUID.generarNuevoUUID(), estado, UtilUUID.generarNuevoUUID(), asesorDeLaFicha);
    }
}
