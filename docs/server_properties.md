Todas as variáveis possíveis de definir para configuração do servidor.
Arquivo: src/main/resources/server.properties


server.*.port: Int -> Define a porta para o servidor em específico. Ex: Primeiro servidor aberto -> server.1.port; Segundo -> server.2.port; Default: 8080
server.blocking: Boolean -> Define se o servidor usará blocking IO ou não. Default: false

connection.keepalive: Boolean -> Define se o servidor irá utilizar o KeepAlive em conexões TCP. Default: true
connection.keepalive.idle: Long -> Define o intervalo (em segundos) para a conexão ser considerada como idle. Default: 30
connection.keepalive.interval: Long -> Define o intervalo (em segundos) entre as verificações após o tempo de idle. Default: 10
connection.keepalive.tries: Int -> Define quantas tentativas ele irá fazer antes de desconectar. Default: 3

logger.logging: Boolean -> Define se o Logger ira exibir as informações no console. Default: true