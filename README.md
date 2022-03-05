# Lamport algorithm for distributed systems
This project shows an implementation of the Lamport algorithm for distributed datasources that share access to one datastore. (since this was a study assignment, some parts are still in german... sorry.)


## Project description
- 3 distributed datasources share access to a datastore. They run 
  asynchronously, but their write access to the datastore is coordinated decentralized by means of the Lamport algorithm.
- The write access from Datasource to Datastore takes place by means of RPC Thrift.
- The communication between the datasources is done via TCP.

### Derived rules for the application of the Lamport algorithm.
- The randomly assigned process id (pid) serves as a tiebreak   
- Any send: C = C+1
- Insert data packet into msg-queue: C = C+1
- Persist in datastore: C = C+1
- Remove datapacket from msg-queue : C = C+1
- Receive datapacket: C = max(Cself, Cother) + 1

### Deviation from Leslie Lamport's five rules.
Basically, Lamport's "implementation rules" (IR1 and IR2) from p. 560 were followed. However, the project requirements allow the assumption that the network is stable and that there are no server failures. This allows adjustments to the five rules that define the algorithm (p. 561):
- Since a successful delivery may always be assumed, it was decided to omit the sending of a "releases resource message".
- Likewise the removal of the "request messages" takes place for the same reason directly 
  after sending the ACKNOWLEDGE.

## Run
```bash
[sudo] docker-compose up --build` oder `[sudo -E] make start` (Starten des Containers)
[sudo] docker-compose down` oder `[sudo -E] make stop`(Beenden des Containers)
```

#### Use of different CSV files
- To use different CSV files, they must first be placed in the folder: `src/main/resources`. 
- Then the desired file name must be entered in the `docker-compose.yml` under `'file=/usr/app/*.csv'`: e.g. `'file=/usr/app/data.csv'`.

## Tests
The test protocol can be found under: `Test protocol/Test protocol_VS_mEt.pdf`

## Legal Information
The project is subject to the license GNU GLP V3

## Acknowledgements / Citations
- *https://gitlab.fbi.h-da.de/m.bredel/csv-reader/-/blob/master/java/src/main/java/de/hda/fbi/ds/mbredel/Main.java* provided orientation for the implementation of the CSVReader
- *https://github.com/swairshah/LamportMutex* and *https://github.com/tuvtran/LamportClock* provided orientation for possible project structure
- *Lamport, Leslie: Time, Clocks, and the Ordering of Events in a Distributed System, in: Communications of the ACM 21,7, 1978.* provided the theoretical background.
