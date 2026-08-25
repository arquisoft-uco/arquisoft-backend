package com.arquisoft.usuarios.domain.representantecomitecurriculum;

import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.event.RepresentanteComiteCurriculumAgregadoEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepresentanteComiteCurriculumDomainTest {

    @Test
    void debeCrearRepresentante_cuandoUsuarioValido() {
        // Arrange
        UUID usuario = UUID.randomUUID();

        // Act
        var representante = RepresentanteComiteCurriculumDomain.crear(usuario);

        // Assert
        assertThat(representante).isNotNull();
        assertThat(representante.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoUsuarioNulo() {
        // Arrange
        UUID usuarioNulo = null;

        // Act / Assert
        assertThatThrownBy(() -> RepresentanteComiteCurriculumDomain.crear(usuarioNulo))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("usuario");
    }

    @Test
    void debeReconstruirRepresentante_sinValidarNiEmitirEvento() {
        // Arrange
        UUID usuario = UUID.randomUUID();

        // Act
        var representante = RepresentanteComiteCurriculumDomain.reconstruir(usuario);

        // Assert
        assertThat(representante).isNotNull();
        assertThat(representante.getUsuario()).isEqualTo(usuario);
        assertThat(representante.extraerEventosSinPublicar()).isEmpty();
    }

    @Test
    void debeEmitirEventoYLimpiarlo_cuandoSeExtraen() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        var representante = RepresentanteComiteCurriculumDomain.crear(usuario);

        // Act
        var eventos = representante.extraerEventosSinPublicar();

        // Assert
        assertThat(eventos).hasSize(1);
        assertThat(eventos.get(0)).isInstanceOf(RepresentanteComiteCurriculumAgregadoEvent.class);

        var evento = (RepresentanteComiteCurriculumAgregadoEvent) eventos.get(0);
        assertThat(evento.getRepresentanteId()).isEqualTo(usuario);
        assertThat(evento.getUsuarioId()).isEqualTo(usuario);

        // Verificar que la lista se limpia
        assertThat(representante.extraerEventosSinPublicar()).isEmpty();
    }
}
