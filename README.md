# Personal data collector

## What is this?

This is a solution to a technical assignment provided by Veriff. 
This Android SDK allows users (host application developers) to capture a photo of their user and extract text from their ID card. 

## How does it work?

The SDK provides a single CameraActivity. 
Developers can launch it from their host application via a FaceDetectionResultContract or a TextRecognitionResultContract. 
When SDK flow ends, they receive either a null (if user refused to provide data) or the requested data.
For TextRecognitionResultContract the host application receives recognized text from a picture taken by user.
For FaceDetectionResultContract the host application receives File object pointing to a JPEG file with photo of user's face.


## How to install it?

Clone the repository and add dependency on sdk like that:

```
    implementation project(":sdk")
```

If you want to assess the quality of the SDK, download the latest demo host application from releases page.

## What can be improved?

The SDK isn't tested good enough, there are a lot of things that need to be covered.
That includes instrumented (UI) tests via Espresso that run on a device.
