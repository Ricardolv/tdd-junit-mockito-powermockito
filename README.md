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

# Data-driven testing

O teste direcionado por dados é uma maneira poderosa de testar um determinado cenário com diferentes combinações de valores. Existe várias maneiras de fazer testes de unidade orientados por dados no JUnit.
JUnit fornece algum suporte para testes orientados por dados, através do teste Parameterized.

# Hamcrest 
Framework que possibilita a criação de regras de verificação (matchers) de forma declarativa. Conforme dito no próprio site do [Hamcrest] (http://hamcrest.org/)

Um matcher Hamcrest é um objeto que

- reporta se um dado objeto satisfaz um determinado critério;
- pode descrever este critério; e 
- é capaz de descrever porque um objeto não satisfaz um determinado critério.

# Test Suite
O conjunto de teste é usado para agrupar alguns casos de teste de unidade e executá-los juntos. Em JUnit, as anotações @RunWith e @Suite são usadas para executar os testes do conjunto, declaramos as classes de testes dentro na suite usando a anotação @SuiteClasses .

# Builder Pattern
Em essência, o problema que enfrentamos é que nosso teste de unidade é vinculado ao construtor. Um padrão de design comum para resolver essa dependência é o builder pattern. O builder pattern separa a construção de um objeto complexo de sua representação, de modo que o mesmo processo de construção pode criar diferentes representações, esta é a definição do builder pattern.

