package br.pucpr.bizflow.model;

/**
 * Classe base de todas as entidades persistidas.
 * Todo registro pertence a um Negócio (tenant) — é isso que permite
 * um único banco/software atender vários clientes (multi-tenant).
 */
public abstract class Entidade {
    protected int id;
    protected int negocioId;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getNegocioId() { return negocioId; }
    public void setNegocioId(int negocioId) { this.negocioId = negocioId; }

    public boolean isNovo() { return id == 0; }

    /** Cada entidade conhece as próprias regras de validação. */
    public abstract void validar();

    protected void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new ValidacaoException(mensagem);
    }

    protected boolean vazio(String s) {
        return s == null || s.isBlank();
    }
}
