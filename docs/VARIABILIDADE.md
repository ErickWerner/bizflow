# Como planejamos a variabilidade

## Dois eixos
1. **Por cliente (segmento do negócio)** — `Segmento` (SALAO, PETSHOP, BARBEARIA, ACADEMIA)
2. **Por usuário (perfil de acesso)** — `Perfil` (DONO, RECEPCAO, PROFISSIONAL)

## Feature model — `variabilidade/Modulo.java`

| Módulo | Tipo | Segmentos | Perfis |
|---|---|---|---|
| Clientes | Obrigatório | todos | Dono, Recepção |
| Serviços | Obrigatório | todos | Dono, Recepção |
| Profissionais | Obrigatório | todos | Dono |
| Agendamentos | Obrigatório | todos | todos |
| **Pets e Vacinas** | **Exclusivo** | **só Petshop** | todos |
| **Planos e Mensalidades** | **Exclusivo** | **só Academia** | Dono, Recepção |
| Relatórios | Opcional | todos | Dono |
| Notificação WhatsApp | Opcional | todos | todos |
| Configuração da LPS | Obrigatório | todos | Dono |

- **Obrigatório:** núcleo comum, todo cliente tem.
- **Opcional:** cada cliente liga/desliga (tabela `modulo_ativo`).
- **Exclusivo:** só existe para um segmento — a "função específica de um cliente".

## Pontos de variação no código

| O que varia | Mecanismo | Arquivo |
|---|---|---|
| Quais telas aparecem no menu | Feature flags no banco + regras do enum | `GerenciadorVariabilidade.isVisivel()`, `MainFrame.reconstruirMenu()` |
| Nome "Cliente / Tutor / Aluno" | Atributo do segmento | `Segmento.getRotuloCliente()` |
| Categorias de serviço | Atributo do segmento | `Segmento.getCategorias()` |
| Regra de agenda (individual x em grupo) | **Strategy** escolhida pelo segmento | `Segmento.regraAgendamento()` |
| Canal de notificação | **Strategy** escolhida pela config | `GerenciadorVariabilidade.estrategiaNotificacao()` |
| Dados de cada cliente separados | coluna `negocio_id` em toda tabela | `AbstractDAO` filtra sempre por negócio |

## Momento de resolução (binding time)
Em **tempo de execução**: a configuração vem do banco no login. Ligar um módulo na tela
"Configuração da LPS" muda o menu na hora — sem recompilar, sem outro executável.

## Como adicionar um novo segmento (ex.: Clínica de Estética)
1. Adicionar `CLINICA(...)` no enum `Segmento` (rótulos e categorias).
2. Se tiver função exclusiva: criar a entidade, o DAO (estendendo `AbstractDAO`, ~25 linhas),
   a tela (estendendo `AbstractCrudPanel`, ~60 linhas) e um item no enum `Modulo`.
3. Ligar a tela em `MainFrame.criarTela()`.

Todo o resto (login, menu, CRUD, relatórios, pagamento, notificação) é reaproveitado.

## Para demonstrar
1. Entrar como **Salão / Dono** → não existe "Pets" nem "Planos".
2. Trocar para **PetShop / Dono** → aparece "Pets e Vacinas ★"; clientes viram "Tutores".
3. Trocar para **Academia / Recepção** → aparece "Planos ★"; somem Profissionais, Relatórios e Configuração.
4. Em **Barbearia / Dono** → Configuração da LPS → marcar "Relatórios" → Aplicar → o item aparece no menu.
5. Agendar na Academia o mesmo horário 2x (aula em grupo aceita) e no Salão (bloqueia: Strategy diferente).
