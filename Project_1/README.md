# Rotten Tomatoes Analyzer
## Project 1

## Description
- Project 1 is a Scala console application that is retrieving data using Hive or MapReduce. The job is to build a real-time news analyzer. This application should allow users to view the trending topics (e.g. all trending topics for news related to "politics", "tv shows", "movies", "video games", or "sports" only [choose one topic for project]).
- For this specific project, utilizes data from Rotten Tomatoes to perform queries of certain statistics of movies within streaming services.
- Used IntelliJ to create and run program.

### MVP:
- ALL user interaction must come purely from the console application
- Hive/MapReduce must:
    - was permitted to use created data rather than API, which did come from Rotten Tomatoes website
- Console application must:
    - query data to answer at least 6 analysis questions of your choice
    - have a login system for all users with passwords
        - 2 types of users: BASIC and ADMIN
- implement all CRUD operations

### Stretch Goals:
- Users will be able to update username and password
- Use Rotten Tomatoes API
- implement bucketing and partitioning (data from file not big enough to use these)
- Passwords must be encrypted
- Export all results into a JSON file/ can optional because(changes done)
- find a trend

### How to Start
- While in directory of your choice using a terminal with git installed:
```
git init
git remote add Project_1 https://github.com/EarthBoundMic/Revature
git fetch Project_1
git checkout Project_1/main -- Project_1
```
- Open directory using IntelliJ
- After build, run the program

### How to Use
- Initial boot up of program will ask for the creation of an admin account
	- follow instructions to create admin user
- Menus are numerical based so only use numbers specified in each menu
	- do not use anything other than numbers
- For admin, user data option currently doesn't work.
	- Menu is there but all options other than go back will error.
- There is an example without logging in to start
- Access all the queries by making an account or logging into an existing one.
- Note that there is no log out option currently.
	- exit program to log out

### Technologies
- IntelliJ
- Hadoop MapReduce
- YARN(by default) 
- HDFS
- Scala 2.11 (or 2.12)
- Hive
- Git + GitHub
