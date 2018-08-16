Visão geral

- API mínima: 15 (4.0.3) em diante
- API alvo: 28

Requisitos necessário para rodar o projeto

- Android Studio
- SDK com as APIs 15 e 28 instaladas
- IDE atualizada em relação ao Gradle.

Arquitetura

- Utilizado a Android Architecture Components. Persistência com Room Persistence.
- Duas activivies e dois fragments.
- Para buscar as informações do clima atual e forecast das cidades, foi montado URl através de classe URI, feito conexão através desta e retornado as informações em forma de JSON
- Seguido documentação da OpenWeatherMap para fazer a busca, recuperação de dados do JSON e geração de App Key.

Fluxo de telas

1º Previsão dia atual - Ao abrir o app, será aberto a tela mostrando a previsão de tempo para o dia atual, das cidades cadastradas pelo usuário para receber atualizações. Nesta tela, há as opções de atualizar a previsão do tempo para o dia atual, ordenar as cidades por: Nome, temperatura mínima e máxima. Ao pressionar o FloatingActionButton (+), será direcionado para a tela onde pode-se cadastrar as cidades para receber atualizações. Ao clicar em algum item da lista (previsão para uma cidade cadastrada), será direcionado para a tela de previsão para os próximos 5 dias.

2º Cadastro cidades - Nesta tela permite cadastrar cidades para receber atualizações da previsão do tempo. Após informar o nome da cidade e submeter para gravar a cidade na base, será consultado no Open Weather se retorna resultados para a cidade informada. Caso retorne, será cadastrado na base normalmente. Caso contrário, irá apresentar mensagem informando o motivo.
Também apresenta opção para excluir alguma das cidades cadastradas, para não receber atualizações para esta.
Obs. Mensagem retornada pela OpenWeather.

3º Tela de previsão para os próximos 5 dias - Nesta tela apenas apresenta a previsão (forecast) da cidade selecionada na tela Previsão dia atual.

Navegação entre as telas funciona através do botão Back, do dispositivo.
