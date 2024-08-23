## Local Twitter application
This is a conceptual representation of the Twitter (X) application created for learning puproses for students in Algonquin College for course CST8277 "Enterprise Application Programming". The goal of this project is to demonstrate pros and cons of the microservice architecture in action and let student create the same application by themselves in 4 major steps:
1. General design
2. Data model
3. Impelementation
4. Securing applications

### Build & Run

To build the project, follow the steps below.
1. Install prerequisites
  - JDK 17 with `JAVA_HOME` pointing to the right version
  - Gradle 8.7
  - Docker

2. Clone the repository from GitHub
    ```shell
    cd $HOME
    git clone https://github.com/eugenezimin/bird.git
    ```
2. Go into the folder
    ```shell
    cd $HOME/bird
    ```

3. Set up the database by running the provided SQL script.
    ```shell
    docker run -e MYSQL_ROOT_PASSWORD=passw \
    -d --name bird \
    -v bird-db-data:/var/folders/mysql/data \
    -v ./database:/database -p 3306:3306 \
    mysql:latest
    ```

4. Create databases for MySQL and then tables. After that import data there.
    ```shell
    docker exec -it bird mysql -u root -ppassw -e "create database ums; create database twitter;"
    docker exec -it bird mysql -u root -ppassw -e "use ums; source /database/ums.sql"
    docker exec -it bird mysql -u root -ppassw -e "use twitter; source /database/twitter.sql"
    ```

5. Build your Java applications using Gradle
    - UMS service
        ```shell
        cd $HOME/bird/ums
        gradle build
        ```
    - Messaging service
        ```shell
        cd $HOME/bird/twitter
        gradle build
        ```

6. Start the application and check how it works
    - UMS service
        ```shell
        cd $HOME/bird/ums/build/libs/
        java -jar ums-1.2.jar
        ```
    - Messaging service
        ```shell
        cd $HOME/bird/ums/build/libs/
        java -jar twitter-1.2.jar
        ```

### Verify How It Works
As a result you should have 2 separate services running on your local machine using ports `9000` and `9001` accordingly. Import Postman collections from the `requests` folder into your Postman/Hopscotch/Insomnia client to check how it works.

## Contributing 
Contributions to this repository are welcome! If you would like to add a new code or improve an existing one, please follow these steps: 
1. Fork the repository. 
2. Create a new branch for your changes: `git checkout -b my-new-branch` 
3. Add your article or make changes to an existing one. 
4. Commit your changes: `git commit -m "Add new code"` 
5. Push your changes to your forked repository: `git push origin my-new-branch` 
6. Open a pull request in this repository, describing your changes. 

Please ensure that your article is well-written, accurate, and includes relevant comments. 

## License 
This repository is licensed under the [BSD 2-Clause License](LICENSE).
