all:
	javac Player.java
	javac SendClass.java
	javac Powerup.java
	javac Timestamp.java
	javac Receiver.java
	javac Pong.java
	javac PongWindow.java
	javac JPanelWithBackground.java
	javac LaunchGame.java
	java -Djava.net.preferIPv4Stack=true LaunchGame

clean:
	rm Player.class
	rm Powerup.class
	rm SendClass.class
	rm Pong.class
	rm PongWindow.class
	rm Timestamp.class
	rm Test.class
	rm Receiver.class
	rm Ported.class

run:
	java -Djava.net.preferIPv4Stack=true Test
