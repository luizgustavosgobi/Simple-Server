# SimpleServer

Um framework de servidor modular e simples desenvolvido em Java, baseado em NIO (Non-blocking I/O) com suporte a diferentes tipos de protocolos através de ConnectionHandlers personalizáveis.

Em desenvolvimento!

## 🚀 Características

- **Servidor NIO**: Baseado em Java NIO Selector para lidar com muitas de conexões ao mesmo tempo
- **Sistema de Injeção de Dependências**: Container IoC próprio com `@AutoWired` e `@Bean`
- **Logging**: Sistema de log colorizado com diferentes níveis
- **Suporte HTTP Built-in**: ConnectionHandler HTTP pré-implementado com sistema de roteamento

## 📋 Pré-requisitos

- Java 11 ou superior
- Gradle 7.0+

## 🛠️ Dependências Principais

- **Jackson Databind** (2.18.3): Serialização/deserialização JSON
- **Hibernate Validator** (8.0.1): Validação de dados
- **Lombok** (1.18.38): Redução de boilerplate
- **Jakarta EL** (4.0.2): Expression Language para validação

## 📦 Instalação

1. Clone o repositório:
```bash
git clone https://github.com/luizgustavosgobi/Simple-Server
cd SimpleServer
```

2. Execute o build:
```bash
./gradlew build
```

3. Execute a aplicação de exemplo:
```bash
./gradlew run
```

## 🎯 Uso Básico

### Servidor Customizado

```java
import br.com.luizgustavosgobi.simpleServer.core.server.ServerFactory;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;

// Implementar seu próprio ConnectionHandler
public class MyConnectionHandler implements ConnectionHandler {
    @Override
    public void onAccept(SocketChannel client) {
        System.out.println("Cliente conectado: " + client.socket().getInetAddress());
    }
    
    @Override
    public void onRead(SocketChannel client, byte[] data) {
        String message = new String(data);
        System.out.println("Recebido: " + message);
        // Processe os dados e envie resposta
    }
    
    @Override
    public void onClose(SocketChannel client) {
        System.out.println("Cliente desconectado");
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        // Criar servidor customizado
        Server server = ServerFactory.create(Main.class, 8080, new MyConnectionHandler());
        server.start();
        
        // Servidor fica rodando até ser interrompido
        System.in.read();
        server.close();
    }
}
```

### Servidor HTTP (Uso Simplificado)

```java
import br.com.luizgustavosgobi.simpleServer.http.HttpApplication;

public class HttpMain {
    public static void main(String[] args) {
        // Inicia servidor HTTP com roteamento automático
        Server server = HttpApplication.run(HttpMain.class, 8080);
    }
}
```

### Criando Controllers HTTP

```java
import br.com.luizgustavosgobi.simpleServer.http.annotations.Controller;
import br.com.luizgustavosgobi.simpleServer.http.annotations.GetMapping;
import br.com.luizgustavosgobi.simpleServer.http.annotations.PostMapping;

@Controller
public class ApiController {
    
    @GetMapping(path = "/api/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.status(HttpStatus.OK).body("Server is running!");
    }
    
    @PostMapping(path = "/api/data")
    public ResponseEntity<MyDto> receiveData(MyDto data) {
        // Processamento automático do JSON
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }
}
```

### Sistema de Injeção de Dependências

```java
import br.com.luizgustavosgobi.simpleServer.core.beans.annotations.Bean;
import br.com.luizgustavosgobi.simpleServer.core.beans.annotations.AutoWired;

@Bean
public class DatabaseService {
    public void save(Object data) {
        // Implementação de salvamento
    }
}

@Controller  
public class UserController {
    
    @AutoWired
    private DatabaseService databaseService;
    
    @PostMapping(path = "/users")
    public ResponseEntity<?> createUser(User user) {
        databaseService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
```

## ⚙️ Configuração Avançada

### Arquivo server.properties

```properties
# Configurações do servidor principal
server.port=8080

# Servidor secundário (múltiplas instâncias)
server.2.port=8081

server.blocking=false
```

### Múltiplos Servidores

```java
public class MultiServerMain {
    public static void main(String[] args) throws Exception {
        // Servidor HTTP na porta 8080
        Server httpServer = HttpApplication.run(MultiServerMain.class, 8080);
        
        // Servidor TCP customizado na porta 8081  
        Server tcpServer = ServerFactory.create(MultiServerMain.class, 8081, new MyTcpHandler());
        tcpServer.start();
    }
}
```

## 🏗️ Arquitetura

### Componentes Core

- **Server**: Servidor NIO principal que gerencia o Selector e aceita conexões
- **ServerFactory**: Factory para criação de servidores com diferentes configurações
- **ConnectionHandler**: Interface para processar diferentes tipos de protocolos
- **ConnectionTable**: Gerencia o estado de todas as conexões ativas
- **ThreadManager**: Pool de threads otimizado para operações I/O e CPU-intensivas
- **ApplicationContext**: Container IoC para gerenciamento de beans e dependências

### Estrutura do Projeto

```
src/main/java/br/com/luizgustavosgobi/simpleServer/
├── core/                   # Núcleo do servidor NIO
│   ├── beans/             # Sistema de injeção de dependências  
│   ├── configuration/     # Gerenciamento de configurações
│   ├── connection/        # Gerenciamento de conexões TCP
│   ├── context/          # Contexto da aplicação (IoC Container)
│   ├── executors/        # Gerenciamento de threads
│   └── server/           # Servidor principal e factory
├── http/                  # Implementação HTTP (ConnectionHandler especializado)
│   ├── annotations/      # Anotações para roteamento HTTP
│   ├── entities/         # Request/Response entities
│   ├── parser/          # Parser de protocolo HTTP
│   ├── router/          # Sistema de roteamento
│   └── types/           # Tipos de dados HTTP (JSON, FormData, etc.)
├── logger/               # Sistema de logging
└── utils/               # Utilitários gerais
```

---

Desenvolvido por Luiz Gustavo Sgobi
