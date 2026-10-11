package com.arquisoft.fichas.application.observacionitem.command.validator.impl;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.observacionitem.ModificacionObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemDuplicadaException;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemNoEncontradaException;
import com.arquisoft.fichas.domain.observacionitem.model.ContextoObservacionItem;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemCerradaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarObservacionItemValidatorImplTest {

    private final ModificarObservacionItemValidatorImpl validator = new ModificarObservacionItemValidatorImpl();

    @Test
    void debeNoLanzar_cuandoTodoEsValido() {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var entrada = entradaCon(asesorFicha);
        var contexto = contextoCon(EstadoRevision.NUEVA, asesorFicha);

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada, contexto, 0L)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_antesQueElResto_cuandoElContextoEsVacio() {
        // Arrange — asesor ajeno y texto duplicado también fallarían: la existencia decide primero
        var entrada = entradaCon(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, ContextoObservacionItem.VACIO, 1L))
                .isInstanceOf(ObservacionItemNoEncontradaException.class);
    }

    @Test
    void debeLanzarRevisionCerrada_antesQuePropiedad_cuandoLaRevisionEstaCerrada() {
        // Arrange — el asesor tampoco es el de la ficha: la revisión cerrada decide primero
        var entrada = entradaCon(UtilUUID.generarNuevoUUID());
        var contexto = contextoCon(EstadoRevision.CERRADA, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, contexto, 0L))
                .isInstanceOf(RevisionItemCerradaException.class);
    }

    @Test
    void debeLanzarFichaNoPerteneceAsesor_antesQueDuplicada_cuandoElAsesorNoEsElDeLaFicha() {
        // Arrange — el texto también está duplicado: la propiedad decide primero
        var entrada = entradaCon(UtilUUID.generarNuevoUUID());
        var contexto = contextoCon(EstadoRevision.NUEVA, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, contexto, 1L))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);
    }

    @Test
    void debeLanzarDuplicada_cuandoOtraObservacionDeLaRevisionTieneElMismoTexto() {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var entrada = entradaCon(asesorFicha);
        var contexto = contextoCon(EstadoRevision.EN_PROGRESO, asesorFicha);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, contexto, 1L))
                .isInstanceOf(ObservacionItemDuplicadaException.class);
    }

    private ModificacionObservacionItemDomain entradaCon(UUID asesorFicha) {
        return ModificacionObservacionItemDomain.crear(UtilUUID.generarNuevoUUID(), "Observación válida", asesorFicha);
    }

    private ContextoObservacionItem contextoCon(EstadoRevision estado, UUID asesorDeLaFicha) {
        return new ContextoObservacionItem(
                UtilUUID.generarNuevoUUID(), estado, UtilUUID.generarNuevoUUID(), asesorDeLaFicha);
    }
}
