package br.com.fiap.bank.core.infrastructure.persistence.repository;

import br.com.fiap.bank.core.infrastructure.persistence.entity.ContaBancariaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ContaBancariaJpaRepository extends JpaRepository<ContaBancariaJpaEntity, UUID> {

    Optional<ContaBancariaJpaEntity> findByNumero(String numero);

    boolean existsByNumero(String numero);

    @Query("select c.conta from ClienteJpaEntity c where c.cpf = :cpf")
    Optional<ContaBancariaJpaEntity> findByCpfDoCliente(@Param("cpf") String cpf);
}
