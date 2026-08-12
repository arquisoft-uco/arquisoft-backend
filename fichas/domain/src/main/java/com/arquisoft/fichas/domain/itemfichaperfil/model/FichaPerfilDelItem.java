package com.arquisoft.fichas.domain.itemfichaperfil.model;

import java.util.Optional;
import java.util.UUID;

public record FichaPerfilDelItem(UUID item, Optional<UUID> fichaPerfil) {}
