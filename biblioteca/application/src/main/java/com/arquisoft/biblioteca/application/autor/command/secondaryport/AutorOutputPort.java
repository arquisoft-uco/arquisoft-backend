package com.arquisoft.biblioteca.application.autor.command.secondaryport;

import com.arquisoft.biblioteca.application.autor.command.secondaryport.entity.AutorEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AutorOutputPort {

    void guardar(AutorEntity autor);

    void reactivar(AutorEntity autor);

    void eliminarLogica(UUID id, Instant ocurridoEn);

    Optional<AutorEntity> obtenerPorId(UUID id);
}
