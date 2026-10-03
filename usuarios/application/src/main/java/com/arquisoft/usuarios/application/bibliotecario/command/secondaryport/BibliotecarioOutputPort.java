package com.arquisoft.usuarios.application.bibliotecario.command.secondaryport;

import com.arquisoft.usuarios.application.bibliotecario.command.secondaryport.entity.BibliotecarioEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface BibliotecarioOutputPort {

    void guardar(BibliotecarioEntity bibliotecario);

    void reactivar(UUID usuario);

    void eliminarLogica(UUID usuario, Instant eliminadoEn);

    Optional<BibliotecarioEntity> obtenerPorUsuario(UUID usuario);
}
