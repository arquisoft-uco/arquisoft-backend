package com.arquisoft.usuarios.application.administrador.command.secondaryport;

import com.arquisoft.usuarios.application.administrador.command.secondaryport.entity.AdministradorEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AdministradorOutputPort {

    void guardar(AdministradorEntity administrador);

    void reactivar(UUID usuario);

    void eliminarLogica(UUID usuario, Instant eliminadoEn);

    Optional<AdministradorEntity> obtenerPorUsuario(UUID usuario);

    long contarVigentes();
}
