Following are the steps to be followed to install Argo app
----------------------------------------------------------

DB Installation

1) Download and extract MongoDB zip file (Its free) 
2) Run the Team 7_Database.txt on mongo shell to insert jdaadmin, buyer and supplier users 





How to run the application
------------------------------

1)Extract the com.jda.bsnet.zip file.
2)goto build/libs directory.
3)extract the "com.jda.bsnet-1.0.war" 
4)update <APP_FOLDER>/WEB-INF/classes/bsnetserver.properties
	1) bsnet.jettyjson.loc - location of the bsnetserver.json file
		ex - bsnet/WEB-INF/classes/bsnetserver.json
	2) update the property dbUri=mongodb://localhost:27017/bsnet
		change the string "localhost" to IP address of the mongodb server.

3) goto the folder where the application is extracted.
4) create a folder "logs/access" where the application is extracted
4) Execute the below commands to run the server.

java -cp .\WEB-INF\lib\*;.\WEB-INF\classes\ com.jda.bsnet.server.BsnetServerMain

5) check for the log "BSNET Server up"

6) access the URL http://<SERVER_IP>:4080/bsnet

7) Login with jdaadmin/jdaadmin and upload “Item.csv”.  You can upload your own items but we have some nice images for these items so you can see cool and attractive market place







