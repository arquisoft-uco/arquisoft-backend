package com.arquisoft.fichas.application.observacionitem.command.validator.impl;

import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.observacionitem.CierreObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemNoEnProgresoException;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemNoEncontradaException;
import com.arquisoft.fichas.domain.observacionitem.model.ContextoObservacionItem;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemCerradaException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoCorreccionDisponibleException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CerrarObservacionItemValidatorImplTest {

    private final CerrarObservacionItemValidatorImpl validator = new CerrarObservacionItemValidatorImpl();

    @Test
    void debeNoLanzar_cuandoTodasLasReglasSeCumplen() {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var entrada = entradaCon(asesorFicha);
        var contexto = contextoCon(EstadoRevision.CORRECCION_DISPONIBLE, asesorFicha, EstadoObservacionRevision.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada, contexto, fichaCon(asesorFicha))).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoElContextoEsVacio() {
        // Arrange
        var entrada = entradaCon(UtilUUID.generarNuevoUUID());
        var ficha = fichaCon(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, ContextoObservacionItem.VACIO, ficha))
                .isInstanceOf(ObservacionItemNoEncontradaException.class);
    }

    @Test
    void debeLanzarFichaNoEncontrada_cuandoLaFichaEsVacia() {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var entrada = entradaCon(asesorFicha);
        var contexto = contextoCon(EstadoRevision.CORRECCION_DISPONIBLE, asesorFicha, EstadoObservacionRevision.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, contexto, FichaPerfilDomain.VACIO))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);
    }

    @Test
    void debeLanzarRevisionCerrada_cuandoLaRevisionYaEstaCerrada() {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var entrada = entradaCon(asesorFicha);
        var contexto = contextoCon(EstadoRevision.CERRADA, asesorFicha, EstadoObservacionRevision.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, contexto, fichaCon(asesorFicha)))
                .isInstanceOf(RevisionItemCerradaException.class);
    }

    @Test
    void debeLanzarNoCorreccionDisponible_cuandoLaRevisionEstaEnProgreso() {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var entrada = entradaCon(asesorFicha);
        var contexto = contextoCon(EstadoRevision.EN_PROGRESO, asesorFicha, EstadoObservacionRevision.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, contexto, fichaCon(asesorFicha)))
                .isInstanceOf(RevisionItemNoCorreccionDisponibleException.class);
    }

    @Test
    void debeLanzarFichaNoPerteneceAsesor_cuandoElSolicitanteNoEsElAsesorDeLaFicha() {
        // Arrange
        var entrada = entradaCon(UtilUUID.generarNuevoUUID());
        var asesorDeLaFicha = UtilUUID.generarNuevoUUID();
        var contexto = contextoCon(EstadoRevision.CORRECCION_DISPONIBLE, asesorDeLaFicha, EstadoObservacionRevision.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, contexto, fichaCon(asesorDeLaFicha)))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);
    }

    @Test
    void debeLanzarNoEnProgreso_cuandoLaObservacionEstaPendiente() {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var entrada = entradaCon(asesorFicha);
        var contexto = contextoCon(EstadoRevision.CORRECCION_DISPONIBLE, asesorFicha, EstadoObservacionRevision.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, contexto, fichaCon(asesorFicha)))
                .isInstanceOf(ObservacionItemNoEnProgresoException.class);
    }

    @Test
    void debeLanzarNoEnProgreso_cuandoLaObservacionYaEstaCerrada() {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var entrada = entradaCon(asesorFicha);
        var contexto = contextoCon(EstadoRevision.CORRECCION_DISPONIBLE, asesorFicha, EstadoObservacionRevision.CERRADO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, contexto, fichaCon(asesorFicha)))
                .isInstanceOf(ObservacionItemNoEnProgresoException.class);
    }

    @Test
    void debeRespetarPrioridad_cuandoFallanVariasReglas() {
        // Arrange: gana la revision cerrada sobre la propiedad, y la propiedad sobre el estado de la observacion
        var asesorDeLaFicha = UtilUUID.generarNuevoUUID();
        var ficha = fichaCon(asesorDeLaFicha);
        var entrada = entradaCon(UtilUUID.generarNuevoUUID());
        var cerradaYAjeno = contextoCon(EstadoRevision.CERRADA, asesorDeLaFicha, EstadoObservacionRevision.PENDIENTE);
        var ajenoYPendiente = contextoCon(EstadoRevision.CORRECCION_DISPONIBLE, asesorDeLaFicha,
                EstadoObservacionRevision.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, cerradaYAjeno, ficha))
                .isInstanceOf(RevisionItemCerradaException.class);
        assertThatThrownBy(() -> validator.validar(entrada, ajenoYPendiente, ficha))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);
    }

    private CierreObservacionItemDomain entradaCon(UUID asesorFicha) {
        return CierreObservacionItemDomain.crear(UtilUUID.generarNuevoUUID(), asesorFicha);
    }

    private FichaPerfilDomain fichaCon(UUID asesorFicha) {
        return FichaPerfilDomain.crear("Sistema de gestión", asesorFicha);
    }

    private ContextoObservacionItem contextoCon(EstadoRevision estadoRevision, UUID asesorDeLaFicha,
                                                EstadoObservacionRevision estadoObservacion) {
        return new ContextoObservacionItem(
                UtilUUID.generarNuevoUUID(), estadoRevision, UtilUUID.generarNuevoUUID(), asesorDeLaFicha,
                estadoObservacion);
    }
}
