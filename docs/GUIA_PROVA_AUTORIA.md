# Guia para a prova de autoria (15/10)

Todos precisam saber responder **tudo**. Divisão sugerida para apresentar as telas:

- **Integrante 1:** Clientes + Serviços (CRUDs), Singleton, compactação (AbstractDAO)
- **Integrante 2:** Profissionais + Agendamentos (CRUDs), Strategy, variabilidade

## Perguntas prováveis

**1. O que é Singleton e onde vocês usaram?**
Garante uma única instância da classe com acesso global. `ConexaoBanco` (uma só conexão
com o SQLite) e `GerenciadorVariabilidade` (um só contexto de negócio/usuário).
Como: construtor `private`, atributo `static instancia`, método `static getInstancia()`
que cria só na primeira chamada. `synchronized` evita duas criações em paralelo.

**2. Por que a conexão é Singleton?**
Abrir conexão é caro, e o SQLite trava ("database is locked") com várias conexões
escrevendo. Uma conexão compartilhada resolve.

**3. O que é Template Method? Qual é o "método template"?**
A classe base define o **algoritmo fixo** num método `final` e deixa **passos** para as
subclasses. `AbstractDAO.salvar()`: validar → definir negócio → inserir ou atualizar →
`depoisDeSalvar()`. `AbstractCrudPanel.construirTela()`: cabeçalho → campos → botões →
tabela → carregar. `RelatorioTemplate.gerar()`: cabeçalho → corpo → resumo → rodapé.

**4. Diferença entre método abstrato e gancho (hook)?**
Abstrato: a subclasse é obrigada a implementar (`tabela()`, `colunas()`).
Hook: já tem implementação padrão e a subclasse sobrescreve se quiser
(`validar()`, `depoisDeSalvar()`, `aposSalvar()`, `incluirResumo()`).
Ex.: `AgendamentoDAO` sobrescreve `validar()` para aplicar a regra de horário.

**5. Por que o `salvar()` é `final`?**
Para nenhuma subclasse mudar a ordem dos passos (ex.: salvar sem validar).

**6. O que é Strategy e onde usaram?**
Uma família de algoritmos intercambiáveis atrás de uma interface; o contexto escolhe qual
usar em tempo de execução.
- Agenda: `AgendamentoIndividual` x `AgendamentoEmGrupo` (academia aceita turma).
- Pagamento: `PagamentoPix` (−5%), `PagamentoCartao` (+3%), `PagamentoDinheiro` (arredonda).
- Notificação: `NotificacaoWhatsApp`, `NotificacaoEmail`, `NotificacaoSms`.

**7. Diferença entre Strategy e Template Method?**
Template Method usa **herança** (a subclasse preenche passos). Strategy usa **composição**
(o objeto recebe outro objeto que faz o trabalho) e pode trocar em execução.

**8. Onde a Strategy é escolhida?**
Agenda: `Segmento.regraAgendamento()`. Notificação:
`GerenciadorVariabilidade.estrategiaNotificacao()` (WhatsApp se o módulo está ligado;
senão e-mail se o cliente tem e-mail; senão SMS). Pagamento: `FormaPagamento.estrategia()`.

**9. Quais tabelas e como se relacionam?**
`negocio` (os clientes do BizFlow), `modulo_ativo` (configuração), `cliente`, `servico`,
`profissional`, `agendamento` (FK para cliente, serviço e profissional), `pet` (FK para
cliente) e `plano`. Toda tabela tem `negocio_id`.

**10. Para que serve `negocio_id`?**
Multi-tenant: o mesmo banco guarda vários negócios, e o `AbstractDAO` sempre filtra
`WHERE negocio_id = ?`. O petshop nunca vê os clientes do salão.

**11. O que acontece se excluir um cliente com agendamento?**
A chave estrangeira bloqueia (`PRAGMA foreign_keys = ON`) e o `AbstractDAO` traduz o erro
para uma mensagem amigável.

**12. Como planejaram a variabilidade?**
Ver `docs/VARIABILIDADE.md`: enum `Modulo` é o feature model (obrigatório/opcional/
exclusivo), a tabela `modulo_ativo` guarda o que cada negócio ligou, e o
`GerenciadorVariabilidade` decide o que é visível. O menu é montado a partir disso.

**13. Qual é a função exclusiva de um cliente?**
"Pets e Vacinas" (só Petshop) e "Planos e Mensalidades" (só Academia). Ficam separadas
porque o enum `Modulo` declara `EnumSet.of(Segmento.PETSHOP)` — em outro segmento o item
nem entra no menu.

**14. Como criar um novo segmento?** Ver o final de `docs/VARIABILIDADE.md`.

**15. Que técnica de compactação vocês usaram?**
Generalização com Template Method + Generics: o código repetido dos DAOs subiu para
`AbstractDAO<T>`, que gera o SQL a partir das colunas. 813 → 332 linhas, mesmo comportamento.

**16. Como o `AbstractDAO` monta o INSERT sozinho?**
`"INSERT INTO " + tabela() + " (negocio_id, " + String.join(", ", colunas()) + ") VALUES (?" + ", ?".repeat(n) + ")"`
e depois preenche os `?` com `valores(entidade)` na mesma ordem.

## Pontos para treinar ao vivo
- Abrir o sistema e fazer um CRUD completo (incluir, alterar, excluir) numa tela sua.
- Trocar de negócio/perfil e explicar por que o menu mudou.
- Mostrar no código onde está cada padrão sem procurar muito.
