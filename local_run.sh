if [ ! -f ~/.ssh/id_rsa ]; then ssh-keygen -C '' -t rsa -N '' -f ~/.ssh/id_rsa ; fi
cp ~/.ssh/id_rsa ./environments/dev/
cp ~/.ssh/id_rsa.pub ./environments/dev/

label=calculator
old_container=$(docker ps -a | grep $label | awk '{print $1}')
if [ ! -z "$old_container" ] ; then docker rm -f $old_container ; fi

docker build -t $label ./environments/dev
container_id=$(docker run -d -v data:/temp/ -p 9024:22 -p 16000:8080 $label)

export ANSIBLE_HOST_KEY_CHECKING=False
ansible-playbook -i local_inventory.yml -k ./environments/dev/playbook.yml
docker cp ./mvnw $container_id:tmp
docker cp ./.mvn/ $container_id:tmp
docker cp ./pom.xml $container_id:tmp
docker cp ./src/ $container_id:tmp

docker exec $container_id bash -c 'cd /tmp/ ; ./mvnw clean verify ; ./mvnw org.pitest:pitest-maven:mutationCoverage ; cp target/calculator-web-*.war /var/lib/tomcat8/webapps/ROOT.war ; mkdir /var/lib/tomcat8/webapps/reports ; cp -r /tmp/target/site/jacoco/ /var/lib/tomcat8/webapps/reports/test-coverage ;  cp -r /tmp/target/pit-reports/*/ /var/lib/tomcat8/webapps/reports/mutation ; ./mvnw failsafe:integration-test -DskipTests=false'
