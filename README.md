# Ping Pong P2P Game

## Introduction

This is a simple 4 player P2P ping pong game implementation with the following salient features.

* P2P implementation to ensure robustness on node failures/ sudden network disturbance.
* Physics engine for collision detection between multiple balls and interpolation on network delays.
* Interesting powerups and ball speedups to enhance the gaming experience.
* Mouse and keyboard support.
* AI bots to be placeholders when number of players are less than 4.

## Installation

The code contains a simple `makefile` to build, clean and run the game. It uses Java and the [**Java Swing Library**](https://docs.oracle.com/javase/7/docs/api/javax/swing/package-summary.html).

## How to play

* The game throws a menu that asks for Username, suitable port, number of balls and input device.
* The port number needs to be shared among all players to allow them to join.
* All the players must connect to the same LAN / WAN for the game.
* Once in the game, the arrow keys or the mouse (as chosen earlier) can be used to control the paddle.
* Pressing **'A'** when hitting a ball speeds it up, **'D'** slows down the ball and ' '(spacebar) doubles the paddle size.
* All the above powers are limited to 4 uses per game and current availability is displayed on the screen.
* Pickups (the Flash symbol) and Hazards (!) appear on the arena time to time and get triggered on touch.