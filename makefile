all:
	javac Player.java
	javac SendClass.java
	javac Pong.java
	javac PongWindow.java
	javac Test.java
	java -Djava.net.preferIPv4Stack=true Test

clean:
	rm Player.class
	rm SendClass.class
	rm Pong.class
	rm PongWindow.class
	rm Test.class
	rm Receiver.class

run:
	java -Djava.net.preferIPv4Stack=true Test