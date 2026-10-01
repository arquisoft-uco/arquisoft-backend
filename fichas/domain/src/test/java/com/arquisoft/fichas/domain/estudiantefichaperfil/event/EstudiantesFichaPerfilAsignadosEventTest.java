package com.arquisoft.fichas.domain.estudiantefichaperfil.event;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantesFichaPerfilAsignadosEventTest {

    private static final ContactoEstudiante ANA =
            new ContactoEstudiante("Ana Gomez", "ana.gomez@soyuco.edu.co");
    private static final ContactoEstudiante LUIS =
            new ContactoEstudiante("Luis Diaz", "luis.diaz@soyuco.edu.co");

    private static EstudiantesFichaPerfilAsignadosEvent eventoCon(
            List<ContactoEstudiante> estudiantes) {
        return new EstudiantesFichaPerfilAsignadosEvent(
                UUID.randomUUID(), "Sistema de gestión", estudiantes);
    }

    @Test
    void debeExponerLaFichaYLosContactos_cuandoSeConstruyeElEvento() {
        // Arrange
        UUID fichaId = UUID.randomUUID();

        // Act
        EstudiantesFichaPerfilAsignadosEvent evento = new EstudiantesFichaPerfilAsignadosEvent(
                fichaId, "Sistema de gestión", List.of(ANA, LUIS));

        // Assert
        assertThat(evento.getFichaPerfilId()).isEqualTo(fichaId);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getEstudiantes()).containsExactly(ANA, LUIS);
    }

    @Test
    void debeUsarElTopicDeTresSegmentos_cuandoSeConstruyeElEvento() {
        // Act — el constructor de DomainEvent rechaza un topic que no tenga 3 segmentos
        EstudiantesFichaPerfilAsignadosEvent evento = eventoCon(List.of(ANA));

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("fichas.estudiante_ficha_perfil.asignados");
        assertThat(evento.getTipoEvento()).isEqualTo("EstudiantesFichaPerfilAsignadosEvent");
    }

    @Test
    void debeAsignarIdYMomentoDeOcurrencia_cuandoSeConstruyeElEvento() {
        // Act
        EstudiantesFichaPerfilAsignadosEvent evento = eventoCon(List.of(ANA));

        // Assert — el idEvento es la clave de idempotencia del consumidor
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }

    @Test
    void debeAislarLaListaDeLaQueRecibio_cuandoQuienLaConstruyeLaModificaDespues() {
        // Arrange
        List<ContactoEstudiante> mutable = new ArrayList<>(List.of(ANA));
        EstudiantesFichaPerfilAsignadosEvent evento = eventoCon(mutable);

        // Act
        mutable.add(LUIS);

        // Assert — el evento viaja al broker; una lista compartida lo dejaria mutar tras publicarse
        assertThat(evento.getEstudiantes()).containsExactly(ANA);
    }

    @Test
    void debeRechazarLaModificacionDeSusContactos_cuandoUnConsumidorLoIntenta() {
        // Arrange
        EstudiantesFichaPerfilAsignadosEvent evento = eventoCon(List.of(ANA));

        // Act & Assert
        assertThatThrownBy(() -> evento.getEstudiantes().add(LUIS))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
