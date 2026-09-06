package com.arquisoft.fichas.domain.fichaperfil.event;

import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesor;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FichaPerfilRegistradaEventTest {

    private static final ContactoAsesor CARLOS =
            new ContactoAsesor("Carlos Ruiz", "carlos.ruiz@soyuco.edu.co");

    private static FichaPerfilRegistradaEvent evento() {
        return new FichaPerfilRegistradaEvent(
                UUID.randomUUID(), "Sistema de gestión", UUID.randomUUID(), CARLOS);
    }

    @Test
    void debeExponerLosDatosDelAsesor_cuandoSeConstruyeElEvento() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID asesorId = UUID.randomUUID();

        // Act
        FichaPerfilRegistradaEvent evento = new FichaPerfilRegistradaEvent(
                fichaId, "Sistema de gestión", asesorId, CARLOS);

        // Assert
        assertThat(evento.getFichaPerfilId()).isEqualTo(fichaId);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getAsesorFichaId()).isEqualTo(asesorId);
        assertThat(evento.getAsesor()).isEqualTo(CARLOS);
    }

    @Test
    void debeUsarElTopicDeTresSegmentos_cuandoSeConstruyeElEvento() {
        // Act — el constructor de DomainEvent rechaza un topic que no tenga 3 segmentos
        FichaPerfilRegistradaEvent evento = evento();

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("fichas.ficha_perfil.registrada");
        assertThat(evento.getTipoEvento()).isEqualTo("FichaPerfilRegistradaEvent");
    }

    @Test
    void debeAsignarIdYMomentoDeOcurrencia_cuandoSeConstruyeElEvento() {
        // Act
        FichaPerfilRegistradaEvent evento = evento();

        // Assert — el idEvento es la clave de idempotencia del consumidor
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
