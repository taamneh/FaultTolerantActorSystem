FaultTolerantActorSystem
========================
A simple application that shows how event sourcing and command sourcing techniques are used in Akka.

- This program was designed to run on a single machine.

- all the persistent messages will be saved in a built-int local Database. 

- run "FSMCommandSourcing.scala" to test command sourcing 

- run "FMSEventSourcing.scala" to test event sourcing

- Both programs will ask you to enter a grade

- Type "Request" to get the GPA

- Type "Throw" to fail the GPA caculatot Actor and to see how the program will react by reading from journal 

- To free the journal, just delete "Journal" Folder
