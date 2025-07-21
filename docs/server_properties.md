Todas as variáveis possíveis de definir para configuração do servidor.
Arquivo: src/main/resources/server.properties


server.*.port: Int -> Define a porta para o servidor em específico. Ex: Primeiro servidor aberto -> server.1.port; Segundo -> server.2.port; Default: 8080
server.blocking: Boolean -> Define se o servidor usará blocking IO ou não. Default: false

logger.logging: Boolean -> Define se o Logger ira exibir as informações no console. Default: true