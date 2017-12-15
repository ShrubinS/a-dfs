# a-dfs

All communication between servers happen using HTTP APIs. The servers are built using Spring boot and runs on an embedded tomcat server (configurable using ```application.properties```). Data access is managed using Spring Data JPA on MySQL servers. 

### Prerequisites for build
1. Java 8
2. Maven

To build and run each module, follow the setup guide for each module, then run ```mvn package``` on the module folder. run the code by traversing to the generated ```target``` folder and running the .jar file. 

### Features implemented
1. Transparent and distributed file access.
2. Locking service
3. File System service (upload-download model)
4. Directory service (```nameserver```) providing optional horizontal scaling of ```filesystemserver``` instances.
5. Caching on client-side

## client

Dependencies:
1. Address information (ip and port) of ```nameserver``` instance.
1. Address information (ip and port) of ```lockserver``` instance.

Functionalities:
1. Provides transparent file access to remote servers. Users may query a remote server using e.g. ```w /dir1/file.txt``` or ```cd dir1/dir2``` to navigate created folders. Current directory is shown in the command line program as prefix, ie.
```@/Dir1/``` or ```@/```. File accesses include write, append and read, all of which is **routed through a local cache**.

File operations overview:

* **Read operation** will first attempt to read from local cache, then compare the md5 hash with file on server. if hash is same, this file is returned. If file is not same, the file is downloaded into the cache from server. **Read operation** is attempted again. If cache is empty, file download is attempted from server. If server does not contain a lookup for the file, the file does not exist. An example command: ```r /Dir/file```
* **Write operation** overwrites existing file (if exists) or creates a new file and pushes to server. Writes are first made to cache then an asynchronous REST call is made to a) ```lockserver``` requesting lock. b) ```fileserver``` writing file. c) ```locksever``` removing lock. If a lock is already present on the file, the request waits ```500 ms``` before attempting to request again. This process is repeated till the lock is acquired, or until ```lock.max_tries``` times retries are exhausted. ```lock.max_tries``` is configurable using ```config.properties```. An example command: ```w /Dir1/Dir2/newfile.log "some text here"```
* **Append operation** performs the **Read operation**, then proceeds to append text by perfroming the **write opertation** without overwriting the file. An example command: ```a newfile.log "some more text here"```

Setup guide:
1. Modify ```src/main/java/resources/config.properties``` for providing ```nameserver``` and ```lockserver``` details. Can configure other parameters such as ```lock.max_tries```, default client directory on the remote server using ```client.wdir``` and local directory for cache storage (temp files) using ```config.localtempdir```.

## filesystemserver

Dependencies:
1. Address information (ip and port) of ```lockserver``` instance.

Functionalities: 
1. Storage/modification of files sent from ```client```. Files are stored in a flat structure in location specified in ```StorageProperties.java```
2. When a file is added/changed, its md5 checksum is calculated and stored in a MySQL database managed by the server. This is for quick lookup from client, to figure out if the file must be re-cached by the client or not. Both this information and the last-modified information of file is sent back to client to make this decision.
3. Communicates with ```lockserver``` when a file has been added or deleted, to add or remove the file from lockserver lookup, and keep data consistent.

Setup guide:
1. Modify ```src/main/java/resources/application.properties``` for database setup and server properties; ```src/main/java/resources/config.properties``` for ```lockserver``` connection properties. Details on DB and user creation are given in the ```application.properties``` file.

## lockserver

Functionalities:
1. All file modification requests from ```client``` will first require a lock from the ```lockserver```. List of active locks, with timestamps of when the locks are aquired, is stored in a mysql database managed by the lockserver.
2. New files will be registered with the lockserver when it has been written to the server. If a file is not found in the ```lock_db```, it is assumed that this is a new file and no lock will be used to write this file.
3. Additional logic is used by lockserver for determining if the lock is to be given. If another client requested a lock and ```config.maxtime``` minutes have passed, the lock may be granted to this process. To keep track of this, the timestamp of lock acquisition is stored with the file on the database.
4. Internal synchronization of the lock access is managed by the database.

Setup guide:
1. Modify ```src/main/java/resources/application.properties``` for database setup and server properties; ```src/main/java/resources/config.properties``` for setting ```maxtime``` (affecting time before lock expires). Details on DB and user creation are given in the ```application.properties``` file.


## nameserver

Dependencies:
1. Address information (ip and port) of all running ```filesystemserver``` instances.

Functionalities:
1. Maps the file requests (with directory information), from client to the ```filesystemserver```, and responds to the client with a modified separator, which will be used by filesystem to store/retrieve the files. The server details of the server holding the file is also returned
e.g. If the user requests to ```r /Dir1/Dir2/file.txt```, this is interpreted as ```sep#Dir1sep#Dir2sep#file.txt``` and returned back to client with address information of ```filesystemserver```
2. Using the ```config.properties``` may be used to store different directories in different instances of ```filesystemservers```. This may be used to allow for scale, as all requests from client to file system is managed by the nameserver.

Setup guide:
1. Modify ```src/main/java/resources/application.properties``` for server properties; ```src/main/java/resources/config.properties``` for mapping directory with ```filesystemserver``` instances.




