package com.arquisoft.fichas.domain.fichaperfil.event;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FichaPerfilRegistradaEventTest {

    private static final DestinatarioEvento ANA =
            new DestinatarioEvento("Ana Gomez", "ana.gomez@soyuco.edu.co");
    private static final DestinatarioEvento LUIS =
            new DestinatarioEvento("Luis Diaz", "luis.diaz@soyuco.edu.co");

    private static FichaPerfilRegistradaEvent eventoCon(List<DestinatarioEvento> estudiantes) {
        return new FichaPerfilRegistradaEvent(
                UUID.randomUUID(), "Sistema de gestión", UUID.randomUUID(),
                "Carlos Ruiz", "carlos.ruiz@soyuco.edu.co", estudiantes);
    }

    @Test
    void debeExponerAlAsesorYALosEstudiantes_cuandoSeConstruyeElEvento() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID asesorId = UUID.randomUUID();

        // Act
        FichaPerfilRegistradaEvent evento = new FichaPerfilRegistradaEvent(
                fichaId, "Sistema de gestión", asesorId,
                "Carlos Ruiz", "carlos.ruiz@soyuco.edu.co", List.of(ANA, LUIS));

        // Assert
        assertThat(evento.getFichaPerfilId()).isEqualTo(fichaId);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getAsesorFichaId()).isEqualTo(asesorId);
        assertThat(evento.getAsesorNombre()).isEqualTo("Carlos Ruiz");
        assertThat(evento.getAsesorEmail()).isEqualTo("carlos.ruiz@soyuco.edu.co");
        assertThat(evento.getEstudiantes()).containsExactly(ANA, LUIS);
    }

    @Test
    void debeUsarElTopicDeTresSegmentos_cuandoSeConstruyeElEvento() {
        // Act — el constructor de DomainEvent rechaza un topic que no tenga 3 segmentos
        FichaPerfilRegistradaEvent evento = eventoCon(List.of(ANA));

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("fichas.ficha_perfil.registrada");
        assertThat(evento.getTipoEvento()).isEqualTo("FichaPerfilRegistradaEvent");
    }

    @Test
    void debeAsignarIdYMomentoDeOcurrencia_cuandoSeConstruyeElEvento() {
        // Act
        FichaPerfilRegistradaEvent evento = eventoCon(List.of(ANA));

        // Assert — el idEvento es la clave de idempotencia del consumidor
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }

    @Test
    void debeAceptarLaListaVacia_cuandoLaFichaNoTieneEstudiantes() {
        // Act
        FichaPerfilRegistradaEvent evento = eventoCon(List.of());

        // Assert
        assertThat(evento.getEstudiantes()).isEmpty();
    }

    @Test
    void debeAislarLaListaDeLaQueRecibio_cuandoQuienLaConstruyeLaModificaDespues() {
        // Arrange
        List<DestinatarioEvento> mutable = new ArrayList<>(List.of(ANA));
        FichaPerfilRegistradaEvent evento = eventoCon(mutable);

        // Act
        mutable.add(LUIS);

        // Assert — el evento viaja al broker; una lista compartida lo dejaria mutar tras publicarse
        assertThat(evento.getEstudiantes()).containsExactly(ANA);
    }

    @Test
    void debeRechazarLaModificacionDeSusEstudiantes_cuandoUnConsumidorLoIntenta() {
        // Arrange
        FichaPerfilRegistradaEvent evento = eventoCon(List.of(ANA));

        // Act & Assert
        assertThatThrownBy(() -> evento.getEstudiantes().add(LUIS))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
