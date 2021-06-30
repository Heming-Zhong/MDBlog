
build:
	cd src; javac db/*.java --class-path ../lib/ -d ../bin/
	cd src; javac middleground/*.java --class-path ../bin/ -d ../bin/

deploy: build
	cp bin/db/* ~/apache-tomcat-10.0.6/webapps/ROOT/WEB-INF/classes/db/
	cp bin/middleground/* ~/apache-tomcat-10.0.6/webapps/ROOT/WEB-INF/classes/middleground/