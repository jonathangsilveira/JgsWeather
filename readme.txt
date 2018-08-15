Visão geral

- API mínima: 15 (4.0.3) em diante
- API alvo: 26 (8.0)

Requisitos necessário para rodar o projeto

- Android Studio
- SDK com as APIs 15 e 26 instaladas

Arquitetura

- Não houve necessidade de uso de bibliotecas externas
- Não foram usadas Fragments, somentes Activities
- Para buscar as informações do clima atual e forecast das cidades, foi montado URl através de classe URI, feito conexão através desta e retornado as informações em forma de JSON
- Seguido documentação da OpenWeatherMap para fazer a busca, recuperação de dados do JSON e geração de App Key.