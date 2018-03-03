clear
echo "CLEARED"

echo "COMPILING"
#compile domain - ok
echo "...DOMAIN"
javac -cp "/home/ec2-user/ll/src/main/java/lifelog/domain:/home/ec2-user/java_bin/json_simple-1.1.jar" -d "/home/ec2-user/ll/target/" /home/ec2-user/localrepos/lifelog/src/main/java/lifelog/domain/*.java

#compile dao
#echo "...DAO"
#javac -cp "/home/ec2-user/ll/src/main/java/lifelog/dao:/home/ec2-user/ll/target/:/home/ec2-user/java_bin/json_simple-1.1.jar" -d "/home/ec2-user/ll/target/" /home/ec2-user/localrepos/lifelog/src/main/java/lifelog/dao/*.java

#compile dao main and util together
echo "...DAO, UTIL, & MAIN"
javac -cp "/home/ec2-user/ll/src/main/java/lifelog/dao:/home/ec2-user/ll/src/main/lifelog/util:/home/ec2-user/ll/src/main/java/lifelog:/home/ec2-user/ll/target/:/home/ec2-user/java_bin/json_simple-1.1.jar" -d "/home/ec2-user/ll/target/" ./Main.java ./dao/*.java ./util/*.java

#compile util
#echo "...UTIL"
#javac -cp "/home/ec2-user/ll/src/main/java/lifelog/util:/home/ec2-user/java_bin/json_simple-1.1.jar" -d "/home/ec2-user/ll/target/" /home/ec2-user/localrepos/lifelog/src/main/java/lifelog/util/*.java


#compile main
#echo "...MAIN"
#javac -cp "/home/ec2-user/ll/src/main/java/lifelog:/home/ec2-user/java_bin/json_simple-1.1.jar:/home/ec2-user/ll/target/lifelog.domain:/home/ec2-user/ll/target/lifelog.dao:/home/ec2-user/ll/target/lifelog.util" -d "/home/ec2-user/ll/target/" /home/ec2-user/localrepos/lifelog/src/main/java/lifelog/*.java


echo "RUNNING"
java -cp "/home/ec2-user/ll/target/:/home/ec2-user/java_bin/json_simple-1.1.jar" lifelog.Main
