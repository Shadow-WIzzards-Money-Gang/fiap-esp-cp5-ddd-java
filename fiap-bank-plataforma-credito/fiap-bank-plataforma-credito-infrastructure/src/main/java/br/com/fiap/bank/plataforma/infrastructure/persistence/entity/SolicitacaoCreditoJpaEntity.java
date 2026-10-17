package br.com.fiap.bank.plataforma.infrastructure.persistence.entity;

import br.com.fiap.bank.plataforma.domain.enums.StatusSolicitacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_solicitacao_credito")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitacaoCreditoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cpf_cliente", nullable = false, length = 11)
    private String cpfCliente;

    @Column(name = "nome_cliente", nullable = false)
    private String nomeCliente;

    /** Texto exatamente como o front-end enviou (ex.: "EMPRÉSTIMO PESSOAL"). */
    @Column(name = "tipo_credito", nullable = false, length = 50)
    private String tipoCredito;

    @Column(name = "valor_solicitado", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorSolicitado;

    @Column(name = "quantidade_parcelas", nullable = false)
    private Integer quantidadeParcelas;

    @Column(name = "renda_mensal", nullable = false, precision = 15, scale = 2)
    private BigDecimal rendaMensal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusSolicitacao status;

    @Column(name = "parecer_operador", length = 500)
    private String parecer;

    @Column(name = "scr_restricoes", nullable = false)
    private Boolean restricaoScr;

    /** Nulo apenas se a solicitacao for gravada antes de avaliada pelo motor de credito. */
    @Column(name = "score_bureau")
    private Integer scoreBureau;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
}
