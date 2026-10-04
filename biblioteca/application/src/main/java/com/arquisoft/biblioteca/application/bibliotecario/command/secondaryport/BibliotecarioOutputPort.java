package com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport;

import com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport.entity.BibliotecarioEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface BibliotecarioOutputPort {

    void guardar(BibliotecarioEntity bibliotecario);

    void reactivar(BibliotecarioEntity bibliotecario);

    void eliminarLogica(UUID id, Instant ocurridoEn);

    Optional<BibliotecarioEntity> obtenerPorId(UUID id);
}
