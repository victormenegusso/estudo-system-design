package com.example.ingressos.vendaingressos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IngressoRepository extends JpaRepository<Ingresso, Long> {

    @Modifying
    @Query(value = "TRUNCATE TABLE ingresso", nativeQuery = true)
    void truncateTable();
}
