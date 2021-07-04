
build:
	cd src; javac db/*.java -classpath ../lib/ -d ../bin/
	cd src; javac middleground/*.java -classpath ../bin/ -d ../bin/

deploy: build
	cp bin/db/* ~/apache-tomcat/webapps/ROOT/WEB-INF/classes/db/
	cp bin/middleground/* ~/apache-tomcat/webapps/ROOT/WEB-INF/classes/middleground/

clean: 
	rm -r bin/*

jsh: build
	jshell --class-path="./bin:./lib/sqlite-jdbc-3.36.0.jar" prepare.jsh