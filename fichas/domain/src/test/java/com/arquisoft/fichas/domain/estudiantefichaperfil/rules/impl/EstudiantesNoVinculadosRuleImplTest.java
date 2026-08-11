package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantesNoVinculadosRuleImplTest {

    private static final UUID FICHA_PERFIL = UUID.randomUUID();

    @Test
    void debeLanzarExcepcion_cuandoEstudianteYaEstaVinculado() {
        // Arrange
        UUID vinculado = UUID.randomUUID();
        var regla = new EstudiantesNoVinculadosRuleImpl(new PortStub(Set.of(vinculado)));
        var relaciones = EstudianteFichaPerfilDomain.crear(FICHA_PERFIL, List.of(UUID.randomUUID(), vinculado));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(relaciones))
                .isInstanceOf(EstudianteDuplicadoException.class)
                .hasMessageContaining(vinculado.toString());
    }

    @Test
    void debePasar_cuandoNingunEstudianteEstaVinculado() {
        // Arrange
        var regla = new EstudiantesNoVinculadosRuleImpl(new PortStub(Set.of()));
        var relaciones = EstudianteFichaPerfilDomain.crear(
                FICHA_PERFIL, List.of(UUID.randomUUID(), UUID.randomUUID()));

        // Act & Assert
        assertThatCode(() -> regla.validar(relaciones)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoListaEsNula() {
        // Arrange
        var regla = new EstudiantesNoVinculadosRuleImpl(new PortStub(Set.of()));

        // Act & Assert
        assertThatCode(() -> regla.validar(null))
                .doesNotThrowAnyException();
    }

    /** fichas:domain no tiene Mockito en el classpath de test: el doble se escribe a mano. */
    private record PortStub(Set<UUID> vinculados) implements EstudianteFichaPerfilOutputPort {

        @Override
        public boolean existePorFichaYEstudiante(UUID fichaPerfilId, UUID estudianteId) {
            return vinculados.contains(estudianteId);
        }

        @Override
        public void vincularEstudiante(EstudianteFichaPerfilDomain relacion) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long contarPorFichaPerfilId(UUID fichaPerfilId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void desvincularEstudiante(UUID fichaPerfilId, UUID estudianteId) {
            throw new UnsupportedOperationException();
        }
    }
}
