# Rotten Tomatoes Analyzer
## Project 1

## Description
- Project 1 is a Scala console application that is retrieving data using Hive or MapReduce. The job is to build a real-time news analyzer. This application should allow users to view the trending topics (e.g. all trending topics for news related to "politics", "tv shows", "movies", "video games", or "sports" only [choose one topic for project]).
- For this specific project, utilizes data from Rotten Tomatoes to perform queries of certain statistics of movies within streaming services.
- Used IntelliJ to create and run program.

### MVP:
- ALL user interaction must come purely from the console application
- Hive/MapReduce must:
    - scrap data from datasets from an API based on your topic of choice
    - was permitted to use created data rather than API, which did come from Rotten Tomatoes
- Console application must:
    - query data to answer at least 6 analysis questions of your choice
    - have a login system for all users with passwords
        - 2 types of users: BASIC and ADMIN
        - Users should also be able to update username and password
- implement all CRUD operations
- implement bucketing, and partitioning

### Stretch Goals:
- Passwords must be encrypted
- Export all results into a JSON file/ can optional because(changes done)
- find a trend

### Technologies
- IntelliJ
- Hadoop MapReduce
- YARN(by default) 
- HDFS
- Scala 2.11 (or 2.12)
- Hive
- Git + GitHub
