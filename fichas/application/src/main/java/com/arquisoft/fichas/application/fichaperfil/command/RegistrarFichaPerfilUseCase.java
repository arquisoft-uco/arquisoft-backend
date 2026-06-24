package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.asesorficha.query.port.out.AsesorFichaQueryOutputPort;
import com.arquisoft.fichas.application.estadoficha.query.port.out.EstadoFichaQueryOutputPort;
import com.arquisoft.fichas.application.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.application.estudiante.query.port.out.EstudianteQueryOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.application.estudiantefichaperfil.exception.LimiteEstudiantesExcedidoException;
import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.RegistrarFichaPerfilInputPort;
import com.arquisoft.fichas.application.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estadofichaperfil.port.out.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrarFichaPerfilUseCase implements RegistrarFichaPerfilInputPort {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final AsesorFichaQueryOutputPort asesorFichaQueryOutputPort;
    private final EstudianteQueryOutputPort estudianteQueryOutputPort;
    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;
    private final EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;
    private final EstadoFichaQueryOutputPort estadoFichaQueryOutputPort;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(RegistrarFichaPerfilCommand command) {
        // POL-03: validar que el asesor ficha exista (lookup vía query side de la vista materializada)
        if (!asesorFichaQueryOutputPort.existsById(command.asesorFichaId())) {
            throw new AsesorFichaNoEncontradoException(command.asesorFichaId());
        }

        // POL-02: validar que el título sea único
        if (fichaPerfilOutputPort.existsByTituloProyecto(command.tituloProyecto())) {
            throw new FichaTituloDuplicadoException(command.tituloProyecto());
        }

        FichaPerfilAggregate ficha = FichaPerfilAggregate.crear(
                command.tituloProyecto(),
                command.asesorFichaId()
        );

        fichaPerfilOutputPort.guardar(ficha);
        asignarEstadoInicial(ficha.getId());

        // Asignar estudiantes (si aplica)
        if (command.estudiantesIds() != null && !command.estudiantesIds().isEmpty()) {
            // Validación de límite
            if (command.estudiantesIds().size() > FichasMessages.FichaPerfil.ESTUDIANTES_MAX) {
                throw new LimiteEstudiantesExcedidoException();
            }

            // Validación de duplicados en la lista
            var idsUnicos = new HashSet<>(command.estudiantesIds());
            if (idsUnicos.size() != command.estudiantesIds().size()) {
                // Identificar el primer duplicado para el mensaje de error
                var visitados = new HashSet<UUID>();
                var duplicado = command.estudiantesIds().stream()
                    .filter(id -> !visitados.add(id))
                    .findFirst()
                    .orElseThrow();
                throw new EstudianteDuplicadoException(duplicado);
            }

            // Validación de existencia y asignación
            for (UUID estudianteId : command.estudiantesIds()) {
                if (!estudianteQueryOutputPort.existsById(estudianteId)) {
                    throw new EstudianteNoEncontradoException(estudianteId);
                }
                var relacion = EstudianteFichaPerfilAggregate.crear(ficha.getId(), estudianteId);
                estudianteFichaPerfilOutputPort.guardar(relacion);
            }
        }

        log.info(FichasMessages.FichaPerfil.LOG_REGISTRADA, ficha.getId());
        return ficha.getId();
    }

    private void asignarEstadoInicial(UUID fichaPerfilId) {
        var estadoInicial = EstadoFichaPerfilAggregate.crear(fichaPerfilId);

        estadoFichaQueryOutputPort.buscarPorNombre(estadoInicial.getEstadoFicha().getNombre())
                .orElseThrow(() -> new ApplicationException(
                        FichasMessages.EstadoFicha.NOMBRE_NO_ENCONTRADO_MENSAJE.formatted(
                                estadoInicial.getEstadoFicha().getNombre()),
                        FichasMessages.EstadoFicha.ESTADO_NO_ENCONTRADO
                ));

        estadoFichaPerfilOutputPort.guardar(estadoInicial);

        log.info(FichasMessages.EstadoFichaPerfil.LOG_CREADO,
                estadoInicial.getId(),
                estadoInicial.getFichaPerfilId(),
                estadoInicial.getEstadoFicha().getNombre());
    }
}
