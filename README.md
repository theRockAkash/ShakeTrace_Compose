
# ShakeTrace Compose Library 📱 [![](https://jitpack.io/v/theRockAkash/shaketrace.compose.svg)](https://jitpack.io/#theRockAkash/shaketrace.compose)

ShakeTrace Compose is a utility library for Android applications that provides an interactive way to log and view HTTP requests and responses. By simply shaking your device, you can trigger a display of the logged network calls. This can be particularly useful during the development and debugging stages.

For XML-based applications, use [ShakeTrace](https://github.com/theRockAkash/ShakeTrace) for same features.


## Add dependencies in your project

#Settings.gradle file or Project level build.gradle file
```groovy
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }  // add this line
		}
	}
```

#build.gradle file (app level)
```kotlin
dependencies {
	implementation("com.github.theRockAkash:shaketrace.compose:v1.5.0")  // replace with latest version 
   }
```



## Main Features 🌟

- **Network Logging:** Logs all HTTP requests and responses made by the app. This includes the request method, headers, body, and the corresponding response from the server.

- **Shake to View Logs:** When the device is physically shaken, the library displays a screen with the logged network calls. This provides a quick and convenient way to check network logs without needing to connect the device for debugging.

- **Easy Integration:** The library provides a simple API for integration with existing Android projects. It uses common Android libraries and components, making it compatible with most Android projects.

### For Logs, add this code in your app
#add PrettyLoggingInterceptor in your http client
```kotlin

 if (BuildConfig.DEBUG) {
            val prettyInterceptor = PrettyLoggingInterceptor.Builder()
                .setLevel(Level.BASIC)
                .setCashDir(context.cacheDir)   //set casheDir to see logs in app on shake event
		.setCashDir(File("/data/user/0/{your app packge name}/cache"))   //or pass file and replace {your app package name} with your actual app package name.
                .log(VERBOSE)                  
            httpClient.addNetworkInterceptor(prettyInterceptor.build())   // add prettyInterceptor in http client
        }
```


#Add in application class
```kotlin
  override fun onCreate() {
        super.onCreate()
 	if (BuildConfig.DEBUG)
           ShakeTrace.init(this) // add this line in onCreate function of application class only if you want to see logs on shake
    }
```

## Note 📝

While the logging feature of this library is intended for development and debugging purposes and should be disabled in production builds.



Happy coding! 🚀
