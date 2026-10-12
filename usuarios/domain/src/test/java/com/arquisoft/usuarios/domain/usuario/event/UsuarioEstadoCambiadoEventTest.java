package com.arquisoft.usuarios.domain.usuario.event;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioEstadoCambiadoEventTest {

    @Test
    void debeExponerLosDatosDelDestinatarioYDelEstado_cuandoSeConstruyeElEvento() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new UsuarioEstadoCambiadoEvent(usuario, "Ana Perez", "ana@uco.edu.co", "INACTIVO", "Inactivo");

        // Assert
        assertThat(evento.getUsuario()).isEqualTo(usuario);
        assertThat(evento.getNombre()).isEqualTo("Ana Perez");
        assertThat(evento.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getEstado()).isEqualTo("INACTIVO");
        assertThat(evento.getEstadoNombre()).isEqualTo("Inactivo");
    }

    @Test
    void debeUsarSuTopicYAsignarIdentidad_cuandoSeConstruyeElEvento() {
        // Act
        var evento = new UsuarioEstadoCambiadoEvent(UtilUUID.generarNuevoUUID(), "Ana Perez", "ana@uco.edu.co",
                "ACTIVO", "Activo");

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("usuarios.usuario.estado_cambiado");
        assertThat(evento.getTipoEvento()).isEqualTo("UsuarioEstadoCambiadoEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
