# citrus-pack

## Intro

This is a little piece of Processing art that I published on [tumblr](http://robuuuu.tumblr.com/post/62132504042/citrus-packing-i-became-obsessed-with-the).
Essentially, the program randomly generates a bunch of citrus fruit slices and uses physics-like forces to pack them into the window.

## Structure

The `.pde` file contains the standard Processing event handlers and so on, and also handles collisions and force application. The `Citrus.java` contains the Citrus class which contains all of the code for drawing the actual citrus slices and updating position based on velocity.
