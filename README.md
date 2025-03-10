### TH STUDIO
Software engineering project

## Scaffold

### Container
- Be careful to install all the dependecies: 
    - <b>docker</b> 
    - <b>docker-compose</b>
    
    If you do not have one of this dependencies, you must install them.  
    Example on an apt based linux distro (tried on Ubuntu 22.04 LTS):
    - `apt update` --> Update repository (run it only if you need to)
    - `apt install docker.io` --> Install docker
    - `apt install docker-compose` --> Install docker-compose
    - `apt install mysql-client` --> Install mysql client

**Be careful, in order to use the docker deamon with an ordinary user (in a UNIX compliant shell) you MUST add it to the docker group and let the shell reload the groups list (a reboot is highly recommended)**
    
**To add an user to a group you have to run  `sudo usermod -aG name_group name_user`**  

- Give to scaffold.sh the execution privilege: `chmod +x scaffold.sh`
- Run the scaffold script with a user capable of run docker deamon: `./scaffold.sh`

### Codebase
**The codebase has been created using maven, you must use it to build the project.**
- Install maven from cli (in an apt based linux distro) with the following command: ` sudo apt install maven `
- Rename .env.dist file into .env (you can find it in `config/.env.dist`), than fill it with the db access's data
- Move into the project's root (the one with the pom.xml file)
- Install all the maven dependencies: `mvn install`
- Useful command: If you got some problem with the maven dependencies, you can try to clean the maven cache: `mvn clean install -U` or reinstall all the dependencies: `mvn dependency:purge-local-repository`

### Test
To run all the unit test of the project, run : `mvn test`