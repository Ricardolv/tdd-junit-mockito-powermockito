# TDD - Test Driven Development 

Principio FIRST:
  - F -> "Fast" o teste unitário tem que ser executado muito rápido.
  - I -> "Idenpent" um teste não deve depender de outros.
  - R -> "Repeatable" um teste pode ser executado quantas vezes quiser e na hora que quiser.
  - S -> "Self Verifying" auto verificável, um teste deve saber quando foi correta e quando falhou.
  - T -> "Timely" um teste deve ser criado no momento correto.

# Unit tested

Padrão Unit usando Junit 

> TestRunner -> Quem vai executar os testes e coletar os resultados.
> TestFixture -> Também conhecido como TestContext são as precondições necessárias aos testes.
> TestSuites -> A onde podemos alocar os testes que devem ser executados.
> TestResultFormatter -> Padroniza o formato dos testes.
> Assertions -> As exceções que verifica o comportamento ao estado do que esta sendo testado através de uma expressões logica.

# Data-driven testing "Teste orientado a dados"

O teste direcionado por dados é uma maneira poderosa de testar um determinado cenário com diferentes combinações de valores. Existe várias maneiras de fazer testes de unidade orientados por dados no JUnit.
JUnit fornece algum suporte para testes orientados por dados, através do teste Parameterized.