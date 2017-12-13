# a-dfs

All communication between servers happen using HTTP APIs

### Prerequisites for build
1. Java 8
2. Maven, for building

To build and run each module, follow the setup guide for each module, 
then run ```mvn package``` on the module folder. run the code by traversing to the generated ```target``` folder and run the .jar file. 

## filesystemserver

Functionalities: 
1. Storage/modification of files sent from ```client```. Files are stored in a flat structure in location specified in ```StorageProperties.java```
2. When a file is added/changed, its md5 checksum is calculated and stored in a MySQL database managed by the server. This is for quick lookup from client, to figure out if the file must be re-cached by the client or not. Both this information and the last-modified information of file is sent back to client to make this decision.
3. Communicates with ```lockserver``` when a file has been added or deleted, to add or remove the file from lockserver lookup, and keep data consistent.

Setup guide:
1. Modify ```src/main/java/resources/application.properties``` for database setup and server properties; ```src/main/java/resources/config.properties``` for ```lockserver``` properties. (Details on DB and user creation are given in the ```application.properties``` file.)

## lockserver