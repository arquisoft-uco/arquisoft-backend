package com.arquisoft.fichas.domain.fichaperfil.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorFichaCambiadoEventTest {

    @Test
    void debeExponerLosDatosDelAsesor_cuandoSeConstruyeElEvento() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID asesorId = UUID.randomUUID();

        // Act
        AsesorFichaCambiadoEvent evento = new AsesorFichaCambiadoEvent(
                fichaId, "Sistema de gestión", asesorId, "Ana Gomez", "ana.gomez@soyuco.edu.co");

        // Assert
        assertThat(evento.getFichaPerfilId()).isEqualTo(fichaId);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getAsesorFichaId()).isEqualTo(asesorId);
        assertThat(evento.getAsesorNombre()).isEqualTo("Ana Gomez");
        assertThat(evento.getAsesorEmail()).isEqualTo("ana.gomez@soyuco.edu.co");
    }

    @Test
    void debeUsarElTopicDeTresSegmentos_cuandoSeConstruyeElEvento() {
        // Act — el constructor de DomainEvent rechaza un topic que no tenga 3 segmentos
        AsesorFichaCambiadoEvent evento = new AsesorFichaCambiadoEvent(
                UUID.randomUUID(), "Titulo", UUID.randomUUID(), "Ana", "ana@soyuco.edu.co");

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("fichas.ficha_perfil.asesor_cambiado");
        assertThat(evento.getTipoEvento()).isEqualTo("AsesorFichaCambiadoEvent");
    }

    @Test
    void debeAsignarIdYMomentoDeOcurrencia_cuandoSeConstruyeElEvento() {
        // Act
        AsesorFichaCambiadoEvent evento = new AsesorFichaCambiadoEvent(
                UUID.randomUUID(), "Titulo", UUID.randomUUID(), "Ana", "ana@soyuco.edu.co");

        // Assert — el idEvento es la clave de idempotencia del consumidor
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
