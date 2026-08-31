package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaFinder;
import com.arquisoft.fichas.application.estadofichaperfil.command.usecase.AsignarEstadoInicialFichaPerfilUseCase;
import com.arquisoft.fichas.application.estudiante.command.finder.EstudiantesFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.TituloFichaPerfilExisteFinder;
import com.arquisoft.fichas.application.fichaperfil.command.validator.RegistrarFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.entity.FichaPerfilEntity;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.AgregacionEstudiantesFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.RegistroFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.event.DestinatarioEvento;
import com.arquisoft.fichas.domain.fichaperfil.event.FichaPerfilRegistradaEvent;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.FichaPerfilOutputPort;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * La asignación de estudiantes ya no se dispara aquí: la encadena
 * AsignarEstadoInicialFichaPerfilUseCase al terminar (ver
 * AsignarEstadoInicialFichaPerfilUseCaseTest).
 */
@ExtendWith(MockitoExtension.class)
class RegistrarFichaPerfilUseCaseTest {

    private static final UUID ESTUDIANTE = UUID.randomUUID();
    private static final String ESTUDIANTE_NOMBRE = "Ana Gomez";
    private static final String ESTUDIANTE_EMAIL = "ana.gomez@soyuco.edu.co";

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @Mock
    private AsesorFichaFinder asesorFichaFinder;

    @Mock
    private EstudiantesFinder estudiantesFinder;

    @Mock
    private TituloFichaPerfilExisteFinder tituloFichaPerfilExisteFinder;

    @Mock
    private RegistrarFichaPerfilValidator registrarFichaPerfilValidator;

    @Mock
    private AsignarEstadoInicialFichaPerfilUseCase asignarEstadoInicialFichaPerfilUseCase;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @InjectMocks
    private RegistrarFichaPerfilUseCaseImpl registrarFichaPerfilUseCase;

    @Test
    void debeRegistrar_cuandoDatosValidos() {
        // Arrange
        RegistroFichaPerfilDomain registro = registroValido();
        stubConsultas(registro.getFicha(), asesor(), false);

        // Act
        UUID resultado = registrarFichaPerfilUseCase.ejecutar(registro);

        // Assert
        assertThat(resultado).isEqualTo(registro.getFichaPerfil());
        verify(fichaPerfilOutputPort, times(1)).registrarFicha(entidadDe(registro.getFicha()));
    }

    @Test
    void debeConsultarYValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        RegistroFichaPerfilDomain registro = registroValido();
        FichaPerfilDomain ficha = registro.getFicha();
        stubConsultas(ficha, asesor(), false);

        // Act
        registrarFichaPerfilUseCase.ejecutar(registro);

        // Assert
        InOrder inOrder = inOrder(asesorFichaFinder, tituloFichaPerfilExisteFinder,
                registrarFichaPerfilValidator, fichaPerfilOutputPort);
        inOrder.verify(asesorFichaFinder).obtener(ficha.getAsesorFicha());
        inOrder.verify(tituloFichaPerfilExisteFinder).obtener(ficha.getTituloProyecto());
        inOrder.verify(registrarFichaPerfilValidator).validar(ficha, true, false);
        inOrder.verify(fichaPerfilOutputPort).registrarFicha(entidadDe(ficha));
    }

    @Test
    void debeEncadenarElEstadoInicialConElMismoRegistro_despuesDePersistirLaFicha() {
        // Arrange
        RegistroFichaPerfilDomain registro = registroValido();
        stubConsultas(registro.getFicha(), asesor(), false);

        // Act
        registrarFichaPerfilUseCase.ejecutar(registro);

        // Assert
        verify(asignarEstadoInicialFichaPerfilUseCase).ejecutar(registro);

        InOrder inOrder = inOrder(fichaPerfilOutputPort, asignarEstadoInicialFichaPerfilUseCase);
        inOrder.verify(fichaPerfilOutputPort).registrarFicha(any());
        inOrder.verify(asignarEstadoInicialFichaPerfilUseCase).ejecutar(any());
    }

    @Test
    void debePublicarElEventoConAsesorYEstudiantes_cuandoElRegistroTermina() {
        // Arrange
        RegistroFichaPerfilDomain registro = registroValido();
        FichaPerfilDomain ficha = registro.getFicha();
        AsesorFichaDomain asesor = asesor();
        stubConsultas(ficha, asesor, false);

        // Act
        registrarFichaPerfilUseCase.ejecutar(registro);

        // Assert
        ArgumentCaptor<FichaPerfilRegistradaEvent> captor =
                ArgumentCaptor.forClass(FichaPerfilRegistradaEvent.class);
        verify(eventPublisher).publish(captor.capture());

        FichaPerfilRegistradaEvent evento = captor.getValue();
        assertThat(evento.getFichaPerfilId()).isEqualTo(ficha.getId());
        assertThat(evento.getTituloProyecto()).isEqualTo(ficha.getTituloProyecto());
        assertThat(evento.getAsesorFichaId()).isEqualTo(asesor.getId());
        assertThat(evento.getAsesorNombre()).isEqualTo(asesor.getNombre());
        assertThat(evento.getAsesorEmail()).isEqualTo(asesor.getEmail());
        assertThat(evento.getEstudiantes())
                .containsExactly(new DestinatarioEvento(ESTUDIANTE_NOMBRE, ESTUDIANTE_EMAIL));
    }

    @Test
    void debePublicarDespuesDeAsignarElEstadoInicial_cuandoElRegistroTermina() {
        // Arrange
        RegistroFichaPerfilDomain registro = registroValido();
        stubConsultas(registro.getFicha(), asesor(), false);

        // Act
        registrarFichaPerfilUseCase.ejecutar(registro);

        // Assert
        InOrder inOrder = inOrder(asignarEstadoInicialFichaPerfilUseCase, eventPublisher);
        inOrder.verify(asignarEstadoInicialFichaPerfilUseCase).ejecutar(any());
        inOrder.verify(eventPublisher).publish(any(FichaPerfilRegistradaEvent.class));
    }

    @Test
    void debePasarElResultadoDeLasConsultasAlValidator_cuandoElAsesorNoExiste() {
        // Arrange
        RegistroFichaPerfilDomain registro = registroValido();
        FichaPerfilDomain ficha = registro.getFicha();
        stubConsultas(ficha, AsesorFichaDomain.VACIO, false);
        doThrow(new AsesorFichaNoEncontradoException(ficha.getAsesorFicha()))
                .when(registrarFichaPerfilValidator).validar(ficha, false, false);

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(registro))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);

        verify(fichaPerfilOutputPort, never()).registrarFicha(any());
        verify(asignarEstadoInicialFichaPerfilUseCase, never()).ejecutar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePasarElResultadoDeLasConsultasAlValidator_cuandoElTituloEstaDuplicado() {
        // Arrange
        RegistroFichaPerfilDomain registro = registroValido();
        FichaPerfilDomain ficha = registro.getFicha();
        stubConsultas(ficha, asesor(), true);
        doThrow(new FichaTituloDuplicadoException(ficha.getTituloProyecto()))
                .when(registrarFichaPerfilValidator).validar(ficha, true, true);

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(registro))
                .isInstanceOf(FichaTituloDuplicadoException.class);

        verify(fichaPerfilOutputPort, never()).registrarFicha(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        RegistroFichaPerfilDomain registro = registroValido();
        stubConsultas(registro.getFicha(), asesor(), false);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(fichaPerfilOutputPort).registrarFicha(entidadDe(registro.getFicha()));

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(registro))
                .isInstanceOf(InfrastructureException.class);

        verify(asignarEstadoInicialFichaPerfilUseCase, never()).ejecutar(any());
        verify(eventPublisher, never()).publish(any());
    }

    private void stubConsultas(
            FichaPerfilDomain ficha, AsesorFichaDomain asesorFicha, boolean tituloYaExiste) {
        when(asesorFichaFinder.obtener(ficha.getAsesorFicha())).thenReturn(presencia(asesorFicha));
        when(tituloFichaPerfilExisteFinder.obtener(ficha.getTituloProyecto()))
                .thenReturn(tituloYaExiste);
        lenient().when(estudiantesFinder.obtener(List.of(ESTUDIANTE)))
                .thenReturn(List.of(EstudianteDomain.reconstruir(
                        ESTUDIANTE, "1099", ESTUDIANTE_NOMBRE, ESTUDIANTE_EMAIL)));
    }

    private static Optional<AsesorFichaDomain> presencia(AsesorFichaDomain asesorFicha) {
        return asesorFicha.esVacio() ? Optional.empty() : Optional.of(asesorFicha);
    }

    private static AsesorFichaDomain asesor() {
        return AsesorFichaDomain.reconstruir(
                UUID.randomUUID(), "1088", "Carlos Ruiz", "carlos.ruiz@soyuco.edu.co");
    }

    private static RegistroFichaPerfilDomain registroValido() {
        var ficha = FichaPerfilDomain.crear("Título de prueba", UUID.randomUUID());

        return RegistroFichaPerfilDomain.crear(
                ficha,
                EstadoFichaPerfilDomain.crear(ficha.getId()),
                AgregacionEstudiantesFichaPerfilDomain.crear(
                        EstudianteFichaPerfilDomain.crear(ficha.getId(), List.of(ESTUDIANTE))));
    }

    // El puerto ya recibe la entidad que construyo el mapper: se verifica por identidad de negocio.
    private static FichaPerfilEntity entidadDe(FichaPerfilDomain ficha) {
        return argThat(entity -> entity.id().equals(ficha.getId())
                && entity.tituloProyecto().equals(ficha.getTituloProyecto())
                && entity.asesorFicha().equals(ficha.getAsesorFicha()));
    }
}
