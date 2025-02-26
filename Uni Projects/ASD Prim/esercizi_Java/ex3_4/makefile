all: AbstractQueue.class PriorityQueue.class AbstractEdge.class AbstractGraph.class Edge.class Graph.class Prim.class PriorityQueueTest.class GraphTest.class PrimTest.class

AbstractQueue.class: AbstractQueue.java
	javac -d . AbstractQueue.java

PriorityQueue.class: PriorityQueue.java
	javac -d . PriorityQueue.java

AbstractEdge.class: AbstractEdge.java
	javac -d . AbstractEdge.java

AbstractGraph.class: AbstractGraph.java
	javac -d . AbstractGraph.java

Edge.class: Edge.java
	javac -d . Edge.java

Graph.class: Graph.java
	javac -d . Graph.java

Prim.class: Prim.java
	javac -d . Prim.java

PriorityQueueTest.class: PriorityQueueTest.java
	javac -d . -cp .;junit-4.13.2.jar;hamcrest-core-1.3.jar PriorityQueueTest.java

GraphTest.class: GraphTest.java
	javac -d . -cp .;junit-4.13.2.jar;hamcrest-core-1.3.jar GraphTest.java

PrimTest.class: PrimTest.java
	javac -d . -cp .;junit-4.13.2.jar;hamcrest-core-1.3.jar PrimTest.java
