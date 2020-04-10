JFLAGS = -g -cp
JC = javac
JAVA = java
CONTROLLER = Controller
CLASSPATH = '.:lib/json-simple-1.1.jar'
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $(CLASSPATH) *.java

CLASSES = \
    ToJson.java \
    Variable.java \
    Rule.java \
    Reader.java \
    User.java \
    Evaluator.java \
    Server.java

default: classes

classes: $(CLASSES:.java=.class)

