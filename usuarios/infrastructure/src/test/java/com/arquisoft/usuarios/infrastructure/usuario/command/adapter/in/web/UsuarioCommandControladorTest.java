package com.arquisoft.usuarios.infrastructure.usuario.command.adapter.in.web;

import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;
import com.arquisoft.usuarios.application.usuario.command.model.CrearUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.interactor.CrearUsuarioInteractor;
import com.arquisoft.usuarios.infrastructure.usuario.command.adapter.in.web.dto.CrearUsuarioRequestDTO;
import com.arquisoft.usuarios.infrastructure.usuario.command.adapter.in.web.dto.CrearUsuarioResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioCommandControladorTest {

    @Mock
    private CrearUsuarioInteractor crearUsuarioInteractor;

    @InjectMocks
    private UsuarioCommandControlador adapter;

    @Test
    void debeRetornar201_cuandoRequestValido() {
        // Arrange
        UUID expectedId = UUID.randomUUID();
        CrearUsuarioRequestDTO request = new CrearUsuarioRequestDTO(
                "test@example.com",
                CrearUsuarioRequestDTO.RolUsuarioDTO.ESTUDIANTE
        );
        when(crearUsuarioInteractor.ejecutar(any(CrearUsuarioCommand.class))).thenReturn(expectedId);

        // Act
        ResponseEntity<CrearUsuarioResponseDTO> response = adapter.crear(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(expectedId);
        assertThat(response.getBody().getEmail()).isEqualTo("test@example.com");
        assertThat(response.getBody().getRol()).isEqualTo("estudiante");
    }

    @Test
    void debeInvocarUseCase_cuandoCrear() {
        // Arrange
        UUID expectedId = UUID.randomUUID();
        CrearUsuarioRequestDTO request = new CrearUsuarioRequestDTO(
                "admin@example.com",
                CrearUsuarioRequestDTO.RolUsuarioDTO.ADMINISTRADOR
        );
        when(crearUsuarioInteractor.ejecutar(any(CrearUsuarioCommand.class))).thenReturn(expectedId);
        ArgumentCaptor<CrearUsuarioCommand> captor = ArgumentCaptor.forClass(CrearUsuarioCommand.class);

        // Act
        adapter.crear(request);

        // Assert
        verify(crearUsuarioInteractor).ejecutar(captor.capture());
        CrearUsuarioCommand command = captor.getValue();
        assertThat(command.email()).isEqualTo("admin@example.com");
        assertThat(command.rol()).isEqualTo(UsuarioRole.ADMINISTRADOR);
    }
}
