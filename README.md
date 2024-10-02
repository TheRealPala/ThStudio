### TH STUDIO
Software engineering project

## Scaffold
- Be careful to install all the dependecies: 
    - <b>docker</b> 
    - <b>docker-compose</b>
    
    If you do not have one of this dependencies, you must install them.  
    Example on an apt based linux distro (tried on Ubuntu 22.04 LTS):
    - `apt update` --> Update repository (run it only if you need to)
    - `apt install docker.io` --> Install docker
    - `apt install docker-compose` --> Install docker-compose
    
    <b>
    Be careful, in order to use the docker deamon with an ordinary user (in a UNIX compliant shell) you MUST add it to the docker group and let the shell reload the groups list (a reboot is highly recommended)  
    
    To add an user to a group you have to run  `sudo usermod -aG name_group name_user`
    </b>  

- Give to scaffold.sh the execution privilege: `chmod +x scaffold.sh`
- Run the scaffold script with a user capable of run docker deamon: `./scaffold.sh`