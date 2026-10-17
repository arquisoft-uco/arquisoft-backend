package com.arquisoft.fichas.application.representantecomite.command.finder.impl;

import com.arquisoft.fichas.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.shared.util.UtilUUID;
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
class RepresentanteComitePorIdFinderImplTest {

    @Mock
    private RepresentanteComiteOutputPort representanteComiteOutputPort;

    @InjectMocks
    private RepresentanteComitePorIdFinderImpl finder;

    @Test
    void debeRetornarElDominioIncluidaLaBaja_cuandoLaFilaExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");
        when(representanteComiteOutputPort.obtenerPorId(id)).thenReturn(Optional.of(new RepresentanteComiteEntity(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn, ocurridoEn)));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getIdentificador()).isEqualTo("20161020123");
        assertThat(resultado.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(resultado.estaEliminado()).isTrue();
    }

    @Test
    void debeRetornarVacio_cuandoLaFilaNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(representanteComiteOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isSameAs(RepresentanteComiteDomain.VACIO);
    }
}
