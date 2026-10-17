package com.arquisoft.repositorio_artefactos.application.administrador.command.finder.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.AdministradorOutputPort;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.entity.AdministradorEntity;
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
class AdministradorPorIdFinderImplTest {

    @Mock
    private AdministradorOutputPort administradorOutputPort;

    @InjectMocks
    private AdministradorPorIdFinderImpl finder;

    @Test
    void debeDelegarEnElOutputPort_enAdministradorPorIdFinder() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entity = new AdministradorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now(), null);
        when(administradorOutputPort.obtenerPorId(id)).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.getId()).isEqualTo(id);
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(administradorOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isEqualTo(AdministradorDomain.VACIO);
    }
}
