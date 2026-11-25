package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.application.fichaperfil.command.validator.impl.ModificarFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilNoEnConstruccionException;

import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.domain.fichaperfil.ModificacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarFichaPerfilValidatorTest {

    private final ModificarFichaPerfilValidatorImpl validator = new ModificarFichaPerfilValidatorImpl();

    private ModificacionFichaPerfilDomain modificacion(UUID ficha, UUID estudiante, String titulo) {
        return ModificacionFichaPerfilDomain.crear(ficha, titulo, estudiante);
    }

    @Test
    void debePasar_cuandoEsPropietarioYElTituloEstaLibre() {
        // Arrange
        var modificacion = modificacion(UUID.randomUUID(), UUID.randomUUID(), "Titulo nuevo");

        // Act / Assert
        assertThatCode(() -> validator.validar(modificacion, true, enConstruccion(), false)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoPropietario_cuandoElEstudianteNoEsDuenoDeLaFicha() {
        // Arrange
        var ficha = UUID.randomUUID();
        var modificacion = modificacion(ficha, UUID.randomUUID(), "Titulo nuevo");

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(modificacion, false, enConstruccion(), false))
                .isInstanceOf(FichaNoPropietarioException.class)
                .hasMessageContaining(ficha.toString());
    }

    @Test
    void debeLanzarTituloDuplicado_cuandoElTituloYaEstaTomado() {
        // Arrange
        var modificacion = modificacion(UUID.randomUUID(), UUID.randomUUID(), "Titulo tomado");

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(modificacion, true, enConstruccion(), true))
                .isInstanceOf(FichaTituloDuplicadoException.class)
                .hasMessageContaining("Titulo tomado");
    }

    @Test
    void debeReportarPrimeroLaPropiedad_cuandoAmbasReglasFallan() {
        // Arrange — el orden es parte del contrato: primero propiedad, despues unicidad
        var modificacion = modificacion(UUID.randomUUID(), UUID.randomUUID(), "Titulo tomado");

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(modificacion, false, enConstruccion(), true))
                .isInstanceOf(FichaNoPropietarioException.class);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoFicha.class, names = {
            "DISPONIBLE_PARA_EVALUACION", "DESCARTADA", "APROBADA", "APROBADA_CON_OBSERVACIONES", "NO_APROBADA"})
    void debeLanzarNoEnConstruccion_cuandoLaFichaNoEstaEnConstruccion(EstadoFicha noPermitido) {
        // Arrange
        var ficha = UUID.randomUUID();
        var modificacion = modificacion(ficha, UUID.randomUUID(), "Titulo nuevo");
        var estado = EstadoFichaPerfilDomain.reconstruir(UUID.randomUUID(), ficha, noPermitido, Instant.now());

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(modificacion, true, estado, false))
                .isInstanceOf(FichaPerfilNoEnConstruccionException.class);
    }

    @Test
    void debeLanzarNoEnConstruccion_antesQueTituloDuplicado() {
        // Arrange
        var ficha = UUID.randomUUID();
        var modificacion = modificacion(ficha, UUID.randomUUID(), "Titulo tomado");
        var estado = EstadoFichaPerfilDomain.reconstruir(
                UUID.randomUUID(), ficha, EstadoFicha.DISPONIBLE_PARA_EVALUACION, Instant.now());

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(modificacion, true, estado, true))
                .isInstanceOf(FichaPerfilNoEnConstruccionException.class);
    }

    @Test
    void debeLanzarEstadoNoEncontrado_cuandoLaFichaNoTieneEstado() {
        // Arrange
        var modificacion = modificacion(UUID.randomUUID(), UUID.randomUUID(), "Titulo nuevo");

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(modificacion, true, EstadoFichaPerfilDomain.VACIO, false))
                .isInstanceOf(EstadoFichaPerfilNoEncontradoException.class);
    }

    private EstadoFichaPerfilDomain enConstruccion() {
        return EstadoFichaPerfilDomain.crear(UUID.randomUUID());
    }
}
