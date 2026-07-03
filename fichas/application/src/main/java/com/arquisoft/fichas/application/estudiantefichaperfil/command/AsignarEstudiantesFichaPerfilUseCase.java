package com.arquisoft.fichas.application.estudiantefichaperfil.command;

import com.arquisoft.fichas.application.estudiante.query.port.out.EstudianteQueryOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.port.in.AsignarEstudiantesFichaPerfilInputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.application.estudiantefichaperfil.exception.LimiteEstudiantesExcedidoException;
import com.arquisoft.fichas.application.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsignarEstudiantesFichaPerfilUseCase implements AsignarEstudiantesFichaPerfilInputPort {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final EstudianteQueryOutputPort estudianteQueryOutputPort;
    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(AsignarEstudiantesFichaPerfilCommand command) {
        UUID fichaPerfilId = command.fichaPerfilId();
        var estudiantesIds = command.estudiantesIds();

        // 1. Ficha existe
        if (!fichaPerfilOutputPort.existsById(fichaPerfilId)) {
            throw new FichaPerfilNoEncontradaException(fichaPerfilId);
        }

        // 2. Sin duplicados dentro de la lista
        if (new HashSet<>(estudiantesIds).size() != estudiantesIds.size()) {
            // Identificar el primer duplicado
            var visto = new HashSet<UUID>();
            for (UUID estudianteId : estudiantesIds) {
                if (!visto.add(estudianteId)) {
                    throw new EstudianteDuplicadoException(estudianteId);
                }
            }
        }

        // 3. Por cada estudiante: existe + no ya vinculado
        for (UUID estudianteId : estudiantesIds) {
            if (!estudianteQueryOutputPort.existsById(estudianteId)) {
                throw new EstudianteNoEncontradoException(estudianteId);
            }
            if (estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudianteId)) {
                throw new EstudianteDuplicadoException(estudianteId);
            }
        }

        // 4. Límite: (existentes + nuevos) <= ESTUDIANTES_MAX
        long existentes = estudianteFichaPerfilOutputPort.contarPorFichaPerfilId(fichaPerfilId);
        if (existentes + estudiantesIds.size() > FichasMessages.FichaPerfil.ESTUDIANTES_MAX) {
            throw new LimiteEstudiantesExcedidoException();
        }

        // 5. Crear y guardar cada relación
        for (UUID estudianteId : estudiantesIds) {
            EstudianteFichaPerfilAggregate relacion = EstudianteFichaPerfilAggregate.crear(fichaPerfilId, estudianteId);
            estudianteFichaPerfilOutputPort.guardar(relacion);
        }

        // 6. Log
        log.info(FichasMessages.EstudianteFichaPerfil.LOG_ASIGNADO, fichaPerfilId, estudiantesIds.size());
    }
}
