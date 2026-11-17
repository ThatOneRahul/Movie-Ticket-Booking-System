#!/bin/sh
# Compiles and starts the Movie Ticket Booking System
cd "$(dirname "$0")" || exit 1
mkdir -p out

echo "Compiling..."
javac -d out src/cinema/*.java src/cinema/model/*.java src/cinema/service/*.java src/cinema/ui/*.java || exit 1

echo "Starting the app..."
java -cp out cinema.Main
