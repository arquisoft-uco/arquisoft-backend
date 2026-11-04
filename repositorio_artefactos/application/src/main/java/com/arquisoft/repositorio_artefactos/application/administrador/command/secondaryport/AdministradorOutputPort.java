package com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport;

import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.entity.AdministradorEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AdministradorOutputPort {

    void guardar(AdministradorEntity administrador);

    void eliminarLogica(UUID id, Instant ocurridoEn);

    void reactivar(AdministradorEntity administrador);

    Optional<AdministradorEntity> obtenerPorId(UUID id);

    void actualizar(AdministradorEntity administrador);
}
