package com.arquisoft.fichas.domain.estadofichaperfil;

import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ConteoEvaluacionesPorEstado;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ResumenEvaluacionesFicha;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoFichaPerfilDomainTest {

    @Test
    void debeConstruirEntidad_cuandoDatosValidos() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();

        // Act
        EstadoFichaPerfilDomain aggregate = EstadoFichaPerfilDomain.crear(fichaPerfilId);

        // Assert
        assertThat(aggregate).isNotNull();
        assertThat(aggregate.getId()).isNotNull();
        assertThat(aggregate.getFichaPerfil()).isEqualTo(fichaPerfilId);
        assertThat(aggregate.getEstadoFicha()).isEqualTo(EstadoFicha.EN_CONSTRUCCION);
        assertThat(aggregate.getFechaActualizacion()).isNotNull();
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();

        // Act
        EstadoFichaPerfilDomain aggregate = EstadoFichaPerfilDomain.reconstruir(
                id,
                fichaPerfilId,
                EstadoFicha.EN_CONSTRUCCION,
                java.time.Instant.now()
        );

        // Assert
        assertThat(aggregate).isNotNull();
        assertThat(aggregate.getId()).isEqualTo(id);
        assertThat(aggregate.getFichaPerfil()).isEqualTo(fichaPerfilId);
        assertThat(aggregate.getEstadoFicha()).isEqualTo(EstadoFicha.EN_CONSTRUCCION);
    }

    @Test
    void debeLanzarExcepcion_cuandoFichaPerfilIdEsNulo() {
        // Arrange / Act / Assert
        assertThatThrownBy(() -> EstadoFichaPerfilDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.EstadoFichaPerfil.FICHA_PERFIL);
    }

    @Test
    void debeAsignarEstadoEnConstruccion_cuandoCrear() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();

        // Act
        EstadoFichaPerfilDomain aggregate = EstadoFichaPerfilDomain.crear(fichaPerfilId);

        // Assert
        assertThat(aggregate.getEstadoFicha()).isEqualTo(EstadoFicha.EN_CONSTRUCCION);
        assertThat(aggregate.getEstadoFicha().getNombre()).isEqualTo("En Construccion");
    }

    @Test
    void debeGenerarFechaActualizacionAutomatica_cuandoCrear() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();

        // Act
        EstadoFichaPerfilDomain aggregate = EstadoFichaPerfilDomain.crear(fichaPerfilId);

        // Assert
        assertThat(aggregate.getFechaActualizacion()).isNotNull();
        assertThat(aggregate.getFechaActualizacion()).isBefore(java.time.Instant.now().plusSeconds(1));
    }

    @Test
    void debeDerivarNoAprobada_cuandoElCoordinadorNoAcepta() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var resumen = new ResumenEvaluacionesFicha(fichaPerfil, List.of(
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.APROBADA_CON_OBSERVACIONES, 2, 2)));

        // Act
        var estado = EstadoFichaPerfilDomain.crearPorDecision(fichaPerfil, false, resumen);

        // Assert
        assertThat(estado.getEstadoFicha()).isEqualTo(EstadoFicha.NO_APROBADA);
        assertThat(estado.getId()).isNotNull();
        assertThat(estado.getFichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(estado.getFechaActualizacion()).isNotNull();
        assertThat(estado.esVacio()).isFalse();
    }

    @Test
    void debeDerivarAprobada_cuandoAceptaYNingunaEvaluacionVigenteTieneObservaciones() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var resumen = new ResumenEvaluacionesFicha(fichaPerfil, List.of(
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.APROBADA, 2, 0),
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.NO_APROBADA, 1, 0)));

        // Act
        var estado = EstadoFichaPerfilDomain.crearPorDecision(fichaPerfil, true, resumen);

        // Assert
        assertThat(estado.getEstadoFicha()).isEqualTo(EstadoFicha.APROBADA);
    }

    @Test
    void debeDerivarAprobadaConObservaciones_cuandoAceptaYAlgunaEvaluacionVigenteTieneObservaciones() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var resumen = new ResumenEvaluacionesFicha(fichaPerfil, List.of(
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.APROBADA, 1, 0),
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.NO_APROBADA, 1, 1)));

        // Act
        var estado = EstadoFichaPerfilDomain.crearPorDecision(fichaPerfil, true, resumen);

        // Assert
        assertThat(estado.getEstadoFicha()).isEqualTo(EstadoFicha.APROBADA_CON_OBSERVACIONES);
    }

    @Test
    void debeDerivarAprobada_cuandoLasUnicasObservacionesEstanEnEvaluacionesDescartadas() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var resumen = new ResumenEvaluacionesFicha(fichaPerfil, List.of(
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.APROBADA, 1, 0),
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.DESCARTADA, 2, 2)));

        // Act
        var estado = EstadoFichaPerfilDomain.crearPorDecision(fichaPerfil, true, resumen);

        // Assert
        assertThat(estado.getEstadoFicha()).isEqualTo(EstadoFicha.APROBADA);
    }
}
