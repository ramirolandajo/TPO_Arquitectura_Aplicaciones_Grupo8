package com.uade.order.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KnownProductJpaRepository extends JpaRepository<KnownProductJpaEntity, Long> {

    boolean existsByName(String name);
}
