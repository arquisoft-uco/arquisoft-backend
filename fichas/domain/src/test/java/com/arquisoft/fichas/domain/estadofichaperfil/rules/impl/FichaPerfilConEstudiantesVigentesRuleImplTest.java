package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilSinEstudiantesVigentesException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.IntegrantesVigentesFicha;
import com.arquisoft.shared.message.constant.FichasCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class FichaPerfilConEstudiantesVigentesRuleImplTest {

    private static final String TITULO = "Sistema de gestión de proyectos";

    private final FichaPerfilConEstudiantesVigentesRuleImpl regla = new FichaPerfilConEstudiantesVigentesRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoLaFichaNoTieneEstudiantesVigentes() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var integrantes = new IntegrantesVigentesFicha(fichaPerfil, TITULO, 0);

        // Act
        var excepcion = catchThrowableOfType(FichaPerfilSinEstudiantesVigentesException.class,
                () -> regla.validar(integrantes));

        // Assert
        assertThat(excepcion.getCodigoError()).isEqualTo(FichasCodes.EstadoFichaPerfil.SIN_ESTUDIANTES_VIGENTES);
        assertThat(excepcion.getError().getMessage())
                .contains(TITULO)
                .doesNotContain(fichaPerfil.toString());
    }

    @Test
    void debePasar_cuandoLaFichaTieneUnEstudianteVigente() {
        // Arrange
        var integrantes = new IntegrantesVigentesFicha(UUID.randomUUID(), TITULO, 1);

        // Act & Assert
        assertThatCode(() -> regla.validar(integrantes)).doesNotThrowAnyException();
    }
}
