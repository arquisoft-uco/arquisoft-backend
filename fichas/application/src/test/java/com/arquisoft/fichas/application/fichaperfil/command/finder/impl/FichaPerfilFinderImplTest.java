package com.arquisoft.fichas.application.fichaperfil.command.finder.impl;

import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.fichaperfil.secondaryport.FichaPerfilOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FichaPerfilFinderImplTest {

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @InjectMocks
    private FichaPerfilFinderImpl finder;

    @Test
    void debeRetornarLaFicha_cuandoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        FichaPerfilDomain ficha = FichaPerfilDomain.reconstruir(fichaId, "Título de prueba", UUID.randomUUID());
        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(ficha));

        // Act
        FichaPerfilDomain resultado = finder.obtener(fichaId);

        // Assert
        assertThat(resultado).isSameAs(ficha);
    }

    @Test
    void debeLanzarExcepcion_cuandoNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> finder.obtener(fichaId))
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(fichaId.toString());
    }
}
