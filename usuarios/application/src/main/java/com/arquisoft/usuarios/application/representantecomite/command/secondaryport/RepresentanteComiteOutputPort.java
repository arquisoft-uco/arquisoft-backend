package com.arquisoft.usuarios.application.representantecomite.command.secondaryport;

import com.arquisoft.usuarios.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;

import java.util.Optional;
import java.util.UUID;

public interface RepresentanteComiteOutputPort {

    void guardar(RepresentanteComiteEntity representanteComite);

    void reactivar(UUID usuario);

    Optional<RepresentanteComiteEntity> obtenerPorUsuario(UUID usuario);
}
