# YouTube Transcript Android
[![](https://jitpack.io/v/hungnb94/youtube-transcript-anndroid.svg)](https://jitpack.io/#hungnb94/youtube-transcript-anndroid)

Retrieve subtitles/transcripts for a YouTube video.
It supports manual and automatically generated subtitles, bulk transcript retrieval for all videos in the playlist or on the channel and does not use headless browser for scraping.

Inspired by Python library: [jdepoix/youtube-transcript-api](https://github.com/jdepoix/youtube-transcript-api)

Java version: [Thoroldvix/youtube-transcript-api](https://github.com/Thoroldvix/youtube-transcript-api)

## Installation

**Step 1.** Add the JitPack repository to your build.gradle file

```groovy
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri('https://jitpack.io') } // Add this line
    }
}
```

**Step 2.** Add the dependency

```groovy
dependencies {
    implementation("com.github.hungnb94:youtube-transcript-anndroid:[LATEST_VERSION]")
}
```


## Usage
To start using YouTube Transcript API, you need to create an instance of `YoutubeTranscriptApi` by calling `createWithClient` method of `TranscriptApiFactory`.
Then you can call `listTranscripts` to get a list of all available transcripts for a video:
```java
YoutubeClient youtubeClient = new OkHttpYoutubeClient(new OkHttpClient());
YoutubeTranscriptApi youtubeTranscriptApi = TranscriptApiFactory.createWithClient(youtubeClient);
TranscriptList transcriptList = youtubeTranscriptApi.listTranscripts("videoId");
```

`TranscripList` is an iterable which contains all available transcripts for a video and provides methods for finding specific transcripts by language or by type (manual or automatically generated).
```java
TranscriptList transcriptList = youtubeTranscriptApi.listTranscripts("videoId");

// Iterate over transcript list
for (Transcript transcript : transcriptList) {
System.out.println(transcript);
}

// Find transcript in specific language
Transcript transcript = transcriptList.findTranscript("en");

// Find manually created transcript
Transcript manualyCreatedTranscript = transcriptList.findManualTranscript("en");

// Find automatically generated transcript
Transcript automaticallyGeneratedTranscript = transcriptList.findGeneratedTranscript("en");
```


`Transcript` object contains transcript metadata and provides methods for translating the transcript to another language and fetching the actual content of the transcript.
```java
Transcript transcript = transcriptList.findTranscript("en");

// Translate transcript to another language
Transcript translatedTranscript = transcript.translate("de");

// Retrieve transcript content
TranscriptContent transcriptContent = transcript.fetch();
```

You can also use `getTranscript`:
```java
TranscriptContent transcriptContent = youtubeTranscriptApi.getTranscript("videoId", "en");
```

This is equivalent to:
```java
TranscriptContent transcriptContent = youtubeTranscriptApi.listTranscripts("videoId")
        .findTranscript("en")
        .fetch();
```

Given that English is the most common language, you can omit the language code, and it will default to English:
```java
// Retrieve transcript content in english
TranscriptContent transcriptContent = youtubeTranscriptApi.listTranscripts("videoId")
        //no language code defaults to english
        .findTranscript()
        .fetch();
// Or
TranscriptContent transcriptContent = youtubeTranscriptApi.getTranscript("videoId");
```


### Use fallback language
In case if desired language is not available, instead of getting an exception you can pass some other languages that will be used as a fallback.
```java
TranscriptContent transcriptContent = youtubeTranscriptApi.listTranscripts("videoId")
        .findTranscript("de", "en")
        .fetch();

// Or
TranscriptContent transcriptContent = youtubeTranscriptApi.getTranscript("videoId", "de", "en");
```

### Transcript metadata
`Transcript` object contains several methods for retrieving transcript metadata:
```java
String videoId = transcript.getVideoId();

String language = transcript.getLanguage();

String languageCode = transcript.getLanguageCode();

// API URL used to fetch transcript content
String apiUrl = transcript.getApiUrl();

// Whether it has been manually created or automatically generated by YouTube
boolean isGenerated = transcript.isGenerated();

// Whether this transcript can be translated or not
boolean isTranslatable = transcript.isTranslatable();

// Set of language codes which represent available translation languages
Set<String> translationLanguages = transcript.getTranslationLanguages();
```

### Use Formatters
By default, if you try to print `TranscriptContent` it will return the following string representation:
```
content=[{text='Text',start=0.0,dur=1.54},{text='Another text',start=1.54,dur=4.16}]
```
Since this default format may not be suitable for all scenarios, you can implement the TranscriptFormatter interface to customize the formatting of the content.\
```java
// Create a new custom formatter
Formatter transcriptFormatter = new MyCustomFormatter();

// Format transcript content
String formattedContent = transcriptFormatter.format(transcriptContent);
```

The library offers several built-in formatters:
- `JSONFormatter` - Formats content as JSON
- `JSONPrettyFormatter` - Formats content as pretty-printed JSON
- `TextFormatter` - Formats content as plain text without timestamps
- `WebVTTFormatter` - Formats content as WebVTT
- `SRTFormatter` - Formats content as SRT

These formatters can be accessed from the `TranscriptFormatters` class:
```java
// Get json formatter
TranscriptFormatter jsonFormatter = TranscriptFormatters.jsonFormatter();

String formattedContent = jsonFormatter.format(transcriptContent);
```
