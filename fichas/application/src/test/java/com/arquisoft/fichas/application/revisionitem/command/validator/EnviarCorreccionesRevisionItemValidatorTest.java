package com.arquisoft.fichas.application.revisionitem.command.validator;

import com.arquisoft.fichas.application.revisionitem.command.validator.impl.EnviarCorreccionesRevisionItemValidatorImpl;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.revisionitem.EnvioCorreccionesRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemCerradaException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemConObservacionesPendientesException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEncontradoException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEnProgresoException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemSinObservacionesException;
import com.arquisoft.fichas.domain.revisionitem.model.ObservacionesRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.model.PertenenciaRevisionItem;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnviarCorreccionesRevisionItemValidatorTest {

    private final EnviarCorreccionesRevisionItemValidatorImpl validator =
            new EnviarCorreccionesRevisionItemValidatorImpl();

    private final UUID revisionItem = UtilUUID.generarNuevoUUID();
    private final UUID estudiante = UtilUUID.generarNuevoUUID();
    private final UUID fichaPerfilId = UtilUUID.generarNuevoUUID();
    private final UUID asesorFichaId = UtilUUID.generarNuevoUUID();
    private final EnvioCorreccionesRevisionItemDomain entrada =
            EnvioCorreccionesRevisionItemDomain.crear(revisionItem, estudiante);
    private final FichaPerfilDomain ficha = FichaPerfilDomain.reconstruir(fichaPerfilId, "Proyecto", asesorFichaId);
    private final AsesorFichaDomain asesor = AsesorFichaDomain.reconstruir(
            asesorFichaId, "123", "Luis Perez", "luis@uco.edu.co", Instant.now(), UtilFecha.VACIO);
    private final ObservacionesRevisionItem observacionesAtendidas =
            new ObservacionesRevisionItem(revisionItem, 3, 0);

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Arrange
        var pertenencia = pertenencia(true, EstadoRevision.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada, pertenencia, ficha, asesor, observacionesAtendidas))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarRevisionNoEncontrada_cuandoLaRevisionNoExisteAunqueTodoLoDemasFalle() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, PertenenciaRevisionItem.VACIO,
                FichaPerfilDomain.VACIO, AsesorFichaDomain.VACIO, new ObservacionesRevisionItem(revisionItem, 0, 0)))
                .isInstanceOf(RevisionItemNoEncontradoException.class);
    }

    @Test
    void debeLanzarFichaNoEncontrada_cuandoLaFichaNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, pertenencia(false, EstadoRevision.CERRADA),
                FichaPerfilDomain.VACIO, AsesorFichaDomain.VACIO, observacionesAtendidas))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);
    }

    @Test
    void debeLanzarAsesorNoEncontrado_cuandoElAsesorDeLaFichaNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, pertenencia(false, EstadoRevision.CERRADA),
                ficha, AsesorFichaDomain.VACIO, observacionesAtendidas))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);
    }

    @Test
    void debeLanzarFichaNoPropietario_cuandoElEstudianteNoEsPropietarioAunqueLaRevisionEsteCerrada() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, pertenencia(false, EstadoRevision.CERRADA),
                ficha, asesor, observacionesAtendidas))
                .isInstanceOf(FichaNoPropietarioException.class);
    }

    @Test
    void debeLanzarRevisionCerrada_cuandoLaRevisionEstaCerrada() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, pertenencia(true, EstadoRevision.CERRADA),
                ficha, asesor, new ObservacionesRevisionItem(revisionItem, 0, 0)))
                .isInstanceOf(RevisionItemCerradaException.class);
    }

    @Test
    void debeLanzarNoEnProgreso_cuandoLaRevisionEstaNuevaVisualizadaOConCorreccionDisponible() {
        // Arrange
        var estados = new EstadoRevision[] {
            EstadoRevision.NUEVA, EstadoRevision.VISUALIZADA, EstadoRevision.CORRECCION_DISPONIBLE
        };

        // Act & Assert
        for (var estado : estados) {
            assertThatThrownBy(() -> validator.validar(entrada, pertenencia(true, estado),
                    ficha, asesor, new ObservacionesRevisionItem(revisionItem, 0, 0)))
                    .isInstanceOf(RevisionItemNoEnProgresoException.class);
        }
    }

    @Test
    void debeLanzarSinObservaciones_cuandoLaRevisionEnProgresoNoTieneObservaciones() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, pertenencia(true, EstadoRevision.EN_PROGRESO),
                ficha, asesor, new ObservacionesRevisionItem(revisionItem, 0, 0)))
                .isInstanceOf(RevisionItemSinObservacionesException.class);
    }

    @Test
    void debeLanzarConPendientes_cuandoAlgunaObservacionSigueEnPendiente() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, pertenencia(true, EstadoRevision.EN_PROGRESO),
                ficha, asesor, new ObservacionesRevisionItem(revisionItem, 3, 1)))
                .isInstanceOf(RevisionItemConObservacionesPendientesException.class);
    }

    private PertenenciaRevisionItem pertenencia(boolean esPropietario, EstadoRevision estado) {
        return new PertenenciaRevisionItem(fichaPerfilId, esPropietario, estado);
    }
}
