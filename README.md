# Pain-Patterns
Repo for Pain Patterns, supervised by James Landay and Liz Murnane.

## Getting Started
Welcome to Pain Projects! Thank you for stopping by.

This repository contains the code for an iOS and Android based data collection app and a series of Python scripts to aggregate the data for processing and analysis.

These instructions will get you copies of the iOS and Android app and Python scripts up and running on your local machine for development and testing purposes.

## Setup (github and python)

### Cloning from git
Navigate into the directory on your computer where you would like to place the project folder. Then type the following command to download the project.

```
$ git clone --recursive https://github.com/StanfordHCI/Pain-Patterns.git
```

The ```--recursive``` flag tells git to install the submodules that this project uses. If you have installed the project but it is missing one or more, navigate to the project folder in your terminal, then enter the following two commands to install the project's submodules:

```
$ git submodule init
```
```
$ git submodule update
```

### Committing Changes to git

To commit changes to the repo, save all of your changes locally, then navigate into the project folder from your terminal. Then, type the following commands:

```
$ git add .
$ git commit -m “[message]”
```

Where “message” (inside quotes but no need for the square brackets), is a brief description of the changes you have made since  last committing to the repo. Next, type,

```
$ git push origin master
```

And this should update the repository to the version running locally on your machine. :)

The rule of thumb with which I am familiar is to "commit whenever something works”. This means you probably shouldn't quite commit as often as you save, but once progress has been made (even if it's a small tweak), don’t be hesitant to commit your changes.

When starting work on a project each day, you should confirm that you are working on the latest version of the codebase. To do so, navigate to the project folder in your terminal, then type,

```
$ git pull
```

This will ensure your local copy of the code is up to date with the latest version in the codebase. Make sure to do this in order to avoid merge conflict. :)

### Python Scripts

Before running the Python scripts on your computer, there are some libraries that you may need to install.

To do so, navigate into the project folder, then run the following command (if you wish you may setup a virtual environment beforehand):

```
$ pip install -r requirements.txt
```

After this, you should be able to run the command `$ python3 [filename]` to run the file `[filename].py`.

#### Python file descriptions:
* `read_building_data.py`: Upon being passed in the filename of a .csv file containing building or Qualtrics survey data (since Stanford Qualtrics does not allow API access, to read in the data, one must download the Qualtrics responses as a .csv file, then run this script on the downloaded file), this program creates a Pandas dataframe of the building data.
* `read_sql_remote_data.py`: This program pulls data from a remote SQL database and reads it into a Pandas dataframe. By default, this script is set up to pull data from an AWARE server (though this can be altered by modifying the host and credentials).
* `analysis.py`: This file imports the importent functions from the other two files so its all in one place.

#### Python Data Collection
If you want to use a different file from the analysis file...
The following instructions detail how to collect data from a deployment of the PPS Mobile Client:

1. Create a new Python file for your data analysis. At the top of the file, add the lines:
```
from read_sql_remote_data import gen_df_from_remote_SQL
from read_csv_data import read_csv_data
```

2. Call the functions in your analysis file with the required parameters. Each function call will return a pandas dataframe.

##  iOS Directions

### Prerequisites
This project requires [XCode 9.4.1](https://developer.apple.com/xcode/) and [Python 3](https://www.python.org/downloads/) (links to download pages). All XCode frameworks/pods are included within the repository.

### Orientation

Now that the project submodules are installed, navigate into the iOS directory, then into HPDS-Data. Open up the file with the .xcworkspace extension (not the .xcproject) in XCode. From here, you can make various edits, add features, have a party, etc.

Below, you will find descriptions of some of the key files in the project:

XCode:
* ```AppDelegate.swift```: Contains the code necessary to get the sensors up and running, and sending data to the AWARE server.
* ```ViewController.swift```: Provides the openEmail function (which enables the user to contact the researchers running the study), and the researchKitSurvey function (which starts a survey through ResearchKit).

## Deployment
To deploy the PPS Mobile Client to an iOS simulator or to a live device:

1. Follow the instructions at [this tutorial](http://www.awareframework.com/run-a-study-with-aware/) to set up an AWARE server. (There is currently a test server to which data is forwarded, but it is linked to Michael Cooper's SUID, so you may want to set up another one to test on).

2. Update the ```getUrl() -> String``` function in ```AppDelegate.swift``` to return the url of your new AWARE server. Additionally, update the variable ```email``` under the ```openEmail``` function in ```ViewController.swift``` to an email at which you would like users to be able to reach you.

3. Update the notification request in the ```func createPushNotifications()``` function in ```AppDelegate.swift```. Change the values, such as ```contentTitle: ESM Survey``` used to define each ```request``` variable to contain the desired content and sending time. 
    **Note: Remember to add each request to the notificationCenter in order to send notifications.  

4. Build and run the project in XCode. From XCode, you can set the simulated device on which you would like the project to run. To run on a live device, plug the device into your computer. After a few seconds, the device should become available to select from the menu in the top-left corner (to the right of the play button). Select your device, then run the project.
    **Note: Simulated devices will only register as a device in the AWARE server; this is normal as simulated devices do not         have sensor capabilities.

## Built With
[AWARE Framework iOS](https://github.com/tetujin/AWAREFramework-iOS)  
[ResearchKit](https://github.com/ResearchKit/ResearchKit)

## Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md) for more project context and contribution instructions/guidance!

## Authors
* Joshua Ren
* Raymond Yao
* Michael Cooper
* Alex Weitzman
* Gabe Saldivar
* Andrew Ying

## License

## Acknowledgments
[Michael Cooper](https://github.com/cooper-mj) for his development on the HPDS project, which was used as a base for the development of this app, which can be found here https://github.com/StanfordHCI/Hybrid-Physical-Digital-Spaces.

[Yuuki Nishiyama](https://github.com/tetujin) for his work on the AWARE Framework iOS, and for his AWARE Framework tutorials, which were used in the development of this application.

[Stephen Groom](https://stackoverflow.com/users/2475902/stephen-groom) on StackOverFlow. His [solution to send an email from an iOS app](https://stackoverflow.com/questions/25981422/how-to-open-mail-app-from-swift) was used in this project.

## Tutorials for Reference
Here are a list of tutorials that were used over the course of this project. The hope here is that, if you are not familiar with some of the design elements of the HPDS-Data app, these resources will enable you to quickly bring yourself up to speed.

* [Using AWAREFramework-iOS (library version of AWARE iOS)](http://www.awareframework.com/creating-a-standalone-ios-application-with-awareframework-ios/)

* [Managing a Study with AWARE](http://www.awareframework.com/run-a-study-with-aware/)

* [Stanford CS193P (Tour of XCode, Introduction to Swift)](https://www.youtube.com/playlist?list=PLPA-ayBrweUz32NSgNZdl0_QISw-f12Ai)
