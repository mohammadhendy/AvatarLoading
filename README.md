# Avatar Loading

This project is a library for easier loading of Avatar images in your app with the following features:
1. Load images from Url and shape it in a circle
2. Placeholder image is displayed until loading completes
3. Error images is displayed when failed to load the Avatar image
4. Circular Loading Progress around the image
5. Configurable Memory and Disk caching with ability to set max of size and count that cache don't exceed  

## How to build the project

In Android Studio, just open the `Avatar Loading` project in Android Studio, let the IDE sync and then click the *Run* button.

## Usage

##### Initialize
In your Application's class onCreate, place the following to initialize Avatar. As you see you must provide cache configuration limits to ```init``` function
* Note that Avatar is a Singleton so it must be initialized once otherwise you will get an exception if you tried to call init more than once time 
```KOTLIN
    Avatar.init(
        context = this,
        maxDiskCacheItemCount = maxDiskCacheCount,
        maxDiskCacheSizeKBytes = maxDiskCacheSizeKB,
        maxMemoryCacheItemCount = maxMemoryCacheCount,
        maxMemoryCacheSizeKBytes = maxMemoryCacheSizeKB
    )
```
##### Loading Avatar into ```ImageView```
```KOTLIN
    Avatar.load("https://picsum.photos/id/0/1024/1024/")
        .placeholder(R.drawable.ic_placeholder)
        .errorImage(R.drawable.ic_error)
        .showProgress(true)
        .memoryCache(true)
        .diskCache(true)
        .into(avatar_image_view)
```

##### Let Avatar be Lifecycle aware
For automatic canceling of pending loading requests Avatar must be set as Lifecycle Observer to your Activity.
```KOTLIN
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)       
        lifecycle.addObserver(Avatar)        
        ...
    }
    
    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(Avatar)
    }
```
## Solution Description
I was inspired by Libraries like Picasso when designing the usage interface of my library. I can describe my solution in terms of how i get over the following problems:
* **Downloading the image**: 
Considering not to use any third party libraries, i had to use the native **HttpUrlConnection** instead, 
so i created my own **ImageDownloader** that downloads the image and handle all possible errors 
and report either the downloaded image data **ByteArray** or **LoadingError**.

* **Decoding and Transformation**:
I created **BitmapUtils** class to handle decoding of image data into **Bitmap** and also load placeholder drawable.
I added also some functions to down sample the image according to the required image view size or a default size if can't get image view size.
I found that most of time image view size is not set because view is not measured yet so i fall back to defaults.
This problem can be solved by adding **OnGlobalLayoutListener** to listen to layout changes and get exact size.

* **Rendering the Progress**:
I created a custom drawable that takes the progress to update it and set this drawable to the target image view.
Calculating the image size was a challenge and there is still a problem with images smaller than the target image view, the image is distorted due to stretching to fit the bigger size.
Solving this problem would require adding scale types support to the library where i can select to center smaller images inside the target image view without stretching.   

* **Threading & Canceling Execution**:
Of course there should be threading to handle long running operations such as downloading, decoding, transforming, and caching.
I was confused between **RxJava** and **ThreadExecutor** but i decided to continue without third party libraries and hence i selected **ThreadExecutor**.
I created task for loading the image that run on worker thread and others for updating UI.
To cancel loading image tasks i keep track of Future objects returned when submitting task to Executor.
Then i just call cancel to interrupt the thread of a task and there i was able check for thread interrupted flag then exit the thread with proper error.  

* **Caching**:
This was an exciting part where i have to implement my own cache inspired by LRU cache, 
so i created a common base **Cache** class which extended by both **MemoryCache** and **DiskCache**.
To assure quality of this module i wrote some tests for the **DiskCache** which implicitly tests also **MemoryCache** because it tests the common logic in **Cache** base class.

* **Testing**
I implemented the sample view required in the main activity but i added also a button to open another testing view to test simultaneous loading of images.
This view is a simple GridView that displays only images in its items.  

## Assumptions
* Library only load images from server urls which means local url are not supported
* Avatar always has fixed image size which means for same image view all images loaded is of the same size

## Missing
* Write more tests
* Handle ExifOrientation
* Add support for ScaleTypes
* Support 2 gradient progress color  
