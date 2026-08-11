package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.fichaperfil.secondaryport.FichaPerfilOutputPort;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FichaPerfilExisteRuleImplTest {

    @Test
    void debeLanzarExcepcion_cuandoFichaNoExiste() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();
        var regla = new FichaPerfilExisteRuleImpl(new FichaPerfilOutputPortStub(false));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(fichaPerfil))
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(fichaPerfil.toString());
    }

    @Test
    void debePasar_cuandoFichaExiste() {
        // Arrange
        var regla = new FichaPerfilExisteRuleImpl(new FichaPerfilOutputPortStub(true));

        // Act & Assert
        assertThatCode(() -> regla.validar(UUID.randomUUID())).doesNotThrowAnyException();
    }

    /** fichas:domain no tiene Mockito en el classpath de test: el doble se escribe a mano. */
    private record FichaPerfilOutputPortStub(boolean existe) implements FichaPerfilOutputPort {

        @Override
        public boolean existePorId(UUID id) {
            return existe;
        }

        @Override
        public void registrarFicha(FichaPerfilDomain ficha) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void actualizarTitulo(UUID fichaPerfil, String tituloProyecto) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void actualizarAsesor(UUID fichaPerfil, UUID nuevoAsesorFicha) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<FichaPerfilDomain> buscarPorId(UUID id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean existePorTituloProyecto(String titulo) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean existeTituloEnOtraFicha(UUID fichaPerfil, String titulo) {
            throw new UnsupportedOperationException();
        }
    }
}
