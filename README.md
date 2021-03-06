# DevopsCalculator

#### Forked from [activelylazy/calculator-web](https://github.com/activelylazy/calculator-web)

#### Functionality:
- A simple webapp calculator with history

#### Uses:
- Java (maven)
- PostgreSQL (for storing history)

#### How to run:
- Install Docker
- `sudo sh local_run.sh`
- See the logs: building of envs, building of app, unit/acceptance tests 
- See reports: 
  - http://localhost:16000/reports/test-coverage
  - http://localhost:16000/reports/mutation
- Open the app http://localhost:16000

#### Tests:
- Unit tests: 20
- Integration tests:
    - 4 for PostgreSQL
    - 6 for testing frontend with Selenium

#### Provides environments (Dockerfile + ansible):
- dev
- stage
- prod

#### [Integration server](https://github.com/kochetov-dmitrij/IntegrationServer) (GitLab, Jenkins, Artifactory)

#### Screenshot:
![Screenshot](https://media.discordapp.net/attachments/471031073556529171/691625995807227965/2020-03-23_15.33.55.png "Screenshot")
