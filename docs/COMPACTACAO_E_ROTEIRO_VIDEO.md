# Compactação de código focada em reuso

## Técnica: generalização com Template Method + Generics
(refatorações *Extract Superclass* e *Pull Up Method*)

**Antes** (commit `versão inicial`): cada DAO tinha seu próprio SQL e JDBC escritos à mão —
`listar`, `buscarPorId`, `inserir`, `atualizar`, `excluir` e `mapear` repetidos em 6 classes,
mudando só o nome da tabela e das colunas.

**Depois** (commit `refactor: compactação`): a lógica repetida subiu para `AbstractDAO<T>`,
que **gera o SQL** a partir de `tabela()` e `colunas()`. Cada DAO concreto só declara o que é
diferente.

| | Antes | Depois |
|---|---|---|
| Linhas da camada DAO (6 entidades) | 813 | 332 (−59%) |
| Linhas de um DAO concreto (ex. Cliente) | ~130 | ~25 |
| Lugares para corrigir um bug de SQL | 6 | 1 |
| Para criar um DAO novo | copiar ~130 linhas | estender e escrever ~25 |

O comportamento é o mesmo: os mesmos testes passaram nas duas versões.

A mesma ideia foi aplicada às telas: `AbstractCrudPanel` concentra formulário, tabela, botões,
salvar/excluir e mensagens; cada tela concreta só define seus campos.

## Roteiro do vídeo (5–7 min, YouTube "não listado")

1. **(0:00) Apresentação** — equipe, projeto BizFlow, o que é a técnica.
2. **(0:30) Repositório** — mostrar o GitHub e o histórico: `git log --oneline`.
3. **(1:00) O problema** — abrir `ClienteDAO.java` da versão antiga:
   `git show HEAD~2:src/main/java/br/pucpr/bizflow/dao/ClienteDAO.java`
   Mostrar ao lado o `ServicoDAO` antigo e apontar que é o mesmo código com outro nome de tabela.
4. **(2:00) Passo 1 — identificar o que repete e o que varia** — repete: abrir conexão, montar
   SQL, try/catch, last_insert_rowid. Varia: tabela, colunas, valores, como ler o ResultSet.
5. **(2:45) Passo 2 — criar `AbstractDAO<T>`** — mostrar o método `salvar()` final (Template
   Method) e os métodos abstratos `tabela()`, `colunas()`, `valores()`, `mapear()`.
   Mostrar `inserir()` gerando o SQL a partir de `colunas()`.
6. **(3:45) Passo 3 — reduzir cada DAO** — abrir o `ClienteDAO` novo (~25 linhas).
   Rodar `git diff HEAD~2 HEAD~1 --stat` para mostrar as linhas removidas.
7. **(4:30) Passo 4 — provar que continua funcionando** — rodar o sistema, cadastrar, editar e
   excluir um cliente.
8. **(5:15) Reuso na prática** — mostrar `PetDAO`/`PlanoDAO`: módulos exclusivos que nasceram
   com ~25 linhas porque já existia a base.
9. **(6:00) Fechamento** — números (813 → 332), um único lugar para manter.

Comandos úteis durante a gravação:
```bash
git log --oneline
git diff HEAD~2 HEAD~1 --stat
git diff HEAD~2 HEAD~1 -- src/main/java/br/pucpr/bizflow/dao/ClienteDAO.java
```
