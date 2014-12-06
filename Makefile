all: NetworkTablesClient.class

NetworkTablesClient.class: NetworkTablesClient.java
	javac NetworkTablesClient.java -cp .

clean:
	rm -f *~ NetworkTablesClient.class NetworkTablesClient.jar

run:
	java NetworkTablesClient -cp .

