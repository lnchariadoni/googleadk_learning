This repo is a starting point to learn building of agents using Google ADK.
# Level 0: Getting Started with Google ADK
Refer to level0. This has a very simple example of a "science teacher" agent that answers basic science questions.

## How to run
*Note: The key must be set using export GOOGLE_API_KEY=<<YOUR_KEY>>*

### Shell based interaction
```shell
    mvn clean compile exec:java -DmainClass="agents.level0.ScienceTeacherAgent" 
```
### Web based interaction
```shell
    mvn clean compile exec:java \
    -Dexec.args="--adk.agents.source-dir=target --server.port=8000" \
    -Dexec.mainClass="com.google.adk.web.AdkWebServer" 
```
# Level 1: Getting Started Tools using local function as tool
Refer to level1. This has a very simple example of a "order status" agent that answers to questions on order status.
## How to run
*Note: The key must be set using export GOOGLE_API_KEY=<<YOUR_KEY>>*

### Shell based interaction
```shell
    mvn clean compile exec:java -DmainClass="agents.level1.CustomerOrderStatusAgent"
```
### Web based interaction
```shell
    mvn clean compile exec:java \
    -Dexec.args="--adk.agents.source-dir=target --server.port=8000" \
    -Dexec.mainClass="com.google.adk.web.AdkWebServer" 
```
# Level 2: Aggregate agent using multiple tools
Refer to level2. This is an aggregate agent that uses multiple agents to answer user queries.
## How to run
*Note: The key must be set using export GOOGLE_API_KEY=<<YOUR_KEY>>*

### Shell based interaction
```shell
    mvn clean compile exec:java -DmainClass="agents.level2.AggregateRootAgent"
```
### Web based interaction
```shell
    mvn clean compile exec:java \
    -Dexec.args="--adk.agents.source-dir=target --server.port=8000" \
    -Dexec.mainClass="com.google.adk.web.AdkWebServer" 
```

# Level 3: An course recommendation agent using MCP
Refer to level3. The agent is just a facade, per se, that depends on the MCP to retrieve available courses and respond to user queries.
## How to run
*Note: The key must be set using export GOOGLE_API_KEY=<<YOUR_KEY>>*

### Shell based interaction
```shell
    mvn clean compile exec:java -DmainClass="agents.level3.CoursesWithMCPAgent"
```

# Level 4: An DVD Movie rental analysis agent using MCP
Refer to level4. This agent uses the MCP to analyze DVD movie rental data and provide insights.
## How to run
*Note: The key must be set using export GOOGLE_API_KEY=<<YOUR_KEY>>*

### Shell based interaction
```shell
    mvn clean compile exec:java -DmainClass="agents.level4.DVDRentalsAnalysisAgentUsingMCP"
```

## References
- https://www.youtube.com/watch?v=VM3b3csBeUc&list=PLWVjTNKbh-LmnsxminYNE5UM0eKH4oy_c
- https://www.youtube.com/watch?v=44C8u0CDtSo
- https://www.youtube.com/watch?v=P4VFL9nIaIA