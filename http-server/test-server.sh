#!/bin/sh

# NOTE: this script assumes you have built the project
# and you are running the server, like this:
#
#   java -jar build/lib/*-all.jar

curl -X POST http://localhost:8080/flexmi2plantuml \
  -H 'Content-Type: application/json' \
  -d '{"emfatic": "", "flexmi": "<?nsuri http://www.eclipse.org/emf/2002/Ecore?>\n<package name=\"p1\"/>"}'
