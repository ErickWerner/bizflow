# BizFlow — Linha de Produto de Software

Sistema de gestão para pequenos negócios que vivem de agenda (salão, petshop, barbearia, academia).
**Um único código** atende os quatro segmentos: o que muda entre eles é **configuração**, não código copiado.

Java 17+ · Swing · SQLite (arquivo `bizflow.db`, criado sozinho na primeira execução)

## Como rodar

**IntelliJ / NetBeans / Eclipse:** abra a pasta como projeto Maven (`pom.xml`) e rode `br.pucpr.bizflow.Main`.

**Terminal:**
```bash
mvn compile exec:java
```
Na tela de login escolha o **negócio** e o **perfil** — o menu muda conforme a escolha.
Para começar do zero, apague o arquivo `bizflow.db`.

## Onde está cada item da atividade

| Item pedido | Onde está |
|---|---|
| 10+ classes com atributos e métodos | `model/`: Entidade, Negocio, Cliente, Servico, Profissional, Agendamento, Pagamento, Usuario, Pet, Plano (+ enums) |
| **Singleton** (2) | `db/ConexaoBanco`, `variabilidade/GerenciadorVariabilidade` |
| **Template Method** (3) | `dao/AbstractDAO` (CRUD), `ui/AbstractCrudPanel` (telas), `relatorio/RelatorioTemplate` (relatórios) |
| **Strategy** (3) | `strategy/agenda` (regra de horário), `strategy/pagamento` (Pix/Cartão/Dinheiro), `strategy/notificacao` (WhatsApp/E-mail/SMS) |
| 2 telas CRUD por integrante, com banco | Clientes, Serviços, Profissionais, Agendamentos (+ Pets e Planos, exclusivas) |
| 3+ tabelas | 8 tabelas: negocio, modulo_ativo, cliente, servico, profissional, agendamento, pet, plano |
| Exemplo de variabilidade | `variabilidade/Modulo` (feature model) + tela **Configuração da LPS** — ver `docs/VARIABILIDADE.md` |
| Compactação de código focada em reuso | `dao/AbstractDAO` — ver `docs/COMPACTACAO_E_ROTEIRO_VIDEO.md` e o histórico do git |

## Estrutura

```
src/main/java/br/pucpr/bizflow/
├── Main.java
├── model/          entidades do domínio
├── db/             ConexaoBanco (Singleton) e criação das tabelas
├── dao/            AbstractDAO (Template Method) + um DAO por tabela
├── variabilidade/  Segmento, Perfil, Modulo, GerenciadorVariabilidade (Singleton)
├── strategy/       agenda/, pagamento/, notificacao/
├── relatorio/      RelatorioTemplate (Template Method) + 3 relatórios
└── ui/             AbstractCrudPanel (Template Method) + telas Swing
```

## Documentação
- `docs/VARIABILIDADE.md` — como a variabilidade foi planejada
- `docs/COMPACTACAO_E_ROTEIRO_VIDEO.md` — técnica de compactação + roteiro do vídeo
- `docs/GUIA_PROVA_AUTORIA.md` — perguntas prováveis na prova de autoria
