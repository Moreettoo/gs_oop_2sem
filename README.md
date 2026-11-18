# Territory Predictive Insights

Global Solution 2º/2026 – Object-Oriented Programming (FIAP) – tema CitizenTech.

**Recorte escolhido:** clima local – chuva intensa e risco de alagamento em pontos vulneráveis.

## Integrantes

| Nome | RM |
|---|---|
| Enzo Moretto | 563655 |
| Lucas Scovini | 563355 |
| João Ivo | [preencher] |

## O que o sistema faz

Um programa de console em Java que lê sensores de um ponto de alagamento (pluviômetro, nível do córrego e umidade do solo), guarda as leituras em um banco de dados, calcula previsões simples e emite alertas explicando o motivo.

Todos os dados são simulados e o território é fictício ("Zona Leste", "Córrego Água Branca – Ponte da Rua 5"). Não há dados de pessoas reais.

## Público beneficiado

- **Quem sofre o problema:** moradores de áreas próximas a córregos que alagam com chuva forte.
- **Quem decide:** Defesa Civil ou associação de bairro.
- **Decisão que o sistema apoia:** quando avisar a comunidade e deixar a equipe de prontidão. O sistema só mostra o alerta e a explicação. Quem decide o que fazer é a pessoa.

## Como rodar

Precisa de **JDK 17 ou mais novo** e **Maven 3.6.3 ou mais novo**. Na primeira vez o Maven precisa de internet para baixar as bibliotecas.

```
mvn test
mvn compile exec:java
```

No `mvn test` aparecem avisos do Mockito sobre "Java agent". São normais.

**Banco de dados:** usamos o H2 em modo arquivo. Não precisa instalar nada. A pasta `data/` é criada sozinha na primeira execução e guarda o histórico, que continua lá quando o programa é reiniciado. Para zerar o banco, apague a pasta `data/`.

**Para ver um alerta rápido:** rode o programa e digite `6` (simula 10 leituras com chuva forte), depois `2` (roda as previsões e mostra os alertas).

## Menu

| Opção | O que faz |
|---|---|
| 1 | Lê uma vez cada sensor e grava no banco |
| 2 | Roda as duas previsões e emite os alertas |
| 3 | Histórico: medições de um sensor, alertas emitidos ou exportar CSV |
| 4 | Dados do território: ver, alterar população ou alterar limiares |
| 5 | Status completo |
| 6 | Simula 10 leituras com chuva forte |
| 0 | Encerra |

## Decisões da equipe

- **JDBC puro com H2 em arquivo.** Simples e sem Spring. Escolhemos o H2 e não o Oracle para quem corrigir conseguir rodar sem instalar nada. Todo SQL usa `PreparedStatement` e `try-with-resources`.
- **Duas previsões:** média móvel (média das últimas 5 leituras) e comparação com o período anterior (projeta a tendência).
- **Alertas:** níveis ATENÇÃO, ALERTA e CRÍTICO. Cada sensor gera no máximo 1 alerta por rodada, com o pior nível. Se as duas previsões empatam, vale a média móvel.
- **Limiares padrão:** chuva 30/50/80 mm/h, nível do córrego 150/250/350 cm, umidade do solo 70/85/95 %. Os limiares alterados no menu valem só até fechar o programa.
- **Validação:** valores fora da faixa do sensor lançam `DadosInvalidosException`. Falha de sensor ou do banco lança `FonteIndisponivelException`. O menu captura as duas com mensagem clara.

## Padrões criacionais

- **Singleton – `ConfiguracaoSistema`:** os limiares e a configuração precisam ser um só para todo o sistema. Se cada classe criasse a sua, os valores poderiam ficar diferentes. Por isso o construtor é privado e todos usam `getInstancia()`.
- **Factory – `FabricaPrevisao`:** cria a estratégia de previsão pelo nome (`MEDIA_MOVEL` ou `COMPARACAO`) em um único lugar. O resto do código não tem `if` para escolher a estratégia e só chama `prever()`.

## Conceitos de POO e onde estão

- **Classe abstrata:** `ComponenteTerritorial` (com `verificarStatus()` abstrato, filha concreta `PontoMonitorado`) e `EstrategiaPrevisao`.
- **Interface:** `Sensor`, com 3 implementações (`SensorPluviometro`, `SensorNivelCorrego`, `SensorUmidadeSolo`).
- **Herança e polimorfismo:** `PrevisaoMediaMovel` e `PrevisaoComparacao` sobrescrevem `prever()` e usam `super`.
- **Encapsulamento:** `DadosTerritorio` tem atributos privados e setters que validam.

## Testes

28 testes JUnit 5 no padrão Arrange, Act, Assert. Cobrem validação de medições, limiares, as duas previsões, o Singleton e a Factory, o banco (H2 em memória), as regras de alerta e a exportação CSV. Os testes de `assertThrows` conferem as exceções próprias. O `ServicoMonitoramentoTest` usa Mockito para trocar os DAOs por falsos e testar a lógica de alerta sem banco.

## Bônus: exportar CSV

Opção `3`, depois `3` e o número do sensor. O arquivo é salvo em `exportacoes/<sensor>.csv`.

## Reflexão crítica

**Quando o sistema deve se calar?** Com menos de 5 leituras no banco, a previsão não é confiável. Nesse caso o sistema não prevê e não alerta, e mostra "Histórico insuficiente (n de 5)". Também não grava medições fora da faixa do sensor.

**O que os dados não representam?** São simulados. Um pluviômetro não representa um bairro inteiro, e o sistema não sabe de drenagem, relevo ou obras. Os limiares são exemplos, e uma Defesa Civil real usaria os seus.

**Por que o alerta apoia e não decide?** O alerta é só um texto com o sensor, o valor previsto e o limite estourado. Avisar moradores ou evacuar uma área é uma decisão de alto impacto e precisa de uma pessoa, que conhece o local e pode confirmar se o alerta faz sentido.

## Uso de IA

Professor, utilizamos IA em algumas partes do projeto, foi usada como auxilio na parte de estruturação do projeto (quando ele estava no papel ainda) para conferirmos se estávamos caminhando para o lado certo, utilizamos para fazer a escrita dos testes, que era algo pouco ambientado e relativamente novo para o grupo, utilizamos em uma parte da classe DadosTerritorio, pois precisamos utilizar HashMap, que é um conceito que tivemos que aprender por fora. Foi utilizada para a escrita de coisas triviais como os textos dos System.out.println, afim de poupar tempo e conseguir focar no que realmente importa, precisamos entender por fora o que era o h2 também

Teve conhecimentos prévios do grupo que utilizamos mas que não foram vistos em sala de aula, como Interface, Enum, classe abstrata, protected, finally e etc

Também tivemos que user auxilio de busca e pesquisa para ferramentas nvoas, como o Random, o exportador de CSV, consulta com subconsulta, AlertaDAO e medicaoDAO com List<>, o assertThrows, injeção pelo construtor e a parte do Factory e Singleton. Além de um auxilio na escrita do readme

Em resumo foi isso, qualquer outra parte foi utilizada apenas sintaxe simples com lógica de programação de JDBC para armazenar os dados, conceitos vistos na aula

## Divisão de tarefas

| Integrante | Parte |
|---|---|
| Enzo Moretto | `pom.xml`, exceções e domínio (sensores, medição, previsões, território), com os testes de `Medicao` e `DadosTerritorio` |
| Lucas Scovini | persistência (banco e DAOs), `ConfiguracaoSistema`, `FabricaPrevisao`, `RelatorioSensor` e exportação CSV, com os testes de banco, fábrica, previsões, sensores e CSV |
| João Ivo | `ServicoMonitoramento`, menu (`SistemaMonitoramento`) e README, com o teste do serviço (Mockito) |

## Limitações

- Dados simulados e um único ponto monitorado.
- Limiares editados no menu não são salvos no banco.
- Cada rodada de previsão pode gerar novos alertas, mesmo repetidos.
- Por padrão os sensores simulados não falham.
