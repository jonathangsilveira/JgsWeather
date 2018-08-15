Vis�o geral

- API m�nima: 15 (4.0.3) em diante
- API alvo: 26 (8.0)

Requisitos necess�rio para rodar o projeto

- Android Studio
- SDK com as APIs 15 e 26 instaladas

Arquitetura

- N�o houve necessidade de uso de bibliotecas externas
- N�o foram usadas Fragments, somentes Activities
- Para buscar as informa��es do clima atual e forecast das cidades, foi montado URl atrav�s de classe URI, feito conex�o atrav�s desta e retornado as informa��es em forma de JSON
- Seguido documenta��o da OpenWeatherMap para fazer a busca, recupera��o de dados do JSON e gera��o de App Key.