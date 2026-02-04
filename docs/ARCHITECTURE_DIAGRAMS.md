# Diagrama de Arquitetura Hexagonal - Simple Server

## Visão Geral da Arquitetura

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           CAMADA DE APLICAÇÃO                            │
│                                                                          │
│  ┌──────────────────┐                    ┌─────────────────────────┐   │
│  │  HttpApplication │───────creates──────▶│ HttpConnectionHandler │   │
│  └──────────────────┘                    └──────────┬──────────────┘   │
│                                                     │                    │
│                                                     │ implements         │
└─────────────────────────────────────────────────────┼──────────────────┘
                                                      │
                                                      ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         CAMADA DE DOMÍNIO (CORE)                        │
│                                                                          │
│  ┌───────────────────┐          ┌──────────────────────────────┐       │
│  │ ConnectionHandler │◀────────│      ConnectionTable          │       │
│  │   (Interface)     │          │       (Interface)             │       │
│  └───────────────────┘          └──────────────────────────────┘       │
│                                                                          │
│  ┌───────────────────┐          ┌──────────────────────────────┐       │
│  │   ServerPort      │          │      BeanRegistry             │       │
│  │   (Interface)     │          │       (Interface)             │       │
│  └────────┬──────────┘          └──────────────────────────────┘       │
│           │                                                              │
└───────────┼──────────────────────────────────────────────────────────────┘
            │
            │ implements
            ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        CAMADA DE ADAPTADORES                             │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                           Server (NIO)                           │   │
│  │                                                                  │   │
│  │  Dependencies (All Injected):                                   │   │
│  │  • ConnectionTable                                              │   │
│  │  • ConnectionHandler                                            │   │
│  │  • ThreadManager                                                │   │
│  │  • BeanRegistry                                                 │   │
│  │  • Logger                                                       │   │
│  └──────────────────────────────┬───────────────────────────────────┘   │
│                                 │                                        │
│                                 │ delegates to                           │
│                   ┌─────────────┼──────────────┐                        │
│                   │             │              │                         │
│                   ▼             ▼              ▼                         │
│  ┌─────────────────────┐  ┌──────────┐  ┌─────────────────────┐       │
│  │ AcceptEventHandler  │  │  Read    │  │  CloseEventHandler  │       │
│  │                     │  │  Event   │  │                     │       │
│  │ • connectionTable   │  │ Handler  │  │ • connectionTable   │       │
│  │ • connectionHandler │  │          │  │ • connectionHandler │       │
│  │ • threadManager     │  │ • conn   │  │ • threadManager     │       │
│  │ • isBlockingIo      │  │  Handler │  │                     │       │
│  └─────────────────────┘  │ • thread │  └─────────────────────┘       │
│                            │  Manager │                                 │
│                            └──────────┘                                 │
└─────────────────────────────────────────────────────────────────────────┘
            │
            │ uses
            ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                       CAMADA DE INFRAESTRUTURA                          │
│                                                                          │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────┐  ┌──────────────┐ │
│  │   Selector  │  │ServerSocket  │  │   Socket    │  │   DataUtils  │ │
│  │    (NIO)    │  │   Channel    │  │   Channel   │  │              │ │
│  └─────────────┘  └──────────────┘  └─────────────┘  └──────────────┘ │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

## Fluxo de Criação (Bootstrap)

```
┌───────────────┐
│ Application   │
│   (Main)      │
└───────┬───────┘
        │
        ▼
┌──────────────────────────────────────────────────────────────────┐
│                      ServerFactory                               │
│                                                                  │
│  1. Create Logger                                               │
│  2. Resolve ServerConfiguration                                 │
│  3. Bootstrap ApplicationContext                                │
│     └─> ScannerProvider scans packages                         │
│     └─> Register core beans (Logger, AnnotationRegistry)       │
│  4. Create Dependencies                                         │
│     ├─> ThreadManager                                           │
│     └─> ConnectionTable                                         │
│  5. Register dependencies in BeanRegistry                       │
│  6. Create Server (inject all dependencies)                     │
│  7. Register Server in BeanRegistry                             │
│  8. Register in ServerRegistry                                  │
└────────────────────────┬─────────────────────────────────────────┘
                         │
                         ▼
                   ┌──────────┐
                   │  Server  │
                   │ (Ready)  │
                   └────┬─────┘
                        │
                        ▼
                   server.start()
```

## Fluxo de Requisição (Runtime)

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ TCP Connection
       ▼
┌──────────────────────────────────────────────────────────────────┐
│                        Server (NIO)                              │
│                                                                  │
│  Selector.select() detects event                                │
│                                                                  │
│  ┌─────────────────────────────────────────────────────┐       │
│  │ if (key.isAcceptable())                             │       │
│  │    ├─> AcceptEventHandler.handle()                  │       │
│  │    │     ├─> Accept client                          │       │
│  │    │     ├─> Register in selector                   │       │
│  │    │     ├─> Add to ConnectionTable                 │       │
│  │    │     └─> Submit to ThreadManager                │       │
│  │    │            └─> ConnectionHandler.onAccept()    │       │
│  └─────────────────────────────────────────────────────┘       │
│                                                                  │
│  ┌─────────────────────────────────────────────────────┐       │
│  │ if (key.isReadable())                               │       │
│  │    ├─> ReadEventHandler.handle()                    │       │
│  │    │     ├─> Read data via DataUtils                │       │
│  │    │     └─> Submit to ThreadManager                │       │
│  │    │            └─> ConnectionHandler.onRead(data)  │       │
│  └─────────────────────────────────────────────────────┘       │
│                                                                  │
│  ┌─────────────────────────────────────────────────────┐       │
│  │ if (!key.isValid())                                 │       │
│  │    ├─> CloseEventHandler.handle()                   │       │
│  │    │     ├─> Submit to ThreadManager                │       │
│  │    │     │      └─> ConnectionHandler.onClose()     │       │
│  │    │     ├─> Cancel key                             │       │
│  │    │     └─> ConnectionTable.disconnect()           │       │
│  └─────────────────────────────────────────────────────┘       │
└──────────────────────────────────────────────────────────────────┘
```

## Separação de Responsabilidades

```
┌───────────────────────────────────────────────────────────────────┐
│                    ServerFactory (Creator)                        │
│ Responsabilidades:                                                │
│ • Orquestrar criação de todas as dependências                    │
│ • Configurar o ApplicationContext                                │
│ • Injetar dependências no Server                                 │
│ • Registrar servidor no registry central                         │
└───────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────────────┐
│              ApplicationContextBootstrap (Initializer)            │
│ Responsabilidades:                                                │
│ • Executar scanning de componentes anotados                      │
│ • Registrar beans fundamentais                                   │
│ • Processar annotations customizadas                             │
│ • Suportar BeanRegistry externo                                  │
└───────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────────────┐
│                  ServerConfiguration (Value Object)               │
│ Responsabilidades:                                                │
│ • Encapsular configurações do servidor                           │
│ • Resolver valores do ConfigurationManager                       │
│ • Ser imutável e seguro                                          │
└───────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────────────┐
│                    Server (NIO Adapter)                           │
│ Responsabilidades:                                                │
│ • Implementar protocolo NIO (Selector, Channels)                 │
│ • Gerenciar loop de eventos                                      │
│ • Delegar processamento para handlers especializados            │
│ • Controlar lifecycle (start/stop)                               │
└───────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────────────┐
│                  AcceptEventHandler (Specialist)                  │
│ Responsabilidades:                                                │
│ • Processar eventos de aceitação                                 │
│ • Configurar novos clientes (blocking, register)                 │
│ • Adicionar em ConnectionTable                                   │
│ • Notificar ConnectionHandler                                    │
└───────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────────────┐
│                   ReadEventHandler (Specialist)                   │
│ Responsabilidades:                                                │
│ • Processar eventos de leitura                                   │
│ • Ler dados via DataUtils                                        │
│ • Detectar desconexões                                           │
│ • Notificar ConnectionHandler                                    │
└───────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────────────┐
│                  CloseEventHandler (Specialist)                   │
│ Responsabilidades:                                                │
│ • Processar eventos de fechamento                                │
│ • Notificar ConnectionHandler                                    │
│ • Cancelar SelectionKey                                          │
│ • Remover de ConnectionTable                                     │
└───────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────────────┐
│                   ServerRegistry (Manager)                        │
│ Responsabilidades:                                                │
│ • Rastrear todas instâncias de servidores                        │
│ • Gerar índices únicos                                           │
│ • Integrar com Console para gerenciamento                        │
└───────────────────────────────────────────────────────────────────┘
```

## Injeção de Dependências

```
┌────────────────────────────────────────────────────────────────┐
│                  Antes (Criação Interna)                       │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  public Server(int port, ...) {                               │
│      this.threadManager = new ThreadManager();  ❌            │
│      this.context = new ApplicationContext();   ❌            │
│      // Dependências hardcoded                                │
│  }                                                             │
│                                                                │
│  Problemas:                                                    │
│  • Impossível testar com mocks                                │
│  • Acoplamento forte                                          │
│  • Difícil de estender                                        │
└────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────┐
│                  Depois (Injeção de Dependências)             │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  public Server(int port,                                      │
│                ConnectionTable connectionTable,     ✅        │
│                ConnectionHandler connectionHandler, ✅        │
│                ThreadManager threadManager,         ✅        │
│                BeanRegistry context,                ✅        │
│                Logger logger) {                     ✅        │
│      this.threadManager = threadManager;                      │
│      this.context = context;                                  │
│      // Todas dependências injetadas                          │
│  }                                                             │
│                                                                │
│  Benefícios:                                                   │
│  • Facilita testes (mocks)                                    │
│  • Desacoplamento                                             │
│  • Fácil de estender                                          │
│  • Inversão de controle                                       │
└────────────────────────────────────────────────────────────────┘
```

## Comparação: BeanRegistry

```
┌────────────────────────────────────────────────────────────────┐
│                    Antes (Duplicado)                           │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  ServerFactory:                                                │
│    BeanRegistry context1 = scProvider.getBeanRegistry(); ❌   │
│    context1.register(beans...)                                │
│                                                                │
│  Server:                                                       │
│    BeanRegistry context2 = new ApplicationContext();    ❌   │
│    context2.register(beans...)                                │
│                                                                │
│  Problema: 2 contextos diferentes!                            │
└────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────┐
│                Depois (Compartilhado)                          │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  ServerFactory:                                                │
│    BeanRegistry context = bootstrap.getBeanRegistry(); ✅     │
│    context.register(beans...)                                 │
│    Server server = new Server(..., context, ...);             │
│                                                                │
│  Server:                                                       │
│    this.context = context; // Recebe injetado        ✅     │
│                                                                │
│  Solução: Mesmo contexto compartilhado!                       │
└────────────────────────────────────────────────────────────────┘
```

## Princípios SOLID Aplicados

```
┌─────────────────────────────────────────────────────────────────┐
│ S - Single Responsibility Principle                             │
├─────────────────────────────────────────────────────────────────┤
│ ✅ Server: apenas gerencia NIO                                  │
│ ✅ AcceptEventHandler: apenas processa accept                   │
│ ✅ ReadEventHandler: apenas processa read                       │
│ ✅ CloseEventHandler: apenas processa close                     │
│ ✅ ServerFactory: apenas orquestra criação                      │
│ ✅ ApplicationContextBootstrap: apenas inicializa contexto      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ O - Open/Closed Principle                                       │
├─────────────────────────────────────────────────────────────────┤
│ ✅ ServerPort (interface) está fechado para modificação         │
│ ✅ Server implementa ServerPort (aberto para extensão)          │
│ ✅ Novas implementações: NettyServer, VirtualThreadServer       │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ L - Liskov Substitution Principle                               │
├─────────────────────────────────────────────────────────────────┤
│ ✅ Qualquer implementação de ServerPort pode substituir Server  │
│ ✅ Qualquer implementação de ConnectionHandler funciona         │
│ ✅ Qualquer implementação de ConnectionTable funciona           │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ I - Interface Segregation Principle                             │
├─────────────────────────────────────────────────────────────────┤
│ ✅ ServerPort expõe apenas métodos essenciais                   │
│ ✅ ConnectionHandler tem apenas 3 métodos                       │
│ ✅ ConnectionTable tem interface mínima                         │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ D - Dependency Inversion Principle                              │
├─────────────────────────────────────────────────────────────────┤
│ ✅ Server depende de abstrações (interfaces)                    │
│ ✅ HttpApplication depende de ServerPort, não de Server         │
│ ✅ Handlers dependem de interfaces, não de implementações       │
└─────────────────────────────────────────────────────────────────┘
```

---

**Diagrama criado em**: 03/02/2026  
**Arquitetura**: Hexagonal (Ports & Adapters) + Clean Architecture
