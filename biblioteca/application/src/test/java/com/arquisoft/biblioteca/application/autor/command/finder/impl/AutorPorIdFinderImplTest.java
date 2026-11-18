package com.arquisoft.biblioteca.application.autor.command.finder.impl;

import com.arquisoft.biblioteca.application.autor.command.secondaryport.AutorOutputPort;
import com.arquisoft.biblioteca.application.autor.command.secondaryport.entity.AutorEntity;
import com.arquisoft.biblioteca.domain.autor.AutorDomain;
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
class AutorPorIdFinderImplTest {

    @Mock
    private AutorOutputPort autorOutputPort;

    @InjectMocks
    private AutorPorIdFinderImpl finder;

    @Test
    void debeRetornarDomainReconstruido_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");
        var eliminadoEn = Instant.parse("2026-09-25T10:00:00Z");
        when(autorOutputPort.obtenerPorId(id)).thenReturn(Optional.of(new AutorEntity(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn, eliminadoEn)));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getIdentificador()).isEqualTo("20161020123");
        assertThat(resultado.getNombre()).isEqualTo("Ana Perez");
        assertThat(resultado.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(resultado.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(resultado.getEliminadoEn()).isEqualTo(eliminadoEn);
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(autorOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isSameAs(AutorDomain.VACIO);
    }
}
