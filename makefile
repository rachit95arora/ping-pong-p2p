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

build:
	javac Player.java
	javac SendClass.java
	javac Powerup.java
	javac Timestamp.java
	javac Receiver.java
	javac Pong.java
	javac PongWindow.java
	javac JPanelWithBackground.java
	javac LaunchGame.java

clean:
	rm Player.class
	rm SendClass.class
	rm Powerup.class
	rm Ported.class
	rm Timestamp.class
	rm Receiver.class
	rm Pong.class
	rm PongWindow.class
	rm JPanelWithBackground.class
	rm LaunchGame.class
	rm LaunchGame'$$'keyboardButtonListener.class
	rm LaunchGame'$$'mouseButtonListener.class
run:
	java -Djava.net.preferIPv4Stack=true LaunchGame
