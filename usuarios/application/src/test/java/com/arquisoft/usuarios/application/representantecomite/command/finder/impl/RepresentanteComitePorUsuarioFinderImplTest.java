package com.arquisoft.usuarios.application.representantecomite.command.finder.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.usuarios.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepresentanteComitePorUsuarioFinderImplTest {

    @Mock
    private RepresentanteComiteOutputPort representanteComiteOutputPort;

    @InjectMocks
    private RepresentanteComitePorUsuarioFinderImpl finder;

    @Test
    void debeRetornarElDominio_cuandoElRepresentanteExiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        when(representanteComiteOutputPort.obtenerPorUsuario(usuario))
                .thenReturn(Optional.of(new RepresentanteComiteEntity(usuario, eliminadoEn)));

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getUsuario()).isEqualTo(usuario);
        assertThat(resultado.getEliminadoEn()).isEqualTo(eliminadoEn);
    }

    @Test
    void debeRetornarVacio_cuandoElRepresentanteNoExiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(representanteComiteOutputPort.obtenerPorUsuario(usuario)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado).isSameAs(RepresentanteComiteDomain.VACIO);
    }
}
