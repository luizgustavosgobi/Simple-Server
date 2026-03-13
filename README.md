# Simple-Server

Simple-Server is a Java-based, modular server project built with Gradle.

It is organized as a multi-module workspace:
- `core`: shared server abstractions and infrastructure
- `http`: HTTP application layer on top of `core`
- root application: packaging, runnable JAR, and Windows EXE generation

## Features

- Multi-module architecture (`core` + `http`)
- Java NIO/event-driven server approach (see architecture docs)
- External configuration through `server.properties`
- Lightweight DI/annotation-based component discovery
- HTTP routing via annotations like `@Controller`, `@GetMapping`, `@PostMapping`, and `@RequestMapping`
- Runnable fat JAR from the root project
- Windows executable generation via Launch4j

## Project Structure

```text
Simple-Server/
  core/
  http/
  src/main/java/br/com/luizgustavosgobi/testes/Main.java
  src/main/resources/server.properties
  docs/
```

## Requirements

- JDK 11 or newer
- Gradle Wrapper (already included: `gradlew` / `gradlew.bat`)

## Build

Use the Gradle Wrapper from the repository root.

```powershell
.\gradlew.bat clean build
```

To build the runnable JAR:

```powershell
.\gradlew.bat jar
```

Generated artifact:
- `build/libs/SimpleServer-1.0.jar`

## Run

Run the packaged JAR:

```powershell
java -jar build\libs\SimpleServer-1.0.jar
```

The default entry point is `br.com.luizgustavosgobi.testes.Main`.

## Code Walkthrough

The current bootstrap is minimal and starts the HTTP application on a chosen port:

```java
public static void main(String[] args) throws IOException {
    Server application = HttpApplication.run(Main.class, 80);
    Console.getInstance().loop();
}
```

`HttpApplication.run(...)` creates and wires:
- `Router`
- `HttpConnectionHandler`
- JSON support with Jackson (`ObjectMapper`)
- bean validation (`Validator`)
- converter pipeline (`ByteToStringCodec` + `StringToHttpCoded`)

## Controller Example (Current Project Style)

A real controller from the project:

```java
@Controller
public class MainController {

    private final MainService service;

    public MainController(MainService service) {
        this.service = service;
    }

    @GetMapping(path = "/")
    public ResponseEntity<String> greeting() {
        return ResponseEntity.ok("Hello, World! Service called successfully!");
    }
}
```

## More Route Examples

You can create handlers with query params, headers, and body mapping:

```java
@Controller
public class UserController {

    @GetMapping(path = "/users")
    public ResponseEntity<String> list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestHeader(value = "x-request-id", required = false) String requestId
    ) {
        int safePage = page == null ? 1 : page;
        return ResponseEntity.ok("page=" + safePage + ", requestId=" + requestId);
    }

    @PostMapping(path = "/users")
    public ResponseEntity<String> create(@RequestBody CreateUserRequest body) {
        return ResponseEntity.status(201).body("created: " + body.name());
    }

    @RequestMapping(path = "/health", method = HttpMethod.GET)
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }
}
```

Example body DTO:

```java
public record CreateUserRequest(String name, Integer age) {}
```

## cURL Examples

With the current sample controller (`MainController`):

```bash
curl -i http://localhost:8080/
```

If you add the `UserController` example above:

```bash
curl -i "http://localhost:8080/users?page=2" -H "x-request-id: demo-123"
curl -i -X POST http://localhost:8080/users -H "Content-Type: application/json" -d "{\"name\":\"Ana\",\"age\":27}"
curl -i http://localhost:8080/health
```

## Windows EXE

This project uses Launch4j in the root `build.gradle`.

Create the executable:

```powershell
.\gradlew.bat createExe
```

Expected output:
- `build/launch4j/SimpleServer.exe`

## Configuration

Main configuration file:
- `src/main/resources/server.properties`

Current sample values:

```ini
server.port=8080
server.2.port=8081
```

Additional documented properties are listed in:
- `docs/server_properties.md`

## Tests

Run all tests:

```powershell
.\gradlew.bat test
```

## Architecture Docs

For diagrams and architecture notes, see:
- `docs/ARCHITECTURE_DIAGRAMS.md`
- `docs/IO_ARCHITECTURE_DIAGRAMS.md`

## Notes

- `@RequestParam` and `@RequestHeader` support primitive conversions (`String`, `int`, `long`, `double`, `boolean` and wrappers).
- `@RequestBody` is deserialized with Jackson and validated with Jakarta Validation when constraints are present.
- The architecture docs and some internal comments are still in Portuguese.

## License

This project is licensed under the terms of the `LICENSE` file in the repository root.

