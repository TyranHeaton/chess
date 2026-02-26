# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
Chess Server Design URL:
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoARiqfJCIK-P8gK0eh8KVEB-rgeWKkwes+h-DsXzQo8wHiVQSIwAgQnihignCQSRJgKSb6GLuNL7gyTJTspXI3r5d5LsKMBihKboynKZbvEqmAqsGGpujAaAQMwABmvgStAMDqTsMDiiA0AouAIW8mFNl2TF2gwPlPZ9tu3k2f6zLTJe0BIAAXigHBRjGcaFKBmHIKmMDpgAjAROaqHm8zQUWJaup1erdX1ux0U2w4OkmvrOl2G7uluXmCrt9IwIecgoM+8TToZwUXQK4X1I+AaXq+h3tnppYueKGSqABmC-SB1T6YRj0FsZYwUVR9akQ2o37fA43YTAuH4aMkMJSpMCod8cOIQjBPbQxnjeH4-heCg6AxHEiQ03TLm+FgomCqB9QNNIEb8RG7QRt0PRyaoCnDETSHaYKv31BL6Ag1ZGE1S69lCazzlq6eblqB5LXnbe-L+WAd3npe8NoKSyXqjAjLG5eMAomAPjvAKTX9s9YOdt2vb9mdKOc5Ka3xBt-UwINKCxgpUso6JaZODNozLHNC3Q8t0D1D4Qch7sMBk5VC4vcrR13V9nb66FhtGCg3DHpepvwcTFv5-u94RRkMwQDQH0vqd30ozLAma2AgPA6D-vg2BVzI8maNgDheFZnnFPMf4KLrv42Dihq-FojAADiSoaOzbWlg0e-80L9hKuLZuN9HumK6WcuFGPB1e-ZaIHzmzmf4f2sknrHyVVK62xNs-S2qoNSgPto7Z2aBXY+2btVN+tkVZu0ASfVaFFs5h2jBHYa0cZ4lDnpNeOs1+QpyWsWdOCos5QF6v1Bs9FAFUgNpdZAOQv5qAxHOCui5KjLgErvE0CAbaHyQXtIu9QuECg4eiBifsPyP3qDvThh8Hb-gQIBR+nsai1BGMsK+OYCwNHGEYlAABJaQBYprhGCIEEEmx4i6hQG6TkexvjJFAGqNxkFFjfHMQAOSVP4i4MBOhTwnmNYh88sYGLGOY1QJizFKisTYuxDjlhOJcb4oyniEDeNyYtMYASlTBLmKE8JS8mJUw4AAdjcE4FATgYgRmCHALiAA2eAE5DBcJgEUWeHMJ5c1aB0S+18g7myzEEpUkSJI6QeLCf0z8YDDBgCMWZcxLLLMWV5eo110RcIxIclA-9dZ+1YXw0cNsmRgNvkhCBKVA7YPoZtCRi4pHpUQYolB7U6EMPXFaIaUdp6VFjqQhO8Tk75ioStWhrzAVLDzpcjsqCjpbMMFQE0SAGJWw1AAIRDBowkOsPmFxQXZdBvy0X+iJeuFEpKcgwGBfg0FUSY6zzjnEsYMLFqFmoaWPQDKUBMrAEwpsqLnoHN6cczFvDgH8KFG9NcYi5jkt0VSlAmwuFWIwQ-XZMqjwoH6SiIGWiFa7N0fpQxqTrH1FsfY3OhDwVcoxgvROCS7XpKddUymARLDVwcpsemSAEhgEDX2CAIaABSEBxT7wrP4LxIA1SDOIcMhZozmQyR6OYm+DckJZmwAUwNUA4AQAclANY5irHzMTAar8Lxn5rBLcAMtFaq01rtTsr8mb0X1AAFbxrQMc4dANRXuU8n3K5iqbnQLPOAzAJpmDmxgBYtA2LkAMXxh7ce790H4xpQHTOiLNrh0jvGI9HKiETWmlmJOFDYUCvhaerqbzGHOurgYGAa6ABqOKFEzpkGw+dTI5V2tJPjFdia5gwECZlSKuUd34xA9czVaDEGobRYOTBMB6XnIvQQ7DYLUYxNIXEx9uZn1jDTkK4ljL3LIswPjb9fSKxRhtCx1DMHO35UDGaeUYZJbYevWXfdeiAxVlDOGNARGo6idpYsiF6ZMyer5anQVstpM1lk8x0TbHyA+H3NxxTUrQMZ2wFoI5SoMS1ukGsIKaxSpVqg4ptDc7W6uiszdWDKAPTuZw++RtcJ6hxonearAgWx54YxEcWM8odAQHNG5szN7XXkcxovUzzDWNNhZU2Zhy8qZeHbaG8NpX5SIGDLAYA2AS2EDyAUAZx8-mnx5nzAWQtjD3yWU2q63A8DnMtX2r5IBBtQB4SwjzBcDkTam3ugRbcjid0MIkxzAxVBrEeMaS8tpQMYaOmQYzI59V9dCwNmrPCNGRZG9ZPDIx61K1vejLLow85AA
