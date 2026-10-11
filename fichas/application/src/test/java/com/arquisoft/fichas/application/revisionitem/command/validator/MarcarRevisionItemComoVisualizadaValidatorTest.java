package com.arquisoft.fichas.application.revisionitem.command.validator;

import com.arquisoft.fichas.application.revisionitem.command.validator.impl.MarcarRevisionItemComoVisualizadaValidatorImpl;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemCerradaException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MarcarRevisionItemComoVisualizadaValidatorTest {

    private final MarcarRevisionItemComoVisualizadaValidatorImpl validator =
            new MarcarRevisionItemComoVisualizadaValidatorImpl();

    private final UUID revisionItem = UtilUUID.generarNuevoUUID();
    private final UUID estudiante = UtilUUID.generarNuevoUUID();
    private final UUID fichaPerfil = UtilUUID.generarNuevoUUID();

    @Test
    void debePasar_cuandoLaRevisionExisteElEstudianteEsPropietarioYNoEstaCerrada() {
        // Act & Assert
        assertThatCode(() -> validator.validar(revisionItem, estudiante, fichaPerfil,
                true, true, EstadoRevision.NUEVA.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarRevisionNoEncontrada_cuandoLaRevisionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(revisionItem, estudiante,
                UtilUUID.obtenerUUIDPorDefecto(), false, false, EstadoRevision.VACIO.getId()))
                .isInstanceOf(RevisionItemNoEncontradoException.class);
    }

    @Test
    void debeLanzarFichaNoPropietario_cuandoElEstudianteNoEstaVinculado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(revisionItem, estudiante, fichaPerfil,
                true, false, EstadoRevision.NUEVA.getId()))
                .isInstanceOf(FichaNoPropietarioException.class);
    }

    @Test
    void debeLanzarRevisionCerrada_cuandoLaRevisionEstaCerrada() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(revisionItem, estudiante, fichaPerfil,
                true, true, EstadoRevision.CERRADA.getId()))
                .isInstanceOf(RevisionItemCerradaException.class);
    }

    @Test
    void debeGanarLaExistencia_cuandoLaRevisionNoExisteYElEstudianteNoEsPropietario() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(revisionItem, estudiante, fichaPerfil,
                false, false, EstadoRevision.CERRADA.getId()))
                .isInstanceOf(RevisionItemNoEncontradoException.class);
    }

    @Test
    void debeGanarLaPropiedad_cuandoElEstudianteNoEsPropietarioYLaRevisionEstaCerrada() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(revisionItem, estudiante, fichaPerfil,
                true, false, EstadoRevision.CERRADA.getId()))
                .isInstanceOf(FichaNoPropietarioException.class);
    }
}
