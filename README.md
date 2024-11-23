
# ShakeTrace Library 📱 [![](https://jitpack.io/v/theRockAkash/ShakeTrace.svg)](https://jitpack.io/#theRockAkash/ShakeTrace)

ShakeTrace is a utility library for Android applications that provides an interactive way to log and view HTTP requests and responses. By simply shaking your device, you can trigger a display of the logged network calls. This can be particularly useful during the development and debugging stages.

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
```groovy
dependencies {
	implementation 'com.github.theRockAkash:ShakeTrace:latestVersion'  // replace with latest version e.g. 1.3.0
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

## Utility Functions 🛠️

- **Toasty:** An easy way to show toast messages. It ensures that if there's already a toast message being displayed, it cancels it before showing the new one.

	```kotlin
	 Utils.toasty(this, "Button clicked!")
	```
 
- **getFormattedDateTime(utcString: String,outputFormat:String):** Converts a UTC date string to another date format.

	```kotlin
 	val utcDateString = "2022-01-15T10:15:30.00Z"  // UTC date string
 	val outputFormat = "dd MMM yyyy, hh:mm a"  // Desired output format
 	val formattedDate = Utils.getFormattedDateTime(utcDateString, outputFormat)	
  	println("Formatted date: $formattedDate")		// Output=> Formatted date: 15 Jan 2022, 10:15 AM
	```
 
- **getFormattedDateTime(date: String,inputFormat:String,outputFormat:String):** Converts a date string from one format to another. For example, if you have a date in "15/01/2022" format and want to convert it to "15 Jan 2022", you can use this function.

	```kotlin
	val dateString = "15/01/2022"  // Date string
 	val inputFormat = "dd/MM/yyyy"  // Input format
 	val outputFormat = "dd MMM yyyy"  // Desired output format
	val formattedDate = Utils.getFormattedDateTime(dateString, inputFormat, outputFormat)
 	println("Formatted date: $formattedDate")	// Output=> Formatted date: 15 Jan 2022
	```

- **getError:** Extracts an error message from a `ResponseBody` object. This can be useful when handling network request errors.

	```kotlin

	fun showErrorToast(errorBody: okhttp3.ResponseBody?) {
 	    val errorMessageKey = "error"
 	    val errorMessage = Utils.getError(errorMessageKey, errorBody)
	    Utils.toasty(this, errorMessage)
	}
	```

These utility functions provide additional functionality to your application, making it easier to display toast messages and format date strings.

## DataHelper Class 📦

The `DataHelper` class is a sealed class used to represent different states during network calls. It has three subclasses: `Success`, `Error`, and `Loading`. Each subclass represents a different state of a data operation:

- **Success:** Represents a successful operation and contains the resulting data.
- **Error:** Represents a failed operation and contains an error message.
- **Loading:** Represents an operation in progress.

```kotlin

val commonResponseLiveData by lazy { MutableLiveData<DataHelper<CommonResponse>>() }

suspend fun isUserActive(
    req: Long
) {
    if (!networkManager.isNetworkAvailable()) {
        commonResponseLiveData.postValue(DataHelper.Error("No Internet Connection"))
        return
    }
    commonResponseLiveData.postValue(DataHelper.Loading())

    val response = api.isUserActive(req)
    if (response.isSuccessful && response.body()?.status == true) {
        commonResponseLiveData.postValue(DataHelper.Success(response.body()!!))
    } else if (response.body()?.message != null) {
        commonResponseLiveData.postValue(DataHelper.Error(response.body()!!.message))
    } else commonResponseLiveData.postValue(DataHelper.Error(Utils.getError("message",response.errorBody())))

    }

// Observe the response
commonResponseLiveData.observe(this) {
    when (it) {
        is DataHelper.Error -> {res->
            Utils.toasty(this, res.msg)
        }
        is DataHelper.Loading -> { }
        is DataHelper.Success -> {res-> }
    }
```

This class can be used independently to handle different states of network calls in your app.

## Note 📝

While the logging feature of this library is intended for development and debugging purposes and should be disabled in production builds, the utility functions (like toasty, getFormattedDateTime, and getError) & DataHelper Class are designed to be used in both development and production environments.



Happy coding! 🚀
