# DevopsCalculator

#### Forked from [activelylazy/calculator-web](https://github.com/activelylazy/calculator-web)

#### Functionality:
- A simple webapp calculator with history

#### Uses:
- Java (maven)
- PostgreSQL (for storing history)

#### How to run:
```
./mvnw clean package 
./mvnw failsafe:integration-test -DskipTests=false
sudo cp <project_dir>/target/calculator-web.war /var/lib/tomcat8/webapps/ROOT.war 
```

#### Tests:
- Unit tests: 20
- Integration tests:
    - 4 for PostgreSQL
    - 6 for testing frontend with Selenium

#### Provides environments (vagrant/ansible):
- dev
- test
- stage
- prod

#### [Integration server](https://github.com/kochetov-dmitrij/IntegrationServer) (GitLab, Jenkins)

#### Screenshot:
![Screenshot](https://media.discordapp.net/attachments/471031073556529171/691625995807227965/2020-03-23_15.33.55.png "Screenshot")
