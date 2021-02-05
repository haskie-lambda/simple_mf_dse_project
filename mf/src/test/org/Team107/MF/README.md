# Testing for the Messaging Framewok (MF)

The `org.Team107.MF` test suite is designed to do as many tests
as possible for to ensure correct workings for the MF.
The test suite is a JUnit test suite that can be run in any
IDE or using maven (`mvn test`).

However, as the MF is normally used as an external `jar` dependency
`./example_code/artifact_testing` is an additional test that 
can be run, ensuring that the java reflection related code in the MF
works correctly when using it as a jar.

### Running the test suite
`$ project_root> mvn test`

### Running the additional test

```shell script
cd [route of the project]/implementation/mf/example_code/artifact_testing/
# installing the dependencies
mvn install:install-file -Dfile=ext/mf.jar -DgroupId=org.Team107 \
    -DartifactId=MF -Dversion=3.0.0 -Dpackaging=jar \
    -DgeneratePom=true
# compiling
mvn compile
# run
mvn exec:java -Dexec.mainClass="Main"
```

This is not a JUnit test suite (also for the sake of being
useful example code to start from).
The code passes the test when all the messages are printed 
correctly.

Correct output will look something like this:
```
{ "messageID": "07812470-f610-4e47-843e-75d8848de261"; "topic": "Messages.SimpleMessage"; "data": { "myInt": 100 } }
{ "messageID": "07812470-f610-4e47-843e-75d8848de261"; "topic": "Messages.SimpleMessage"; "data": { "myInt": 100 } }
{ "messageID": "8dca89c2-fd7a-411c-a68d-f13c087f84fa"; "topic": "Messages.SimpleMessage"; "data": { "myInt": 200 } }
org.Team107.MF.Messages
{ "messageID": "c0636b96-dd55-402a-87e3-9fe32761a58a"; "topic": "org.Team107.MF.Messages.ContainerInfo"; "data": { "containerID": "50e1adbb-8940-4613-8be4-abf1227a02aa"; "weight": 0.0; "src": "1e678e22-a008-45a3-93fa-5bbf153c7c0b"; "dest": "f9dee6ba-5d4a-499f-a007-ac47fde19e3a"; "vehicle": "423f3770-1a9b-465d-8917-cb1113abcc55" } }
```
Note that the ids will be different as they are randomly generated.