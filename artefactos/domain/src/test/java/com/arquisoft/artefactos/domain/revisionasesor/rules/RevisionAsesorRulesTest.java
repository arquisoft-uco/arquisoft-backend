package com.arquisoft.artefactos.domain.revisionasesor.rules;

import com.arquisoft.artefactos.domain.revisionasesor.exception.EstadoDestinoRevisionAsesorNoPermitidoException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.EstudianteNoParticipaArtefactoException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorCerradaException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEncontradaException;
import com.arquisoft.artefactos.domain.revisionasesor.model.DestinoCambioEstado;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ExistenciaRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ParticipacionEstudianteArtefacto;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.EstadoDestinoRevisionAsesorPermitidoRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.EstudianteParticipaArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorExisteRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorNoCerradaRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionAsesorRulesTest {

    @Test
    void debeLanzar_cuandoLaRevisionNoExiste() {
        // Arrange
        var regla = new RevisionAsesorExisteRuleImpl();
        var entrada = new ExistenciaRevisionAsesor(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionAsesorNoEncontradaException.class,
                        ex -> assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.NO_ENCONTRADA));
    }

    @Test
    void noDebeLanzar_cuandoLaRevisionExiste() {
        // Arrange
        var regla = new RevisionAsesorExisteRuleImpl();
        var entrada = new ExistenciaRevisionAsesor(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoElEstudianteNoParticipa() {
        // Arrange
        var regla = new EstudianteParticipaArtefactoRuleImpl();
        var entrada = new ParticipacionEstudianteArtefacto(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EstudianteNoParticipaArtefactoException.class,
                        ex -> assertThat(ex.getCodigoError())
                                .isEqualTo(ArtefactosCodes.RevisionAsesor.ESTUDIANTE_NO_PARTICIPA_ARTEFACTO));
    }

    @Test
    void noDebeLanzar_cuandoElEstudianteParticipa() {
        // Arrange
        var regla = new EstudianteParticipaArtefactoRuleImpl();
        var entrada = new ParticipacionEstudianteArtefacto(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaRevisionEstaCerrada() {
        // Arrange
        var regla = new RevisionAsesorNoCerradaRuleImpl();
        var entrada = new EstadoActualRevisionAsesor(UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.CERRADA);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionAsesorCerradaException.class,
                        ex -> assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.CERRADA));
    }

    @Test
    void noDebeLanzar_cuandoLaRevisionNoEstaCerrada() {
        // Arrange
        var regla = new RevisionAsesorNoCerradaRuleImpl();
        var entrada = new EstadoActualRevisionAsesor(UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.RESUELTA);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoElDestinoEsCerrada() {
        // Arrange
        var regla = new EstadoDestinoRevisionAsesorPermitidoRuleImpl();
        var entrada = new DestinoCambioEstado(UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.CERRADA);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EstadoDestinoRevisionAsesorNoPermitidoException.class,
                        ex -> assertThat(ex.getCodigoError())
                                .isEqualTo(ArtefactosCodes.RevisionAsesor.ESTADO_DESTINO_NO_PERMITIDO));
    }

    @Test
    void noDebeLanzar_cuandoElDestinoNoEsCerrada() {
        // Arrange
        var regla = new EstadoDestinoRevisionAsesorPermitidoRuleImpl();
        var entrada = new DestinoCambioEstado(UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
