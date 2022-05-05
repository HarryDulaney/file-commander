# File-Commander
File-Commander is a desktop application for performing file conversions. 
It is unique in that it persists user preferences between sessions.
The user creates settings profile and File Commander performs conversions based on that.
----
![ScreenShot](https://raw.githubusercontent.com/HarryDulaney/file-commander/master/screenshots/ScreenshotDarkTheme.png)

## How to run File-Commander:
You can execute the File-Commander.jar on any system with Java 8 or later installed. 
+ On Windows:
  + Use the .bat file included with the Release downloads to run the application. 
+ On Mac/ Linux:
  + Open a command prompt in the containing folder and run: java -jar File-Commander-0.1.0.jar
____
## What can it do?
## File Commander supports the following conversions:
### Word Document Types:
- DOCX ---> PDF
- PDF --> text extraction to --> DOCX
- PDF ---> DOCX (Windows only)
### Excel Workbook / Spreadsheet Types:
- XLSX ---> CSV
- CSV ---> XLSX
### Image Types: 
#### The user chooses a default background color in the event of transparent background convert to a non-transparent supporting image format. 
- PNG ---> JPG/ GIF/ BMP
- JPG ---> PNG/ GIF/ BMP
- GIF ---> PNG/ BMP/ JPG
- BMP ---> PNG/ GIF/ JPG
 ____
 ## Future Features
 - Batch file conversions
 - Create OS specific installers/packaging
  + Launch4J?
 - Reformat Main view without ugly file preferences. 
  + Implement default preferences.
  + Move customizing preferences view to it's own tab. 
