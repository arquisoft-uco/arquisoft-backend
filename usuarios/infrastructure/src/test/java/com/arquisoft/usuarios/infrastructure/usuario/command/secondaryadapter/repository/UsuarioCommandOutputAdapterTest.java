package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(MockitoExtension.class)
class UsuarioCommandOutputAdapterTest {

    private static final String EMAIL = "ana.gomez@soyuco.edu.co";

    @Mock
    private AppLogger logger;

    @InjectMocks
    private UsuarioCommandOutputAdapter adapter;

    @Test
    void debeNoLanzar_cuandoGuardaUnUsuario() {
        // Arrange
        var usuario = new UsuarioEntity(UUID.randomUUID(), EMAIL, "estudiante");

        // Act & Assert
        assertThatCode(() -> adapter.guardar(usuario)).doesNotThrowAnyException();
    }

    @Test
    void debeReportarNoExistente_siempre_porqueNoConsultaLaTabla() {
        // Act & Assert
        assertThat(adapter.existePorEmail(EMAIL)).isFalse();
    }
}
