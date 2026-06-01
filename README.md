# PathFinder

A small JavaFX application for creating and exploring a weighted graph on a map of Europe.

The app lets you:

- open a map of Europe map and graph data
- add places on the map
- create, inspect, and update connections between places
- find a path between two selected places
- save graph data back to `europa.graph`
- export the current map view as `capture.png`

## Requirements

- Java JDK
- JavaFX SDK

## Compile and Run

Set `JAVAFX_HOME` to your JavaFX SDK folder, then run:

```sh
export JAVAFX_HOME=/usr/share/openjfx
javac -d out --module-path "$JAVAFX_HOME/lib" --add-modules javafx.controls,javafx.swing *.java
java -cp out --module-path "$JAVAFX_HOME/lib" --add-modules javafx.controls,javafx.swing PathFinder
```
