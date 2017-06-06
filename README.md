# AndroidMaryTTS 

AndroidMaryTTS is an open source Android **offline** text to speech application, built on top of MaryTTS. Can use **own** HMM-based voice in any android application with using this lib. Just need only generated **HMM-voice** file. Just create your own hmm-based voice model with using **MaryTTS** and share with us. In final listen yourself as tts speaker with using our lib. Already tested this lib with the version of android **lollipop**.

#How to use: 


1) Add below script to build.gradle(app) file : 
```
  android {
  	    defaultConfig {
        		multiDexEnabled true
        		packagingOptions {
        		    exclude 'META-INF/LICENSE.txt'
        		    exclude 'META-INF/NOTICE.txt'
        		    exclude 'META-INF/LICENSE'
        		    exclude 'META-INF/NOTICE'
        		    exclude 'META-INF/DEPENDENCIES'
  	     	      }
  	    }
  }
```
2) Also, other one to build.gradle (module) where tts will use : 
```
	dependencies {
	    compile 'com.marytts.android:marylib:1.0.1'
	}
```

3) Load code marytts-android voise and language models on startup your project. It takes a few seconds. But in future will be so fast : 
```
	MaryLink.load(Context context); 
```
4) Last one to speak or stop what you write : 
```
 	MaryLink.getInstance().startTTS(text);
	
	MaryLink.getInstance().stopTTS();
```
  
# Plan 

#### Will do significant refactoring and modifications the core code MaryTTS 

1. Create new android app and add Marytts core code as module :+1:
2. Change or refactor marytts source files to support android OS :+1:
3. Publishing Gradle AndroidMaryTTS library to jCenter and maven repository :+1:
4. Adding other languages
5. Marytts Android client [demo](https://github.com/AndroidMaryTTS/AndroidMaryTTS-Client) :+1:
6. Creating **TTS engine** using AndroidMaryTTS
7. Optimising AndroidMaryTTS module and make more user friendly module


# Contribute

1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -am 'Added some feature')
4. Push to the branch (git push origin my-new-feature)
5. Create new Pull Request
