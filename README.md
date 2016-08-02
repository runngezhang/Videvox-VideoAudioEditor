# SOFTENG206-Project
Final project for SOFTENG206 course, worth majority of grade.

Audio and commentary creator for video files.  
Requires Linux with Festival voice synthesis and ffmpeg installed.  Vidivox report can be used to see screenshots of application and design decisions, whereas the manual may be viewed for details on usage.

To run VIDIVOX navigate to the VIDIVOX jar directory and use  
`java -jar vidivox`  
or right click and run with java if oracle jre is installed.  

KNOWN BUGS:
1. Closing the project and reopening causes preview audio to not play.  
2. Adding audio longer than video will cause the preview audio to behave unpredictably.  Video export will still work.  
3. Using files with the same filename will cause all files with that name to use the audio of the last added file of that name.  
