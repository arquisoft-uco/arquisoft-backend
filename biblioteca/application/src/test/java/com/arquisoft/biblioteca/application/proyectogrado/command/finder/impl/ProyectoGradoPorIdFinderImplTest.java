package com.arquisoft.biblioteca.application.proyectogrado.command.finder.impl;

import com.arquisoft.biblioteca.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.biblioteca.domain.proyectogrado.ProyectoGradoDomain;
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
class ProyectoGradoPorIdFinderImplTest {

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;

    @InjectMocks
    private ProyectoGradoPorIdFinderImpl finder;

    @Test
    void debeRetornarDomainReconstruido_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-15T10:00:00Z");
        when(proyectoGradoOutputPort.obtenerPorId(id))
                .thenReturn(Optional.of(new ProyectoGradoEntity(id, "Titulo", ocurridoEn)));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getTitulo()).isEqualTo("Titulo");
        assertThat(resultado.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(proyectoGradoOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isSameAs(ProyectoGradoDomain.VACIO);
    }
}
